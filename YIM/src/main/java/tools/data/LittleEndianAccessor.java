package tools.data;

import java.awt.Point;
import java.io.IOException;

public class LittleEndianAccessor {

    private final ByteArrayByteStream bs;

    public LittleEndianAccessor(ByteArrayByteStream bs) {
        this.bs = bs;
    }

    public byte readByte() {
        return (byte) this.bs.readByte();
    }

    public int readInt() {
        int byte1 = this.bs.readByte();
        int byte2 = this.bs.readByte();
        int byte3 = this.bs.readByte();
        int byte4 = this.bs.readByte();
        return (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
    }

    public short readShort() {
        int byte1 = this.bs.readByte();
        int byte2 = this.bs.readByte();
        return (short) ((byte2 << 8) + byte1);
    }

    public int readUShort() {
        int quest = readShort();
        if (quest < 0) {
            quest += 65536;
        }
        return quest;
    }

    public char readChar() {
        return (char) readShort();
    }

    public long readLong() {
        int byte1 = this.bs.readByte();
        int byte2 = this.bs.readByte();
        int byte3 = this.bs.readByte();
        int byte4 = this.bs.readByte();
        long byte5 = this.bs.readByte();
        long byte6 = this.bs.readByte();
        long byte7 = this.bs.readByte();
        long byte8 = this.bs.readByte();
        return (byte8 << 56) + (byte7 << 48) + (byte6 << 40) + (byte5 << 32) + (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
    }

    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    public String readAsciiString(int n) {
        byte[] ret = new byte[n];
        for (int x = 0; x < n; x++) {
            ret[x] = readByte();
        }
        try {
            return new String(ret, "gbk");
        } catch (Exception e) {
            System.err.println(e);
        }
        return "";
    }

    public long getBytesRead() {
        return this.bs.getBytesRead();
    }

    public String readMapleAsciiString() {
        return readAsciiString(readShort());
    }

    public Point readPos() {
        int x = readShort();
        int y = readShort();
        return new Point(x, y);
    }

    public byte[] read(int num) {
        byte[] ret = new byte[num];
        for (int x = 0; x < num; x++) {
            ret[x] = readByte();
        }
        return ret;
    }

    public long available() {
        return this.bs.available();
    }

    public String toString() {
        return this.bs.toString();
    }

    public String toString(boolean b) {
        return this.bs.toString(b);
    }

    public void seek(long offset) {
        try {
            this.bs.seek(offset);
        } catch (IOException e) {
            System.err.println("Seek failed" + e);
        }
    }

    public long getPosition() {
        return this.bs.getPosition();
    }

    public void skip(int num) {
        seek(getPosition() + num);
    }
}