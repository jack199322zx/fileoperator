package demo;

/**
 * @author ss
 * @date 2018/8/23 14:28
 */
public class Test {
    public Test() {
        try {
            DownFileInfoBean bean = new DownFileInfoBean(
                    "https://mirrors.cnnic.cn/apache/tomcat/tomcat-7/v7.0.90/bin/apache-tomcat-7.0.90.zip", "aa",
                    "apache-tomcat-7.0.90.zip", 5,true,null);
            /*File file = new File("D:\\dan07.apk");
            DownFileInfoBean bean = new DownFileInfoBean(null, "D:\\temp",
                    "dan07.apk", 3,false,file);*/
            DownFileFetch fileFetch = new DownFileFetch(bean);
            fileFetch.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Test();
    }
}
