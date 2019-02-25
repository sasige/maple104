package handling.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;
import server.ServerProperties;

public class NettyServerConnection {
    private static final Logger log = Logger.getLogger(NettyServerConnection.class);

    private final int port;
    private int world = -1;
    private int channels = -1;
    private boolean cs;
    private ServerBootstrap boot;
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;

    public NettyServerConnection(int port) {
        this.port = port;
    }

    public NettyServerConnection(int port, int world, int channels, boolean cs) {
        this.port = port;
        this.world = world;
        this.channels = channels;
        this.cs = cs;
    }

    public void run() throws Exception {
        this.boot = new ServerBootstrap().group(this.bossGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, Integer.parseInt(ServerProperties.getProperty("login.userlimit")))
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .childHandler(new NettyServerInit(this.world, this.channels, this.cs));
        this.channel = this.boot.bind(this.port).sync().channel().closeFuture().channel();
    }

    public void close() {
        if (this.channel != null) {
            this.channel.close();
        }
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }
}