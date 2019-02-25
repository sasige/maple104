package handling.channel;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import handling.world.CharacterTransfer;
import handling.world.CheaterData;
import handling.world.World;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.Timer;

public class PlayerStorage {

    private final ReentrantReadWriteLock mutex = new ReentrantReadWriteLock();
    private final Lock rL = this.mutex.readLock();
    private final Lock wL = this.mutex.writeLock();
    private final ReentrantReadWriteLock mutex2 = new ReentrantReadWriteLock();
    private final Lock rL2 = this.mutex2.readLock();
    private final Lock wL2 = this.mutex2.writeLock();
    private final Map<String, MapleCharacter> nameToChar = new HashMap();
    private final Map<Integer, MapleCharacter> idToChar = new HashMap();
    private final Map<Integer, CharacterTransfer> PendingCharacter = new HashMap();
    private int channel;

    public PlayerStorage(int channel) {
        this.channel = channel;

        Timer.PingTimer.getInstance().register(new PersistingTask(), 60000L);
    }

    public final ArrayList<MapleCharacter> getAllCharacters() {
        rL.lock();
        try {
            return new ArrayList<MapleCharacter>(idToChar.values());
        } finally {
            rL.unlock();
        }
    }

    public void registerPlayer(MapleCharacter chr) {
        this.wL.lock();
        try {
            this.nameToChar.put(chr.getName().toLowerCase(), chr);
            this.idToChar.put(Integer.valueOf(chr.getId()), chr);
        } finally {
            this.wL.unlock();
        }
        World.Find.register(chr.getId(), chr.getName(), this.channel);
    }

    public void registerPendingPlayer(CharacterTransfer chr, int playerid) {
        this.wL2.lock();
        try {
            this.PendingCharacter.put(Integer.valueOf(playerid), chr);
        } finally {
            this.wL2.unlock();
        }
    }

    public void deregisterPlayer(MapleCharacter chr) {
        this.wL.lock();
        try {
            this.nameToChar.remove(chr.getName().toLowerCase());
            this.idToChar.remove(Integer.valueOf(chr.getId()));
        } finally {
            this.wL.unlock();
        }
        World.Find.forceDeregister(chr.getId(), chr.getName());
    }

    public void deregisterPlayer(int idz, String namez) {
        this.wL.lock();
        try {
            this.nameToChar.remove(namez.toLowerCase());
            this.idToChar.remove(Integer.valueOf(idz));
        } finally {
            this.wL.unlock();
        }
        World.Find.forceDeregister(idz, namez);
    }

    public int pendingCharacterSize() {
        return this.PendingCharacter.size();
    }

    public void deregisterPendingPlayer(int charid) {
        this.wL2.lock();
        try {
            this.PendingCharacter.remove(Integer.valueOf(charid));
        } finally {
            this.wL2.unlock();
        }
    }

    public final CharacterTransfer getPendingCharacter(final int charid) {
        wL2.lock();
        try {
            return PendingCharacter.remove(charid);
        } finally {
            wL2.unlock();
        }
    }

    public final MapleCharacter getCharacterByName(final String name) {
        rL.lock();
        try {
            return nameToChar.get(name.toLowerCase());
        } finally {
            rL.unlock();
        }
    }

    public final MapleCharacter getCharacterById(final int id) {
        rL.lock();
        try {
            return idToChar.get(id);
        } finally {
            rL.unlock();
        }
    }

    public int getConnectedClients() {
        return this.idToChar.size();
    }

    public List<CheaterData> getCheaters() {
        List cheaters = new ArrayList();
        this.rL.lock();
        try {
            Iterator itr = this.nameToChar.values().iterator();

            while (itr.hasNext()) {
                MapleCharacter chr = (MapleCharacter) itr.next();
                if (chr.getCheatTracker().getPoints() > 0) {
                    cheaters.add(new CheaterData(chr.getCheatTracker().getPoints(), new StringBuilder().append(MapleCharacterUtil.makeMapleReadable(chr.getName())).append(" (").append(chr.getCheatTracker().getPoints()).append(") ").append(chr.getCheatTracker().getSummary()).toString()));
                }
            }
        } finally {
            this.rL.unlock();
        }
        return cheaters;
    }

