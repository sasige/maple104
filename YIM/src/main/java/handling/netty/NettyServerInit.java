package handling.netty;

import handling.MapleServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class NettyServerInit extends ChannelInitializer<SocketChannel> {

    private int world;
    private int channels;
    private boolean cs;

    public NettyServerInit(int world, int channels, boolean cs) {
        this.world = world;
        this.channels = channels;
        this.cs = cs;
    }
    @Override
    protected void initChannel(SocketChannel channel) {
        ChannelPipeline pipe = channel.pipeline();

        pipe.addLast("decoder", new MaplePacketDecoder());
        pipe.addLast("encoder", new MaplePacketEncoder());
        pipe.addLast("handler", new MapleServerHandler(this.channels, this.cs));
    }
}
