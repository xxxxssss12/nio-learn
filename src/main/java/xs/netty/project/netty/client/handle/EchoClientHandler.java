package xs.netty.project.netty.client.handle;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import xs.netty.project.util.InputUtil;

/**
 *
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ByteBuf readBuf = (ByteBuf) msg;
            String readData = readBuf.toString(CharsetUtil.UTF_8).trim();
            if ("[ECHO]quit".equalsIgnoreCase(readData)) {
                System.out.println("[exit]结束");
                ctx.close();
            } else {
                System.out.println("server response:" + readData);
                String inputData = InputUtil.getString("请输入要发送的消息: ");
                byte[] data = inputData.getBytes(CharsetUtil.UTF_8);
                ByteBuf sendBuf = Unpooled.buffer(data.length);
                sendBuf.writeBytes(data);
                ctx.writeAndFlush(sendBuf);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
