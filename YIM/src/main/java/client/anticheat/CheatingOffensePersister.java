package client.anticheat;

import database.DatabaseConnection;
import java.sql.*;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import server.Timer;

public class CheatingOffensePersister {

    private static final CheatingOffensePersister instance = new CheatingOffensePersister();
    private final Set<CheatingOffenseEntry> toPersist = new LinkedHashSet();
    private final Lock mutex = new ReentrantLock();

    private CheatingOffensePersister() {
        Timer.CheatTimer.getInstance().register(new PersistingTask(), 61000L);
    }

    public static CheatingOffensePersister getInstance() {
        return instance;
    }

    public void persistEntry(CheatingOffenseEntry coe) {
        this.mutex.lock();
        try {
            this.toPersist.remove(coe);
            this.toPersist.add(coe);
        } finally {
            this.mutex.unlock();
        }
    }

    public class PersistingTask implements Runnable {

        public PersistingTask() {
        }

        @Override
        public void run() {
            CheatingOffensePersister.this.mutex.lock();
            CheatingOffenseEntry[] offenses;
            try {
                offenses = (CheatingOffenseEntry[]) CheatingOffensePersister.this.toPersist.toArray(new CheatingOffenseEntry[CheatingOffensePersister.this.toPersist.size()]);
                CheatingOffensePersister.this.toPersist.clear();
            } finally {
                CheatingOffensePersister.this.mutex.unlock();
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement insertps = con.prepareStatement("INSERT INTO cheatlog (characterid, offense, count, lastoffensetime, param) VALUES (?, ?, ?, ?, ?)", 1);
                PreparedStatement updateps = con.prepareStatement("UPDATE cheatlog SET count = ?, lastoffensetime = ?, param = ? WHERE id = ?");
                for (CheatingOffenseEntry offense : offenses) {
                    String parm = offense.getParam() == null ? "" : offense.getParam();
                    if (offense.getDbId() == -1) {
                        insertps.setInt(1, offense.getChrfor());
                        insertps.setString(2, offense.getOffense().name());
                        insertps.setInt(3, offense.getCount());
                        insertps.setTimestamp(4, new Timestamp(offense.getLastOffenseTime()));
                        insertps.setString(5, parm);
                        insertps.executeUpdate();
                        ResultSet rs = insertps.getGeneratedKeys();
                        if (rs.next()) {
                            offense.setDbId(rs.getInt(1));
                        }
                        rs.close();
                    } else {
                        updateps.setInt(1, offense.getCount());
                        updateps.setTimestamp(2, new Timestamp(offense.getLastOffenseTime()));
                        updateps.setString(3, parm);
                        updateps.setInt(4, offense.getDbId());
                        updateps.executeUpdate();
                    }
                }
                insertps.close();
                updateps.close();
            } catch (SQLException e) {
                System.err.println("error persisting cheatlog" + e);
            }
        }
    }
}
