package tools;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class MinLogger {

    public static final int LEVEL_NONE = 6;
    public static final int LEVEL_ERROR = 5;
    public static final int LEVEL_WARN = 4;
    public static final int LEVEL_INFO = 3;
    public static final int LEVEL_DEBUG = 2;
    public static final int LEVEL_TRACE = 1;
    private static int level = 3;
    public static boolean ERROR = level <= 5;
    public static boolean WARN = level <= 4;
    public static boolean INFO = level <= 3;
    public static boolean DEBUG = level <= 2;
    public static boolean TRACE = level <= 1;
    private static Logger logger = new Logger();

    public static void set(int level) {
        level = level;
        ERROR = level <= 5;
        WARN = level <= 4;
        INFO = level <= 3;
        DEBUG = level <= 2;
        TRACE = level <= 1;
    }

    public static void NONE() {
        set(6);
    }

    public static void ERROR() {
        set(5);
    }

    public static void WARN() {
        set(4);
    }

    public static void INFO() {
        set(3);
    }

    public static void DEBUG() {
        set(2);
    }

    public static void TRACE() {
        set(1);
    }

    public static void setLogger(Logger logger) {
        logger = logger;
    }

    public static void error(String message, Throwable ex) {
        if (ERROR) {
            logger.log(5, null, message, ex);
        }
    }

    public static void error(String category, String message, Throwable ex) {
        if (ERROR) {
            logger.log(5, category, message, ex);
        }
    }

    public static void error(String message) {
        if (ERROR) {
            logger.log(5, null, message, null);
        }
    }

    public static void error(String category, String message) {
        if (ERROR) {
            logger.log(5, category, message, null);
        }
    }

    public static void warn(String message, Throwable ex) {
        if (WARN) {
            logger.log(4, null, message, ex);
        }
    }

    public static void warn(String category, String message, Throwable ex) {
        if (WARN) {
            logger.log(4, category, message, ex);
        }
    }

    public static void warn(String message) {
        if (WARN) {
            logger.log(4, null, message, null);
        }
    }

    public static void warn(String category, String message) {
        if (WARN) {
            logger.log(4, category, message, null);
        }
    }

    public static void info(String message, Throwable ex) {
        if (INFO) {
            logger.log(3, null, message, ex);
        }
    }

    public static void info(String category, String message, Throwable ex) {
        if (INFO) {
            logger.log(3, category, message, ex);
        }
    }

    public static void info(String message) {
        if (INFO) {
            logger.log(3, null, message, null);
        }
    }

    public static void info(String category, String message) {
        if (INFO) {
            logger.log(3, category, message, null);
        }
    }

    public static void debug(String message, Throwable ex) {
        if (DEBUG) {
            logger.log(2, null, message, ex);
        }
    }

    public static void debug(String category, String message, Throwable ex) {
        if (DEBUG) {
            logger.log(2, category, message, ex);
        }
    }

    public static void debug(String message) {
        if (DEBUG) {
            logger.log(2, null, message, null);
        }
    }

    public static void debug(String category, String message) {
        if (DEBUG) {
            logger.log(2, category, message, null);
        }
    }

    public static void trace(String message, Throwable ex) {
        if (TRACE) {
            logger.log(1, null, message, ex);
        }
    }

    public static void trace(String category, String message, Throwable ex) {
        if (TRACE) {
            logger.log(1, category, message, ex);
        }
    }

    public static void trace(String message) {
        if (TRACE) {
            logger.log(1, null, message, null);
        }
    }

    public static void trace(String category, String message) {
        if (TRACE) {
            logger.log(1, category, message, null);
        }
    }

    public static class Logger {

        private long firstLogTime = new Date().getTime();

        public void log(int level, String category, String message, Throwable ex) {
            StringBuilder builder = new StringBuilder(256);
            long time = new Date().getTime() - this.firstLogTime;
            long minutes = time / 60000L;
            long seconds = time / 1000L % 60L;
            if (minutes <= 9L) {
                builder.append('0');
            }
            builder.append(minutes);
            builder.append(':');
            if (seconds <= 9L) {
                builder.append('0');
            }
            builder.append(seconds);
            switch (level) {
                case 5:
                    builder.append(" ´íÎó: ");
                    break;
                case 4:
                    builder.append(" ¾¯¸æ: ");
                    break;
                case 3:
                    builder.append(" ÐÅÏ¢: ");
                    break;
                case 2:
                    builder.append(" DEBUG: ");
                    break;
                case 1:
                    builder.append(" TRACE: ");
            }

            if (category != null) {
                builder.append('[');
                builder.append(category);
                builder.append("] ");
            }

            builder.append(message);
            if (ex != null) {
                StringWriter writer = new StringWriter(256);
                ex.printStackTrace(new PrintWriter(writer));
                builder.append('\n');
                builder.append(writer.toString().trim());
            }
            print(builder.toString());
        }

        protected void print(String message) {
            System.out.println(message);
        }
    }
}