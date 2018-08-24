package test2;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author ss
 * @date 2018/8/24 14:00
 */
public class LocalEngine implements Engine {

    private List<Pos> posList = new ArrayList<>();
    private boolean stop = false; // 停止标志
    private boolean firstRead = true;
    private Scheduler scheduler;
    private AbstractFileOperator fileOperator;
    private Queue<LocalTask> queue = new LinkedBlockingDeque<>();

    public LocalEngine(Scheduler scheduler) {

    }

    @Override
    public void begin() {

    }

    class LocalTask {

    }
}
