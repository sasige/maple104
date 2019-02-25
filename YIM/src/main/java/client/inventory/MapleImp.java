package client.inventory;

import java.io.Serializable;

public class MapleImp implements Serializable {

    public static enum ImpFlag {

        REMOVED(0x1),
        SUMMONED(0x2),
        TYPE(0x4),
        STATE(0x8),
        FULLNESS(0x10),
        CLOSENESS(0x20),
        CLOSENESS_LEFT(0x40),
        MINUTES_LEFT(0x80),
        LEVEL(0x100),
        FULLNESS_2(0x200),
        UPDATE_TIME(0x400),
        CREATE_TIME(0x800),
        AWAKE_TIME(0x1000),
        SLEEP_TIME(0x2000),
        MAX_CLOSENESS(0x4000),
        MAX_DELAY(0x8000),
        MAX_FULLNESS(0x10000),
        MAX_ALIVE(0x20000),
        MAX_MINUTES(0x40000);
        private int i;

        private ImpFlag(int i) {
            this.i = i;
        }

        public int getValue() {
            return this.i;
        }

        public boolean check(int flag) {
            return (flag & this.i) == this.i;
        }
    }
    private static final long serialVersionUID = 91795493413738569L;
    private int itemid;
    private short fullness = 0;
    private short closeness = 0;
    private byte state = 1;
    private byte level = 1;

    public MapleImp(int itemid) {
        this.itemid = itemid;
    }

    public int getItemId() {
        return this.itemid;
    }

    public byte getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = (byte) state;
    }

    public byte getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = (byte) level;
    }

    public short getCloseness() {
        return this.closeness;
    }

    public void setCloseness(int closeness) {
        this.closeness = (short) Math.min(100, closeness);
    }

    public short getFullness() {
        return this.fullness;
    }

    public void setFullness(int fullness) {
        this.fullness = (short) Math.min(1000, fullness);
    }
}
