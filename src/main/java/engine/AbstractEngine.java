package engine;

import lombok.SneakyThrows;
import operator.AbstractFileOperator;
import model.Pos;
import task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * @author ss
 * @date 2018/9/3 16:12
 */
public abstract class AbstractEngine implements Engine {

    protected AbstractFileOperator fileOperator;
    protected Queue<Task> queue = new LinkedBlockingDeque<>();
    protected List<Pos> posList = new ArrayList<>();
    protected boolean firstRead = true;
    protected boolean stop = false; // 停止标志

    public AbstractEngine(AbstractFileOperator fileOperator) {
        this.fileOperator = fileOperator;
    }

    private void beforePrepare() {
        if (fileOperator.ifExistTempFile()) {
            this.posList = fileOperator.readPos();
            this.firstRead = false;
        } else {
            this.posList = fileOperator.initPosWhenFirstRead();
        }
    }

    protected abstract void startEngine();

    @SneakyThrows
    private void savePosWhenRunning() {
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
    }

    private void mergeFile() {
        fileOperator.mergeFile();
    }

    @Override
    public void begin() {
        beforePrepare();
        startEngine();
        savePosWhenRunning();
        mergeFile();
    }
}
