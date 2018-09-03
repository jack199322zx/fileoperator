package engine;

import lombok.*;
import scheduler.Scheduler;
import model.Pos;
import model.Printer;
import task.Task;
import operator.UrlOperator;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author ss
 * @date 2018/8/24 13:59
 */
public class UrlEngine extends AbstractEngine implements Engine {

    private Scheduler scheduler;

    public UrlEngine(@NonNull Scheduler scheduler) {
        super(new UrlOperator(scheduler));
        this.scheduler = scheduler;
    }

    @Override
    protected void startEngine() {
        // 启动子线程
        for (int i = 0; i < fileOperator.getSplitNum(); i++) {
            UrlTask t = new UrlTask(scheduler, this.posList.get(i), i);
            queue.offer(t);
            t.start();
        }
    }

    class UrlTask extends Thread implements Task {

        private String url; // 下载文件的地址
        @Getter
        private long start; // 文件分段的开始位置
        @Getter
        private long end; // 文件分段的结束位置
        private int threadId; // 线程ID
        private RandomAccessFile accessFile;
        private File currentTempFile;
        @Getter
        private boolean over; // 是否下载完成
        private boolean running; // 是否正在下载
        private boolean stop; // 停止下载

        @SneakyThrows
        public UrlTask(Scheduler scheduler, Pos pos, int threadId) {
            this.url = scheduler.getUrl();
            this.threadId = threadId;
            this.start = pos.getStartPos();
            this.end = pos.getEndPos();
            this.currentTempFile = new File(fileOperator.getTempFileName() + "_" + threadId);
            this.accessFile = new RandomAccessFile(currentTempFile,"rw");
        }

        @Override
        public void run() {
            checkFirstRead();
            download();
        }

        @Override
        public void download() {
            while (start < end && !stop) {
                running = true;
                try {
                    URL url = new URL(this.url);
                    HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                    httpConnection.setRequestProperty("User-Agent", "NetFox");
                    String sProperty = "bytes=" + start + "-";
                    httpConnection.setRequestProperty("RANGE", sProperty);
                    @Cleanup val input = httpConnection.getInputStream();
                    byte[] b = new byte[1024];
                    int nRead;
                    while ((nRead = input.read(b, 0, 1024)) > 0 && start < end && !stop) {
                        if((start + nRead) > end) {
                            nRead = (int)(end - start);
                        }
                        accessFile.write(b, 0, nRead);
                        start += nRead;
                    }
                    Printer.info("Thread:{} is running, startPos is {}", threadId, start);
                    accessFile.close();
                    Printer.info("Thread:{} is over", threadId);
                    over = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(!over){
                if(start >= end){
                    over = true;
                    running = false;
                }
            }
        }

        @SneakyThrows
        private void checkFirstRead() {
            if(!firstRead){
                accessFile.seek(currentTempFile.length());
            }
        }

        @Override
        public boolean isRunning() {
            return this.running;
        }

        @Override
        public boolean isOver() {
            return this.over;
        }

    }
}
