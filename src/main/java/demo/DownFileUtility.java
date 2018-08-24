package demo;

/**
 * @author ss
 * @date 2018/8/23 14:27
 */
public class DownFileUtility {
    public DownFileUtility() {
    }

    /**
     * 休眠时长
     * @param nSecond
     */
    public static void sleep(int nSecond) {
        try {
            Thread.sleep(nSecond);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印日志信息
     * @param sMsg
     */
    public static void log(String sMsg) {
        System.err.println(sMsg);
    }

    /**
     * 打印日志信息
     * @param sMsg
     */
    public static void log(int sMsg) {
        System.err.println(sMsg);
    }
}
