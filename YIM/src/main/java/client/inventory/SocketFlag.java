package client.inventory;

public enum SocketFlag {

    可以镶嵌(1),
    已打孔01(2),
    已打孔02(4),
    已打孔03(8),
    已镶嵌01(16),
    已镶嵌02(32),
    已镶嵌03(64);
    private final int i;

    private SocketFlag(int i) {
        this.i = i;
    }

    public short getValue() {
        return (short) this.i;
    }

    public boolean check(int flag) {
        return (flag & this.i) == this.i;
    }
}
