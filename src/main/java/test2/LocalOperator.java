package test2;

import java.io.File;

/**
 * @author ss
 * @date 2018/8/24 14:42
 */
public class LocalOperator extends AbstractFileOperator {

    private File downFile;

    public LocalOperator(Scheduler scheduler) {
        super(scheduler);
        this.downFile = scheduler.getDownFile();
    }

    @Override
    protected long getFileSize() {
        File localFile = this.downFile;
        return localFile.length();
    }
}
