package tools.data.input;

import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;

public class RandomAccessByteStream
        implements SeekableInputStreamBytestream {

    private RandomAccessFile raf;
    private long read = 0L;

    public RandomAccessByteStream(RandomAccessFile raf) {
        this.raf = raf;
    }

    public int readByte() {
        try {
            int temp = this.raf.read();
            if (temp == -1) {
                throw new RuntimeException("EOF");
            }
            this.read += 1L;
            return temp;
        } catch (IOException e) {
        }
        throw new RuntimeException();
    }

    public void seek(long offset)
            throws IOException {
        this.raf.seek(offset);
    }

    public long getPosition()
            throws IOException {
        return this.raf.getFilePointer();
    }

    public long getBytesRead() {
        return this.read;
    }

    public long available() {
        try {
            return this.raf.length() - this.raf.getFilePointer();
        } catch (IOException e) {
            System.err.println("ERROR" + e);
        }
        return 0L;
    }

    public String toString(boolean b) {
        return toString();
    }
}