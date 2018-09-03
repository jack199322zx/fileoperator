import engine.Engine;
import factory.EngineFactory;
import scheduler.Scheduler;

import java.io.File;

/**
 * @author ss
 * @date 2018/8/24 17:44
 */
public class Main {

    public static void main(String[] args) {
//        Engine engine = EngineFactory.create(new Scheduler("https://mirrors.cnnic.cn/apache/tomcat/tomcat-7/v7.0.90/bin/apache-tomcat-7.0.90.zip", "aa",
//                "apache-tomcat-7.0.90.zip", 5, Scheduler.DownloadType.URL,null));
        Engine engine = EngineFactory.create(new Scheduler(null, "aa",
                "apache-tomcat-7.0.90.zip", 5, Scheduler.DownloadType.LOCAL, new File("aa/apache-tomcat-7.0.90.zip")));
        engine.begin();
    }
}
