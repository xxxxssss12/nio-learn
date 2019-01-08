package xs.netty.project.nio.server;

import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioEchoServer {
    private static class EchoClientHandle implements Runnable {
        private SocketChannel clientChannel;
        private boolean flag = true;
        public EchoClientHandle(SocketChannel clientChannel) {
            this.clientChannel = clientChannel;
            // 一般连上服务器后，应先发送一个消息给客户端
        }
        @Override
        public void run() {
            ByteBuffer buffer = ByteBuffer.allocate(50);

            try {
                while (this.flag) {
                    buffer.clear();
                    int size = this.clientChannel.read(buffer);
                    String readMsg = new String(buffer.array(), 0, size).trim();
                    System.out.println("input=>" + readMsg + ";" + readMsg.length());
                    if (readMsg.length() > 0) {
                        String writeMsg = "[ECHO]" + readMsg + "\r\n";
                        if ("byebye".equalsIgnoreCase(readMsg)) {
                            writeMsg = "[EXIT]goodBye";
                            this.flag = false;
                        }
                        System.out.println("output:" + writeMsg);
                        ByteBuffer outputBuffer = ByteBuffer.wrap(writeMsg.getBytes());
                        this.clientChannel.write(outputBuffer);
                    }
                    buffer.clear();
                    buffer.flip();
                }
                this.clientChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        // NIO基于Channel控制，有一个Selector负责管理所有Channel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 为其设置非阻塞状态机制
        serverSocketChannel.configureBlocking(false);
        // 绑定8000端口
        serverSocketChannel.bind(new InetSocketAddress(9999));
        System.out.println("监听端口：9999");
        // 设置一个selector，管理所有channel
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // 连接进来时处理
        //
        int keySelect = 0;
        while ((keySelect = selector.select()) > 0) {
            Set<SelectionKey> set = selector.selectedKeys();
            Iterator<SelectionKey> iterator = set.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()) {
                    SocketChannel channel = serverSocketChannel.accept();
                    if (channel != null) {
                        executorService.submit(new EchoClientHandle(channel));
                    }
                }
                iterator.remove();
            }
        }
        executorService.shutdown();
        serverSocketChannel.close();
    }
}