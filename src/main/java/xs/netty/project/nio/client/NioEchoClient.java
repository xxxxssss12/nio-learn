package xs.netty.project.nio.client;

import xs.netty.project.util.HostInfo;
import xs.netty.project.util.InputUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioEchoClient {

    public static void main(String[] args) throws IOException {
        SocketChannel clientChannel = SocketChannel.open();
        clientChannel.connect(new InetSocketAddress(HostInfo.HOST_NAME, HostInfo.PORT));
        ByteBuffer buffer = ByteBuffer.allocate(50);
        boolean flag = true;
        while (flag) {
            buffer.clear();
            String inputData = InputUtil.getString("请输入:").trim();
            buffer.put(inputData.getBytes());
            buffer.flip();
            clientChannel.write(buffer);
            buffer.clear();
            int readCnt = clientChannel.read(buffer);
            buffer.flip();
            System.out.println(new String(buffer.array(),0,readCnt));
            if ("byebye".equalsIgnoreCase(inputData)) {
                flag = false;
            }
        }
        clientChannel.close();
    }
}
