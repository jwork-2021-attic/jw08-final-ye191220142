package network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import screen.ClientScreen;
import screen.PlayScreen;

/**
 * 
 * Test client for NIO server
 *
 */
public class WriteClient {
    private SocketChannel client;
    private ClientScreen cs;

    public WriteClient(ClientScreen cs) throws IOException, InterruptedException {
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 9093);
        client = SocketChannel.open(hostAddress);
        this.cs = cs;
    }

    public void write(String s) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(128);
        buffer.put(s.getBytes());
        buffer.flip();
        client.write(buffer);
        buffer.clear();
    }

    public void read() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(10240);
        int numRead = 0;
        numRead = client.read(buffer);
        if (numRead > 0) {
            byte[] data = new byte[numRead];
            System.arraycopy(buffer.array(), 0, data, 0, numRead);
            buffer.clear();
            String s = new String(data);
            cs.handle(s);
        }
    }

    public void action(int keycode) throws IOException {
        String s = "action " + cs.id() + " " + keycode + " ";
        write(s);
    }

}