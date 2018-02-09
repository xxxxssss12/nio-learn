package xs.nio;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by xs on 2018/2/8
 *
 */
        /*
        ServerSocketChannel
        ServerSocket
        SocketChannel
        Selector
        SelectionKey
         */
public class SelectorServer {
    private static int LISTEN_PORT = 5300;
    public static void main(String[] args) {

        try {
            ServerSocketChannel ssc = buildServerSocketChannel();

            Selector selector = Selector.open();
            SelectionKey skey = ssc.register( selector, SelectionKey.OP_ACCEPT );

            ByteBuffer echoBuffer = ByteBuffer.allocate(128);

            System.out.println("channel 准备就绪！");
            while(true) {
                int num = selector.select();//获取通道内是否有选择器的关心事件
                if (num < 1) {
                    continue;
                }
                Set<SelectionKey> selectedKeys = selector.selectedKeys();//获取通道内关心事件的集合
                Iterator<SelectionKey> it = selectedKeys.iterator();
                while (it.hasNext()) {
                    //遍历每个key
                    SelectionKey key = it.next();
                    it.remove();
                    System.out.println("key hashCode=" + key.hashCode());
                    if (key.isAcceptable()) {
                        // 有新的socket链接进来
                        ServerSocketChannel serverChanel = (ServerSocketChannel)key.channel();
                        SocketChannel sc = serverChanel.accept();
                        sc.configureBlocking( false );
                        SelectionKey newKey = sc.register( selector, SelectionKey.OP_READ );
                        System.out.println( "Got connection from "+sc );
                    }
                    if (key.isReadable()) {
                        // 有请求进来
                        SocketChannel sc = (SocketChannel)key.channel();
                        System.out.println("address:" + sc.socket().getPort());
                        int bytesEchoed = 0;
                        while((bytesEchoed = sc.read(echoBuffer))> 0){
                            System.out.println("bytesEchoed:"+bytesEchoed);
                        }
                        echoBuffer.flip();
                        if (bytesEchoed == -1) {
                            System.out.println("connect finish!over!");
                            sc.socket().close();
                            printSelectorKeys(selector);
                            break;
                        }
                        byte [] content = new byte[echoBuffer.limit()];
                        echoBuffer.get(content);
                        String result=new String(content, "utf-8");
                        doPost(result,sc);
                        echoBuffer.clear();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void printSelectorKeys(Selector selector) {
    }

    private static void doPost(String result, SocketChannel sc) {
        System.out.println(result);
    }

    private static ServerSocketChannel buildServerSocketChannel() throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking( false );//使通道为非阻塞
        ServerSocket ss = channel.socket();//创建基于NIO通道的socket连接
        ss.bind(new InetSocketAddress("127.0.0.1",LISTEN_PORT));//新建socket通道的端口
        //将NIO通道选绑定到择器,当然绑定后分配的主键为skey
        return channel;
    }
    private static void register(ServerSocketChannel ssc, Selector selector, int ops) throws ClosedChannelException {
        SelectionKey key = ssc.register(selector, ops);
        System.out.println("selectKey hashCode=" + key.hashCode());
    }
}
