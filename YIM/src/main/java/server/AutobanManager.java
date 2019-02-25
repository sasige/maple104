package server;

import client.MapleClient;
import handling.login.LoginServer;
import handling.world.World.Broadcast;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;
import tools.MaplePacketCreator;

public class AutobanManager implements Runnable {

    private Map<Integer, Integer> points = new HashMap();
    private Map<Integer, List<String>> reasons = new HashMap();
    private Set<ExpirationEntry> expirations = new TreeSet();
    private static int AUTOBAN_POINTS = 5000;
    private static AutobanManager instance = new AutobanManager();
    private ReentrantLock lock = new ReentrantLock(true);
    private static final Logger log = Logger.getLogger(AutobanManager.class);

    public static AutobanManager getInstance() {
        return instance;
    }

    public void autoban(MapleClient c, String reason) {
        if (c.getPlayer() == null) {
            return;
        }
        if ((c.getPlayer().isGM()) || (c.getPlayer().isClone())) {
            c.getPlayer().dropMessage(5, new StringBuilder().append("[警告] A/b 触发: ").append(reason).toString());
            return;
        }
        addPoints(c, AUTOBAN_POINTS, 0L, reason);
    }

    public void addPoints(MapleClient c, int points, long expiration, String reason) {
        this.lock.lock();
        try {
            int acc = c.getPlayer().getAccountID();

            if (this.points.containsKey(Integer.valueOf(acc))) {
                int SavedPoints = ((Integer) this.points.get(Integer.valueOf(acc))).intValue();
                if (SavedPoints >= AUTOBAN_POINTS) {
                    return;
                }
                this.points.put(Integer.valueOf(acc), Integer.valueOf(SavedPoints + points));
                List reasonList = (List) this.reasons.get(Integer.valueOf(acc));
                reasonList.add(reason);
            } else {
                this.points.put(Integer.valueOf(acc), Integer.valueOf(points));
                List reasonList = new LinkedList();
                reasonList.add(reason);
                this.reasons.put(Integer.valueOf(acc), reasonList);
            }

            if (((Integer) this.points.get(Integer.valueOf(acc))).intValue() >= AUTOBAN_POINTS) {
                log.info(new StringBuilder().append("[作弊] 玩家 ").append(c.getPlayer().getName()).append(" A/b 触发 ").append(reason).toString());
                if ((c.getPlayer().isGM()) || (c.getPlayer().isClone())) {
                    c.getPlayer().dropMessage(5, new StringBuilder().append("[警告] A/b 触发 : ").append(reason).toString());
                    return;
                }
                StringBuilder sb = new StringBuilder("A/b ");
                sb.append(c.getPlayer().getName());
                sb.append(" (IP ");
                sb.append(c.getSession().getRemoteAddress().toString());
                sb.append("): ");
                for (final String s : this.reasons.get(acc)) {
                    sb.append(s);
                    sb.append(", ");
                }
                Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, new StringBuilder().append(" <").append(c.getPlayer().getName()).append("> 被系统封号 (原因: ").append(reason).append(")").toString()));

                
                
        if (!LoginServer.isMaxDamage()) {
          Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, new StringBuilder().append(" <").append(c.getPlayer().getName()).append("> 被系统封号 (原因: ").append(reason).append(")").toString()));

       //   c.getPlayer().ban(sb.toString(), false, true, false);
            }
                
                c.getPlayer().ban(sb.toString(), false, true, false);
            } else if (expiration > 0L) {
                this.expirations.add(new ExpirationEntry(System.currentTimeMillis() + expiration, acc, points));
            }
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        for (ExpirationEntry e : this.expirations) {
            if (e.time <= now) {
                this.points.put(Integer.valueOf(e.acc), Integer.valueOf(((Integer) this.points.get(Integer.valueOf(e.acc))).intValue() - e.points));
            } else {
                return;
            }
        }
    }

    private static class ExpirationEntry
            implements Comparable<ExpirationEntry> {

        public long time;
        public int acc;
        public int points;

        public ExpirationEntry(long time, int acc, int points) {
            this.time = time;
            this.acc = acc;
            this.points = points;
        }

        @Override
        public int compareTo(ExpirationEntry o) {
            return (int) (this.time - o.time);
        }

        @Override
        public boolean equals(Object oth) {
            if (!(oth instanceof ExpirationEntry)) {
                return false;
            }
            ExpirationEntry ee = (ExpirationEntry) oth;
            return (this.time == ee.time) && (this.points == ee.points) && (this.acc == ee.acc);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 97 * hash + (int) (this.time ^ (this.time >>> 32));
            hash = 97 * hash + this.acc;
            hash = 97 * hash + this.points;
            return hash;
        }
    }
}