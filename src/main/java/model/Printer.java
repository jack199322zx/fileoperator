package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ss
 * @date 2018/9/3 17:16
 */
public class Printer {
    public static final Logger logger = LoggerFactory.getLogger(Printer.class);

    public static void warn(String str) {
        logger.warn(str);
    }
    public static void warn(String str, Object... obj) {
        logger.warn(str, obj);
    }
    public static void debug(String str) {
        logger.debug(str);
    }
    public static void debug(String str, Object... obj) {
        logger.debug(str, obj);
    }
    public static void info(String str, Object... obj) {
        logger.info(str, obj);
    }
    public static void info(String str) {
        logger.info(str);
    }
}
