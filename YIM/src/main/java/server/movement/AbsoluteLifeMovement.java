package server.movement;

import java.awt.Point;
import tools.data.MaplePacketLittleEndianWriter;

public class AbsoluteLifeMovement extends AbstractLifeMovement {

    private Point pixelsPerSecond;
    private Point offset;
    private int unk;

    public AbsoluteLifeMovement(int type, Point position, int duration, int newstate) {
        super(type, position, duration, newstate);
    }

    public Point getPixelsPerSecond() {
        return this.pixelsPerSecond;
    }

    public void setPixelsPerSecond(Point wobble) {
        this.pixelsPerSecond = wobble;
    }

    public Point getOffset() {
        return this.offset;
    }

    public void setOffset(Point wobble) {
        this.offset = wobble;
    }

    public int getUnk() {
        return this.unk;
    }

    public void setUnk(int unk) {
        this.unk = unk;
    }

    public void defaulted() {
        this.unk = 0;
        this.pixelsPerSecond = new Point(0, 0);
        this.offset = new Point(0, 0);
    }

    @Override
    public void serialize(MaplePacketLittleEndianWriter lew) {
        lew.write(getType());
        lew.writePos(getPosition());
        lew.writePos(this.pixelsPerSecond);
        lew.writeShort(this.unk);
        lew.writePos(this.offset);
        lew.write(getNewstate());
        lew.writeShort(getDuration());
    }
}