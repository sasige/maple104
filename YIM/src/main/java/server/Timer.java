package server;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import tools.FileoutputUtil;

public abstract class Timer {

    private ScheduledThreadPoolExecutor ses;
    protected String file;
    protected String name;
    private static AtomicInteger threadNumber = new AtomicInteger(1);
    /*
    private static Timer instance = new Timer() {};
    private static Logger log = LoggerFactory.getLogger(Timer.class);
    
    
    	private Timer() {
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		try {
			mBeanServer.registerMBean(this, new ObjectName("net.sf.odinms.server:type=TimerManger"));
		} catch (Exception e) {
			log.error("错误登记数据服务.请检查数据库连接时间设置", e);
		}
	}

	public static Timer getInstances() {
		return instance;
	}
        * 
        */
    

    public void start() {
        if ((this.ses != null) && (!this.ses.isShutdown()) && (!this.ses.isTerminated())) {
            return;
        }
        this.file = ("Log_" + this.name + "_Except.rtf");
        this.ses = new ScheduledThreadPoolExecutor(5, new RejectedThreadFactory());
        this.ses.setKeepAliveTime(10L, TimeUnit.MINUTES);
        this.ses.allowCoreThreadTimeOut(true);
        this.ses.setMaximumPoolSize(8);
        this.ses.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
    }

    public ScheduledThreadPoolExecutor getSES() {
        return this.ses;
    }

    public void stop() {
        if (this.ses != null) {
            this.ses.shutdown();
        }
    }

    public ScheduledFuture<?> register(Runnable r, long repeatTime, long delay) {
        if (this.ses == null) {
            return null;
        }
        return this.ses.scheduleAtFixedRate(new LoggingSaveRunnable(r, this.file), delay, repeatTime, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> register(Runnable r, long repeatTime) {
        if (this.ses == null) {
            return null;
        }
        return this.ses.scheduleAtFixedRate(new LoggingSaveRunnable(r, this.file), 0L, repeatTime, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> schedule(Runnable r, long delay) {
        if (this.ses == null) {
            return null;
        }
        return this.ses.schedule(new LoggingSaveRunnable(r, this.file), delay, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> scheduleAtTimestamp(Runnable r, long timestamp) {
        return schedule(r, timestamp - System.currentTimeMillis());
    }

    private class RejectedThreadFactory implements ThreadFactory {

        private AtomicInteger threadNumber2 = new AtomicInteger(1);
        private String tname;

        public RejectedThreadFactory() {
            this.tname = (Timer.this.name + Randomizer.nextInt());
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName(this.tname + "-W-" + Timer.threadNumber.getAndIncrement() + "-" + this.threadNumber2.getAndIncrement());
            return t;
        }
    }

    private static class LoggingSaveRunnable
            implements Runnable {

        Runnable r;
        String file;

        public LoggingSaveRunnable(Runnable r, String file) {
            this.r = r;
            this.file = file;
        }

        @Override
        public void run() {
            try {
                this.r.run();
            } catch (Throwable t) {
                FileoutputUtil.outputFileError(this.file, t);
            }
        }
    }

    public static class PingTimer extends Timer {

        private static PingTimer instance = new PingTimer();

        private PingTimer() {
            this.name = "Pingtimer";
        }

        public static PingTimer getInstance() {
            return instance;
        }
    }

    public static class CheatTimer extends Timer {

        private static CheatTimer instance = new CheatTimer();

        private CheatTimer() {
            this.name = "Cheattimer";
        }

        public static CheatTimer getInstance() {
            return instance;
        }
    }

    public static class EtcTimer extends Timer {

        private static EtcTimer instance = new EtcTimer();

        private EtcTimer() {
            this.name = "Etctimer";
        }

        public static EtcTimer getInstance() {
            return instance;
        }
    }

    public static class CloneTimer extends Timer {

        private static CloneTimer instance = new CloneTimer();

        private CloneTimer() {
            this.name = "Clonetimer";
        }

        public static CloneTimer getInstance() {
            return instance;
        }
    }

    public static class EventTimer extends Timer {

        private static EventTimer instance = new EventTimer();

        private EventTimer() {
            this.name = "Eventtimer";
        }

        public static EventTimer getInstance() {
            return instance;
        }
    }

    public static class BuffTimer extends Timer {

        private static BuffTimer instance = new BuffTimer();

        private BuffTimer() {
            this.name = "Bufftimer";
        }

        public static BuffTimer getInstance() {
            return instance;
        }
    }

    public static class MapTimer extends Timer {

        private static MapTimer instance = new MapTimer();

        private MapTimer() {
            this.name = "Maptimer";
        }

        public static MapTimer getInstance() {
            return instance;
        }
    }

    public static class WorldTimer extends Timer {

        private static WorldTimer instance = new WorldTimer();

        private WorldTimer() {
            this.name = "Worldtimer";
        }

        public static WorldTimer getInstance() {
            return instance;
        }
    }
}