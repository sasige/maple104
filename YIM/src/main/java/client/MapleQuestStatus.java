package client;

import constants.GameConstants;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import server.life.MapleLifeFactory;
import server.quest.MapleQuest;

public final class MapleQuestStatus implements Serializable {

    private static final long serialVersionUID = 91795419934134L;
    private transient MapleQuest quest;
    private byte status;
    private Map<Integer, Integer> killedMobs = null;
    private int npc;
    private long completionTime;
    private int forfeited = 0;
    private String customData;

    public MapleQuestStatus(MapleQuest quest, int status) {
        this.quest = quest;
        setStatus((byte) status);
        this.completionTime = System.currentTimeMillis();
        if (status == 1) {
            if (quest.getRelevantMobs().isEmpty()) {
                registerMobs();
            }
        }
    }

    public MapleQuestStatus(MapleQuest quest, byte status, int npc) {
        this.quest = quest;
        setStatus(status);
        setNpc(npc);
        this.completionTime = System.currentTimeMillis();
        if (status == 1) {
            if (!quest.getRelevantMobs().isEmpty()) {
                registerMobs();
            }
        }
    }

    public void setQuest(int qid) {
        this.quest = MapleQuest.getInstance(qid);
    }

    public MapleQuest getQuest() {
        return this.quest;
    }

    public byte getStatus() {
        return this.status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public int getNpc() {
        return this.npc;
    }

    public void setNpc(int npc) {
        this.npc = npc;
    }

    public boolean isCustom() {
        return GameConstants.isCustomQuest(this.quest.getId());
    }

    private void registerMobs() {
        killedMobs = new LinkedHashMap<>();
        for (final int i : quest.getRelevantMobs().keySet()) {
            this.killedMobs.put(i, 0);
        }
    }

    private int maxMob(int mobid) {
        for (Entry<Integer, Integer> qs : this.quest.getRelevantMobs().entrySet()) {
            if (qs.getKey() == mobid) {
                return qs.getValue();
            }
        }
        return 0;
    }

    public boolean mobKilled(final int id, final int skillID) {
        if ((this.quest != null) && (this.quest.getSkillID() > 0) && (this.quest.getSkillID() != skillID)) {
            return false;
        }

        final Integer mob = killedMobs.get(id);
        if (mob != null) {
            final int mo = maxMob(id);
            if (mob >= mo) {
                return false;
            }
            this.killedMobs.put(id, Math.min(mob + 1, mo));
            return true;
        }
        for (Entry<Integer, Integer> mo : killedMobs.entrySet()) {
            if (questCount(mo.getKey(), id)) {
                int mobb = maxMob(mo.getKey().intValue());
                if (mo.getValue() >= mobb) {
                    return false;
                }
                this.killedMobs.put(mo.getKey(), Math.min(mo.getValue() + 1, mobb));
                return true;
            }
        }
        return false;
    }

    private boolean questCount(int mo, int id) {
        if (MapleLifeFactory.getQuestCount(mo) != null) {
            for (int i : MapleLifeFactory.getQuestCount(mo)) {
                if (i == id) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setMobKills(int id, int count) {
        if (this.killedMobs == null) {
            registerMobs();
        }
        this.killedMobs.put(id, count);
    }

    public boolean hasMobKills() {
        if (this.killedMobs == null) {
            return false;
        }
        return this.killedMobs.size() > 0;
    }

    public int getMobKills(int id) {
        Integer mob = this.killedMobs.get(id);
        if (mob == null) {
            return 0;
        }
        return mob;
    }

    public Map<Integer, Integer> getMobKills() {
        return this.killedMobs;
    }

    public long getCompletionTime() {
        return this.completionTime;
    }

    public void setCompletionTime(long completionTime) {
        this.completionTime = completionTime;
    }

    public int getForfeited() {
        return this.forfeited;
    }

    public void setForfeited(int forfeited) {
        if (forfeited >= this.forfeited) {
            this.forfeited = forfeited;
        } else {
            throw new IllegalArgumentException("Can't set forfeits to something lower than before.");
        }
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }

    public String getCustomData() {
        return this.customData;
    }

    public boolean isDailyQuest() {
        switch (this.quest.getId()) {
            case 11463:
            case 11464:
            case 11465:
            case 11468:
                return true;
            case 11466:
            case 11467:
        }
        return false;
    }
}