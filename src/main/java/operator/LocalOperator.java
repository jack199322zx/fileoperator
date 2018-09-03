package operator;

import lombok.Getter;
import operator.AbstractFileOperator;
import scheduler.Scheduler;

import java.io.File;

/**
 * @author ss
 * @date 2018/8/24 14:42
 */
public class LocalOperator extends AbstractFileOperator {

    @Getter
    private File target;

    public LocalOperator(Scheduler scheduler) {
        super(scheduler);
        this.target = scheduler.getDownFile();
    }

    @Override
    protected long getFileSize() {
        File localFile = this.target;
        return localFile.length();
    }

}
