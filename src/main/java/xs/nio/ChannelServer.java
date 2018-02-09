package xs.nio;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by xs on 2018/1/25
 */
public class ChannelServer {
    public static void main(String[] args) {

        try(RandomAccessFile aFile = new RandomAccessFile("/Users/xs/login.txt", "rw")) {
            FileChannel inChannel = aFile.getChannel();
            //create buffer with capacity of 48 bytes
            ByteBuffer header = ByteBuffer.allocate(128);
            ByteBuffer body   = ByteBuffer.allocate(32);

            ByteBuffer[] bufferArray = { header, body };


            long bytesRead = inChannel.read(bufferArray); //read into buffer.
            while (bytesRead != -1) {
                int i=0;
                for (ByteBuffer buf : bufferArray) {
                    i++;
                    buf.flip();  //make buffer ready for read
                    System.out.println("\n...index=" + i);
                    while (buf.hasRemaining()) {
                        System.out.print((char) buf.get()); // read 1 byte at a time
                    }
                    buf.clear(); //make buffer ready for writing
                }
                bytesRead = inChannel.read(bufferArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
