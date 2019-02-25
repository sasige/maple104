package client;

public enum MapleStat {

    皮肤(0x1),
    脸型(0x2),
    发型(0x4),
    等级(0x10),
    职业(0x20),
    力量(0x40),
    敏捷(0x80),
    智力(0x100),
    运气(0x200),
    HP(0x400),
    MAXHP(0x800),
    MP(0x1000),
    MAXMP(0x2000),
    AVAILABLEAP(0x4000),
    AVAILABLESP(0x8000),
    经验(0x10000),
    人气(0x20000),
    金币(0x40000),
    宠物(0x180008),
    GACHAPONEXP(0x80000),
    疲劳(0x80000),
    领袖(0x100000),
    洞察(0x200000),
    意志(0x400000),
    手技(0x800000),
    感性(0x1000000),
    魅力(0x2000000),
    TRAIT_LIMIT(0x8000000),
    BATTLE_EXP(0x10000000),
    BATTLE_RANK(0x20000000L),
    BATTLE_POINTS(0x40000000L),
    ICE_GAGE(0x80000000L),
    VIRTUE(0x100000000L);

    private final long i;

    private MapleStat(long i) {
        this.i = i;
    }

    public long getValue() {
        return this.i;
    }

    public static MapleStat getByValue(long value) {
        for (MapleStat stat : values()) {
            if (stat.i == value) {
                return stat;
            }
        }
        return null;
    }

    public static enum Temp {

        力量(0x1),
        敏捷(0x2),
        智力(0x4),
        运气(0x8),
        物攻(0x10),
        魔攻(0x20),
        物防(0x40),
        魔防(0x80),
        命中(0x100),
        回避(0x200),
        速度(0x400),
        跳跃(0x800);
        private final int i;

        private Temp(int i) {
            this.i = i;
        }

        public int getValue() {
            return this.i;
        }
    }
}