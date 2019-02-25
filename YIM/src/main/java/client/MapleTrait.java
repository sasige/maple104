package client;

import constants.GameConstants;
import tools.MaplePacketCreator;

public class MapleTrait {

    private MapleTraitType type;
    private int totalExp = 0;
    private int localTotalExp = 0;
    private short exp = 0;
    private byte level = 0;

    public MapleTrait(MapleTraitType t) {
        this.type = t;
    }

    public void setExp(int exp) {
        this.totalExp = exp;
        this.localTotalExp = exp;
        recalcLevel();
    }

    public void addExp(int exp) {
        this.totalExp += exp;
        this.localTotalExp += exp;
        if (exp != 0) {
            recalcLevel();
        }
    }

    public void addExp(int exp, MapleCharacter chr) {
        addTrueExp(exp * chr.getClient().getChannelServer().getTraitRate(), chr);
    }

    public void addTrueExp(int exp, MapleCharacter chr) {
        if (exp != 0) {
            this.totalExp += exp;
            this.localTotalExp += exp;
            chr.updateSingleStat(this.type.stat, this.totalExp);
            chr.getClient().getSession().write(MaplePacketCreator.showTraitGain(this.type, exp));
            recalcLevel();
        }
    }

    public boolean recalcLevel() {
        if (this.totalExp < 0) {
            this.totalExp = 0;
            this.localTotalExp = 0;
            this.level = 0;
            this.exp = 0;
            return false;
        }
        int oldLevel = this.level;
        for (byte i = 0; i < 100; i = (byte) (i + 1)) {
            if (GameConstants.getTraitExpNeededForLevel(i) > this.localTotalExp) {
                this.exp = (short) (GameConstants.getTraitExpNeededForLevel(i) - this.localTotalExp);
                this.level = (byte) (i - 1);
                return this.level > oldLevel;
            }
        }
        this.exp = 0;
        this.level = 100;
        this.totalExp = GameConstants.getTraitExpNeededForLevel(this.level);
        this.localTotalExp = this.totalExp;
        return this.level > oldLevel;
    }

    public int getLevel() {
        return this.level;
    }

    public int getExp() {
        return this.exp;
    }

    public int getTotalExp() {
        return this.totalExp;
    }

    public int getLocalTotalExp() {
        return this.localTotalExp;
    }

    public void addLocalExp(int exp) {
        this.localTotalExp += exp;
    }

    public void clearLocalExp() {
        this.localTotalExp = this.totalExp;
    }

    public MapleTraitType getType() {
        return this.type;
    }

    public static enum MapleTraitType {

        charisma(500, MapleStat.领袖),
        insight(500, MapleStat.洞察),
        will(500, MapleStat.意志),
        craft(500, MapleStat.手技),
        sense(500, MapleStat.感性),
        charm(5000, MapleStat.魅力);
        final int limit;
        final MapleStat stat;

        private MapleTraitType(int type, MapleStat theStat) {
            this.limit = type;
            this.stat = theStat;
        }

        public int getLimit() {
            return this.limit;
        }

        public MapleStat getStat() {
            return this.stat;
        }

        public static MapleTraitType getByQuestName(String q) {
            String qq = q.substring(0, q.length() - 3);
            for (MapleTraitType t : values()) {
                if (t.name().equals(qq)) {
                    return t;
                }
            }
            return null;
        }
    }
}