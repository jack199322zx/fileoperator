package test2;

import demo.DownFileSplitterFetch;
import demo.DownFileUtility;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * @author ss
 * @date 2018/8/24 13:59
 */
@Slf4j
public class UrlEngine implements Engine {

    private List<Pos> posList = new ArrayList<>();
    private boolean stop = false; // 停止标志
    private boolean firstRead = true;
    private Scheduler scheduler;
    private AbstractFileOperator fileOperator;
    private Queue<UrlTask> queue = new LinkedBlockingDeque<>();

    public UrlEngine(@NonNull Scheduler scheduler) {
        this.scheduler = scheduler;
        this.fileOperator = new UrlOperator(scheduler);
    }

    @Override
    public void begin() {
        try {
            if (fileOperator.ifExistTempFile()) {
                this.posList = fileOperator.readPos();
                this.firstRead = false;
            } else this.posList = fileOperator.initPosWhenFirstRead();

            // 启动子线程
            for (int i = 0; i < fileOperator.getSplitNum(); i++) {
                UrlTask t = new UrlTask(scheduler, this.posList.get(i), i);
                queue.offer(t);
                t.start();
            }
            // 下载子线程是否完成标志
            boolean breakWhile;
            while (!stop) {
                fileOperator.writePos(posList);
                TimeUnit.MILLISECONDS.sleep(500);
                breakWhile = true;
                int size = queue.size();
                while (size-- > 0) {
                    if (queue.poll().isOver()) {
                        breakWhile = false;
                        break;
                    }else{
                        fileOperator.writePos(posList);
                    }
                }
                if (breakWhile){
                    break;
                }
            }
            fileOperator.mergeFile();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    class UrlTask extends Thread implements Task{

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
//            DownFileUtility.log("Thread " + nThreadID + " url down filesize is "+(nEndPos-nStartPos));
//            DownFileUtility.log("Thread " + nThreadID + " url start >> "+nStartPos +"------end >> "+nEndPos);
            checkFirstRead();
            download();
        }

        @Override
        public void download() {
            while (start < end && !stop) {
                try {
                    URL url = new URL(this.url);
                    HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                    httpConnection.setRequestProperty("User-Agent", "NetFox");
                    String sProperty = "bytes=" + start + "-";
                    httpConnection.setRequestProperty("RANGE", sProperty);
//                    DownFileUtility.log(sProperty);
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
//                    DownFileUtility.log("Thread " + nThreadID + " nStartPos : "+nStartPos);
                    accessFile.close();
//                    DownFileUtility.log("Thread " + nThreadID + " is over!");
                    over = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(!over){
                if(start >= end){
                    over = true;
                }
            }
        }

        @SneakyThrows
        private void checkFirstRead() {
            if(!firstRead){
                accessFile.seek(currentTempFile.length());
            }
        }
    }
}
