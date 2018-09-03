package factory;

import engine.Engine;
import engine.LocalEngine;
import engine.UrlEngine;
import scheduler.Scheduler;

/**
 * @author ss
 * @date 2018/8/24 14:25
 */
public class EngineFactory {

    public static Engine create(Scheduler scheduler) {
        if (Scheduler.DownloadType.LOCAL == scheduler.getDownloadType()) {
            return new LocalEngine(scheduler);
        } else if (Scheduler.DownloadType.URL == scheduler.getDownloadType()) {
            return new UrlEngine(scheduler);
        }
        return new UrlEngine(scheduler);
    }
}
