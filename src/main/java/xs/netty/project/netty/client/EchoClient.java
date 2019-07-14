package xs.netty.project.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import xs.netty.project.netty.client.handle.EchoClientHandler;
import xs.netty.project.util.HostInfo;

public class EchoClient {
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup(); // 创建线程池
        try {
            Bootstrap client = new Bootstrap();
            ByteBuf delimiter = Unpooled.buffer(HostInfo.SEPERATOR.length);
            delimiter.writeBytes(HostInfo.SEPERATOR);
            client.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024 * 32, delimiter));
                            socketChannel.pipeline().addLast(new EchoClientHandler());
                        }
                    })
            ;
            ChannelFuture future = client.connect(HostInfo.HOST_NAME, HostInfo.PORT).sync();
            System.out.println("EchoClient 启动");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }
}
