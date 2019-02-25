package tools.data.output;


import io.netty.buffer.ByteBuf;

public class ByteBufferOutputstream implements ByteOutputStream {

    private ByteBuf bb;

    public ByteBufferOutputstream(ByteBuf bb) {
        this.bb = bb;
    }

    public void writeByte(byte b) {
        this.bb.writeByte(b);
    }
}