    public List<CheaterData> getReports() {
        List cheaters = new ArrayList();
        this.rL.lock();
        try {
            Iterator itr = this.nameToChar.values().iterator();

            while (itr.hasNext()) {
                MapleCharacter chr = (MapleCharacter) itr.next();
                if (chr.getReportPoints() > 0) {
                    cheaters.add(new CheaterData(chr.getReportPoints(), new StringBuilder().append(MapleCharacterUtil.makeMapleReadable(chr.getName())).append(" (").append(chr.getReportPoints()).append(") ").append(chr.getReportSummary()).toString()));
                }
            }
        } finally {
            this.rL.unlock();
        }
        return cheaters;
    }

    public void disconnectAll() {
        disconnectAll(false);
    }

    public void disconnectAll(boolean checkGM) {
        this.wL.lock();
        try {
            Iterator itr = this.nameToChar.values().iterator();

            while (itr.hasNext()) {
                MapleCharacter chr = (MapleCharacter) itr.next();
                if ((!chr.isGM()) || (!checkGM)) {
                    chr.getClient().disconnect(false, false, true);
                    chr.getClient().getSession().close();
                    World.Find.forceDeregister(chr.getId(), chr.getName());
                    itr.remove();
                }
            }
        } finally {
            this.wL.unlock();
        }
    }

    public String getOnlinePlayers(boolean byGM) {
        StringBuilder sb = new StringBuilder();
        if (byGM) {
            this.rL.lock();
            try {
                Iterator itr = this.nameToChar.values().iterator();
                while (itr.hasNext()) {
                    sb.append(MapleCharacterUtil.makeMapleReadable(((MapleCharacter) itr.next()).getName()));
                    sb.append(", ");
                }
            } finally {
                this.rL.unlock();
            }
        } else {
            this.rL.lock();
            try {
                Iterator itr = this.nameToChar.values().iterator();

                while (itr.hasNext()) {
                    MapleCharacter chr = (MapleCharacter) itr.next();
                    if (!chr.isGM()) {
                        sb.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                        sb.append(", ");
                    }
                }
            } finally {
                this.rL.unlock();
            }
        }
        return sb.toString();
    }

    public void broadcastPacket(byte[] data) {
        this.rL.lock();
        try {
            Iterator itr = this.nameToChar.values().iterator();
            while (itr.hasNext()) {
                ((MapleCharacter) itr.next()).getClient().getSession().write(data);
            }
        } finally {
            this.rL.unlock();
        }
    }

    public void broadcastSmegaPacket(byte[] data) {
        this.rL.lock();
        try {
            Iterator itr = this.nameToChar.values().iterator();

            while (itr.hasNext()) {
                MapleCharacter chr = (MapleCharacter) itr.next();
                if ((chr.getClient().isLoggedIn()) && (chr.getSmega())) {
                    chr.getClient().getSession().write(data);
                }
            }
        } finally {
            this.rL.unlock();
        }
    }

    public void broadcastGMPacket(byte[] data) {
        this.rL.lock();
        try {
            Iterator itr = this.nameToChar.values().iterator();

            while (itr.hasNext()) {
                MapleCharacter chr = (MapleCharacter) itr.next();
                if ((chr.getClient().isLoggedIn()) && (chr.isIntern())) {
                    chr.getClient().getSession().write(data);
                }
            }
        } finally {
            this.rL.unlock();
        }
    }

    public class PersistingTask implements Runnable {

        public PersistingTask() {
        }

        @Override
        public void run() {
            PlayerStorage.this.wL2.lock();
            try {
                long currenttime = System.currentTimeMillis();
                Iterator itr = PlayerStorage.this.PendingCharacter.entrySet().iterator();
                while (itr.hasNext()) {
                    if (currenttime - ((CharacterTransfer) ((Map.Entry) itr.next()).getValue()).TranferTime > 40000L) {
                        itr.remove();
                    }
                }
            } finally {
                PlayerStorage.this.wL2.unlock();
            }
        }
    }
}