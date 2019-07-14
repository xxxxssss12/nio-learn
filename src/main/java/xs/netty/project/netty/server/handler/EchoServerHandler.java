package xs.netty.project.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import xs.netty.project.util.InputUtil;

/**
 * ChannelInboundHandlerAdapter是针对数据输入的处理
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 客户端连接成功时触发
        byte data[] = InputUtil.buildPackage("[server]connect success!".getBytes(CharsetUtil.UTF_8));
        ByteBuf buf = Unpooled.buffer(data.length); // netty自定义缓存

        buf.writeBytes(data);
        ctx.writeAndFlush(buf);
        System.out.println("客户端连入：" + ctx.channel().remoteAddress().toString());
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            // 客户端发过来的消息处理
            ByteBuf buf = (ByteBuf) msg;
            String inputData = buf.toString(CharsetUtil.UTF_8);
            String respData = "[ECHO]" + inputData;
            if ("exit".equalsIgnoreCase(inputData)) {
                respData = "quit.";
            }
            byte[] data = InputUtil.buildPackage(respData.getBytes());

            ByteBuf respbuf = Unpooled.buffer(data.length); // netty自定义缓存
            respbuf.writeBytes(data);
            ctx.writeAndFlush(respbuf);
            System.out.println("响应:addr=" + ctx.channel().remoteAddress().toString() + ";msg=" + respData);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
