package xs.netty;

import io.netty.buffer.ByteBuf;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Handles a server-side channel.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter { // (1)


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        // Discard the received data silently.

        try {
            // Do something with msg
            ByteBuf in = (ByteBuf) msg;
            CharSequence cs = null;
            while (in.isReadable()) { // (1)
                cs = in.readCharSequence(in.readableBytes(), Charset.defaultCharset());
                System.out.print(cs);
                System.out.flush();
            }
            System.out.println("refcnt=" + ((ByteBuf) msg).refCnt());
            ctx.write(cs);
            System.out.println("refcnt=" + ((ByteBuf) msg).refCnt());
            ctx.fireChannelRead(msg);
            System.out.println("refcnt=" + ((ByteBuf) msg).refCnt());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}