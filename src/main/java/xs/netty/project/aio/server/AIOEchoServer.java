package xs.netty.project.aio.server;

import io.netty.buffer.ByteBuf;
import xs.netty.project.util.HostInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;

public class AIOEchoServer {
    public static void main(String[] args) throws IOException {
        new Thread(new AioServerThread()).start();
    }
}

class AioServerThread implements Runnable {

    private AsynchronousServerSocketChannel serverSocketChannel = null;
    private CountDownLatch latch = null;
    public AioServerThread() throws IOException {
        this.latch = new CountDownLatch(1);// 等待线程数1
        this.serverSocketChannel = AsynchronousServerSocketChannel.open();
        this.serverSocketChannel.bind(new InetSocketAddress(HostInfo.PORT));
        System.out.println("aio server监听:" + HostInfo.PORT);
    }
    @Override
    public void run() {
        this.serverSocketChannel.accept(this, new AcceptHandle());
        try {
            this.latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public CountDownLatch getLatch() {
        return latch;
    }
    public AsynchronousServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }
}

class AcceptHandle implements CompletionHandler<AsynchronousSocketChannel, AioServerThread> {

    @Override
    public void completed(AsynchronousSocketChannel result, AioServerThread attachment) {
        attachment.getServerSocketChannel().accept(attachment, this);
        ByteBuffer buffer = ByteBuffer.allocate(100);
        result.read(buffer, buffer, new EchoHandle(result));
    }

    @Override
    public void failed(Throwable exc, AioServerThread aioServerThread) {
        System.out.println("客户端连接失败");
        aioServerThread.getLatch().countDown();
    }
}

class EchoHandle implements CompletionHandler<Integer, ByteBuffer> {
    private boolean exit = false;
    private AsynchronousSocketChannel clientChannel;
    public EchoHandle(AsynchronousSocketChannel clientChannel) {
        this.clientChannel = clientChannel;
    }
    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        buffer.flip();
        String readMsg = new String(buffer.array(), 0, buffer.remaining()).trim();
        String writeMsg = "[ECHO]" + readMsg;
        if ("byebye".equalsIgnoreCase(readMsg)) {
            writeMsg = "[EXIT]good bye";
            System.out.println("断开连接");
            this.exit = true;
        }
        this.echoWrite(writeMsg);
    }
    private void echoWrite(String content) {
        System.out.println("返回:" + content);
        ByteBuffer buffer = ByteBuffer.allocate(100);
        buffer.put(content.getBytes());
        buffer.flip();
        this.clientChannel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buf) {
                if (buf.hasRemaining()) {
                    EchoHandle.this.clientChannel.write(buffer, buffer, this);
                } else {
                    if (!EchoHandle.this.exit) {
                        ByteBuffer readBuffer = ByteBuffer.allocate(100);
                        EchoHandle.this.clientChannel.read(readBuffer, readBuffer, new EchoHandle(EchoHandle.this.clientChannel));
                    }
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    EchoHandle.this.clientChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}