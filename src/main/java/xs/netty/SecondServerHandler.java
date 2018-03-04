package xs.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by xs on 2018/2/23
 */
public class SecondServerHandler extends ChannelInboundHandlerAdapter { // (1)
    private static int count = 0;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        // Discard the received data silently.

        try {
            // Do something with msg
            System.out.println("SecondServerHandler end");
            ctx.writeAndFlush(Unpooled.copiedBuffer(("resp cnt=" + ++count).getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
