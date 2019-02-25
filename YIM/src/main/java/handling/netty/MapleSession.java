package handling.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.SocketAddress;

public class MapleSession {

    private Channel channel;

    public MapleSession(Channel session) {
        this.channel = session;
    }

    public ChannelFuture write(Object o) {
        return this.channel.writeAndFlush(o);
    }

    public void close() {
        this.channel.close();
    }

    public SocketAddress getRemoteAddress() {
        return this.channel.remoteAddress();
    }

    public boolean isActive() {
        return this.channel.isActive();
    }

    public boolean isOpen() {
        return this.channel.isOpen();
    }

    public Channel getChannel() {
        return this.channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
