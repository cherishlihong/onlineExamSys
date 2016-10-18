package com.ifox.framework.common;

/**
 * Created by Administrator on 2016/10/8.
 */

import org.slf4j.spi.LocationAwareLogger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class LogUtil {
    private static final String LOG_FIELD_DELIMITER = " | ";
    private static int levelInt = 0;

    public static boolean isDebugEnabled(Class<?> c) {
        return getLogger(c.getName()).isDebugEnabled();
    }

    public static boolean isTraceEnabled(Class<?> c) {
        return getLogger(c.getName()).isTraceEnabled();
    }

    public static boolean isInfoEnabled(Class<?> c) {
        return getLogger(c.getName()).isInfoEnabled();
    }

    public static Logger getLogger(String c) {
        return (Logger) LoggerFactory.getLogger(c);
    }

    public static void debug(Class<?> clazz, String method, String format, Object... args) {
        writeLog(Level.DEBUG, clazz, method, format, args);
    }

    @SuppressWarnings("static-access")
    private static int toLevel(Level level) {

        if (level.equals(Level.TRACE)) {
            return LocationAwareLogger.TRACE_INT;
        }
        if (level.equals(level.DEBUG)) {
            return LocationAwareLogger.DEBUG_INT;
        }
        if (level.equals(level.INFO)) {
            return LocationAwareLogger.INFO_INT;
        }
        if (level.equals(level.WARN)) {
            return LocationAwareLogger.WARN_INT;
        }
        if (level.equals(level.ERROR)) {
            return LocationAwareLogger.ERROR_INT;
        }
        return LocationAwareLogger.ERROR_INT;
    }

    private static void writeLog(Level level, Class<?> clazz, String method, String format,
                                 Object... args) {
        if (null != clazz && null != format) {
            populateMDC();
            StringBuffer buffer = new StringBuffer();
            buffer.append(clazz.getSimpleName());
            buffer.append(".");
            buffer.append(method);
            buffer.append(LOG_FIELD_DELIMITER);
            if (args != null && args.length > 0)
                buffer.append(String.format(format, args));
            else
                buffer.append(format);
            String message = buffer.toString();
            Logger logger = LogUtil.getLogger(clazz.getName());
            levelInt = toLevel(level);
            logger.log(null, null, levelInt, message, null, null);
        }
    }

    private static void populateMDC() {
        MDC.put("threadId", String.valueOf(Thread.currentThread().getId()));
    }

}

