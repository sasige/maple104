package tools.data.output;


import io.netty.buffer.ByteBuf;

public class ByteBufferLittleEndianWriter extends GenericLittleEndianWriter {

    private ByteBuf bb;

    public ByteBufferLittleEndianWriter() {
        this(50, true);
    }

    public ByteBufferLittleEndianWriter(int size) {
        this(size, false);
    }

    public ByteBufferLittleEndianWriter(int initialSize, boolean autoExpand) {
     /*   this.bb = IoBuffer.allocate(initialSize);
        this.bb.setBoolean(initialSizeautoExpand);*/
        setByteOutputStream(new ByteBufferOutputstream(this.bb));
    }

    public ByteBuf getFlippedBB() {
        return this.bb;
    }

    public ByteBuf getByteBuffer() {
        return this.bb;
    }
}