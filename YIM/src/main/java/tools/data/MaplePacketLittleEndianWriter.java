package tools.data;

import constants.ServerConstants;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import tools.HexTool;

public class MaplePacketLittleEndianWriter {

    private ByteArrayOutputStream baos;
    private static Charset ASCII = Charset.forName("GBK");

    public MaplePacketLittleEndianWriter() {
        this(32);
    }

    public MaplePacketLittleEndianWriter(int size) {
        this.baos = new ByteArrayOutputStream(size);
    }

    public byte[] getPacket() {
//        if (ServerConstants.Use_Localhost) {
//            System.out.println("·þÎñ¶Ë·¢ËÍ:\n" + HexTool.toString(this.baos.toByteArray()) + "\n" + HexTool.toStringFromAscii(this.baos.toByteArray()));
//        }
        return this.baos.toByteArray();
    }

    public String toString() {
        return HexTool.toString(this.baos.toByteArray());
    }

    public void writeZeroBytes(int i) {
        for (int x = 0; x < i; x++) {
            this.baos.write(0);
        }
    }

    public void write(byte[] b) {
        for (int x = 0; x < b.length; x++) {
            this.baos.write(b[x]);
        }
    }

    public void write(byte b) {
        this.baos.write(b);
    }

    public void write(int b) {
        this.baos.write((byte) b);
    }

    public void writeShort(int i) {
        this.baos.write((byte) (i & 0xFF));
        this.baos.write((byte) (i >>> 8 & 0xFF));
    }

    public void writeInt(int i) {
        this.baos.write((byte) (i & 0xFF));
        this.baos.write((byte) (i >>> 8 & 0xFF));
        this.baos.write((byte) (i >>> 16 & 0xFF));
        this.baos.write((byte) (i >>> 24 & 0xFF));
    }

    public void writeAsciiString(String s) {
        write(s.getBytes(ASCII));
    }

    public void writeAsciiString(String s, int max) {
        if (s.length() > max) {
            s = s.substring(0, max);
        }
        write(s.getBytes(ASCII));
        for (int i = s.getBytes().length; i < max; i++) {
            write(0);
        }
    }

    public void writeMapleNameString(String s) {
        if (s.getBytes().length > 12) {
            s = s.substring(0, 12);
        }
        writeAsciiString(s);
        for (int x = s.getBytes().length; x < 12; x++) {
            write(0);
        }
    }

    public void writeMapleAsciiString(String s) {
        writeShort((short) s.getBytes().length);
        writeAsciiString(s);
    }

    public void writePos(Point s) {
        writeShort(s.x);
        writeShort(s.y);
    }

    public void writeRect(Rectangle s) {
        writeInt(s.x);
        writeInt(s.y);
        writeInt(s.x + s.width);
        writeInt(s.y + s.height);
    }

    public void writeLong(long l) {
        this.baos.write((byte) (int) (l & 0xFF));
        this.baos.write((byte) (int) (l >>> 8 & 0xFF));
        this.baos.write((byte) (int) (l >>> 16 & 0xFF));
        this.baos.write((byte) (int) (l >>> 24 & 0xFF));
        this.baos.write((byte) (int) (l >>> 32 & 0xFF));
        this.baos.write((byte) (int) (l >>> 40 & 0xFF));
        this.baos.write((byte) (int) (l >>> 48 & 0xFF));
        this.baos.write((byte) (int) (l >>> 56 & 0xFF));
    }
}