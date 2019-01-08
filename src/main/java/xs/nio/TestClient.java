package xs.nio;

import java.io.*;
import java.net.Socket;

public class TestClient {
    // 搭建客户端
    public static void main(String[] args) throws IOException {
        try {
            // 1、创建客户端Socket，指定服务器地址和端口
            // Socket socket=new Socket("127.0.0.1",5200);
            Socket socket = new Socket("127.0.0.1", 9999);
            new Thread(new SocketReadThread(socket)).start();
            System.out.println("客户端启动成功");

            // 2、获取输出流，向服务器端发送信息
            // 向本机的52000端口发出客户请求
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            // 由系统标准输入设备构造BufferedReader对象
            PrintWriter write = new PrintWriter(socket.getOutputStream());
            // 由Socket对象得到输出流，并构造PrintWriter对象

            String readline;
            readline = br.readLine(); // 从系统标准输入读入一字符串
            while (!readline.equals("end")) {
                // 若从标准输入读入的字符串为 "end"则停止循环
                write.println(readline);
                // 将从系统标准输入读入的字符串输出到Server
                write.flush();
                // 刷新输出流，使Server马上收到该字符串
                System.out.println("Client:" + readline);
                // 从Server读入一字符串，并打印到标准输出上
                readline = br.readLine(); // 从系统标准输入读入一字符串
            } // 继续循环
            //4、关闭资源
            write.close(); // 关闭Socket输出流
            socket.close(); // 关闭Socket
        } catch (Exception e) {
            System.out.println("can not listen to:" + e);// 出错，打印出错信息
        }
    }
}

class SocketReadThread implements Runnable {

    private Socket socket;
    public SocketReadThread(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        try {

            InputStream is = socket.getInputStream();
            byte[] bt = new byte[1024];

            System.out.println("读监听开始...");
            for (;;) {
                StringBuilder sb = new StringBuilder();
                int size = is.read(bt);
                if (size >= 0) {
                    sb.append(new String(bt,0, size, "utf-8"));
                } else {
                    is.close();
                    break;
                }
                System.out.println("SocketReadThread:size=" + size + ";" + sb);
            }
            System.out.println("服务端dead，读监听结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}