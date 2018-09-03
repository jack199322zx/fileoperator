package engine;

import lombok.*;
import scheduler.Scheduler;
import operator.LocalOperator;
import model.Pos;
import model.Printer;
import task.Task;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @author ss
 * @date 2018/8/24 14:00
 */
public class LocalEngine extends AbstractEngine implements Engine {

    private Scheduler scheduler;

    public LocalEngine(@NonNull Scheduler scheduler) {
        super(new LocalOperator(scheduler));
        this.scheduler = scheduler;
    }

    @Override
    protected void startEngine() {
        // 启动子线程
        for (int i = 0; i < fileOperator.getSplitNum(); i++) {
            LocalTask t = new LocalTask(this.posList.get(i), i);
            queue.offer(t);
            t.start();
        }
    }

    class LocalTask extends Thread implements Task {

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
        public LocalTask(Pos pos, int threadId) {
            this.threadId = threadId;
            this.start = pos.getStartPos();
            this.end = pos.getEndPos();
            this.currentTempFile = new File(fileOperator.getTempFileName() + "_" + threadId);
            this.accessFile = new RandomAccessFile(currentTempFile,"rw");
        }

        @Override
        public void download() {
            while (start < end && !stop) {
                running = true;
                try {
                    RandomAccessFile input = new RandomAccessFile(scheduler.getDownFile(),"r");
                    input.seek(start);
                    byte[] b = new byte[1024];
                    int nRead;
                    while ((nRead = input.read(b, 0, 1024)) > 0
                            && start < end && !stop) {
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

        @Override
        public boolean isRunning() {
            return false;
        }

        @Override
        public void run() {
            checkFirstRead();
            download();
        }

        @SneakyThrows
        private void checkFirstRead() {
            if(!firstRead){
                accessFile.seek(currentTempFile.length());
            }
        }
    }
}
