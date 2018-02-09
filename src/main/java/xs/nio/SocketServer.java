package xs.nio;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;

/**
 * Created by xs on 2018/2/9
 */
public class SocketServer {
    private static int LISTEN_PORT = 5300;

    public static void main(String[] args) throws Exception {
        try {
            //1、创建一个服务器端Socket，即ServerSocket，指定绑定的端口，并监听此端口
            ServerSocket serverSocket = new ServerSocket(LISTEN_PORT);//1024-65535的某个端口
            //2、调用accept()方法开始监听，等待客户端的连接
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("connection accept---ip:port=" + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                //3、获取输入流，并读取客户端信息
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String info = null;
                while ((info = br.readLine()) != null) {
                    System.out.println("我是服务器，客户端说：" + info);
                }
                socket.shutdownInput();//关闭输入流
                //4、获取输出流，响应客户端的请求
                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(os);
                pw.write("欢迎您！");
                pw.flush();
                System.out.println("socket disconnect...");
                pw.close();
                os.close();
                socket.close();
                br.close();
                isr.close();
                is.close();
            }
            //5、关闭资源
//            serverSocket.close();
        } catch (Exception e) {

        }
    }
}
