package network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import screen.ServerScreen;
import world.*;

/**
 * 
 * This is a simple NIO based server.
 *
 */
public class Server {
    private Selector selector;

    private InetSocketAddress listenAddress;
    private final static int PORT = 9093;

    private ServerScreen ss;

    public Server(String address, int port, ServerScreen ss) throws IOException {
        listenAddress = new InetSocketAddress(address, PORT);
        this.ss = ss;
    }

    /**
     * Start the server
     * 
     * @throws IOException
     */
    public void startServer() throws IOException {
        this.selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        // bind server socket channel to port
        serverChannel.socket().bind(listenAddress);
        serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server started on port >> " + PORT);

        while (true) {
            // wait for events
            int readyCount = selector.select();
            if (readyCount == 0) {
                continue;
            }

            // process selected keys...
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = (SelectionKey) iterator.next();

                // Remove key from set so we don't process it twice
                iterator.remove();

                if (!key.isValid()) {
                    continue;
                }

                if (key.isAcceptable()) { // Accept client connections
                    this.accept(key);
                } else if (key.isReadable()) { // Read from client
                    this.read(key);
                    this.write(key);
                } else if (key.isWritable()) {
                    // write data to client...
                    // System.out.println("write");
                    this.write(key);
                }
            }
        }
    }

    // accept client connection
    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        Socket socket = channel.socket();
        SocketAddress remoteAddr = socket.getRemoteSocketAddress();
        System.out.println("Connected to: " + remoteAddr);

        /*
         * Register channel with selector for further IO (record it for read/write
         * operations, here we have used read operation)
         */
        channel.register(this.selector, SelectionKey.OP_READ);
        // channel.register(this.selector, SelectionKey.OP_WRITE);
    }

    // read from the socket channel
    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int numRead = -1;
        numRead = channel.read(buffer);

        if (numRead == -1) {
            Socket socket = channel.socket();
            SocketAddress remoteAddr = socket.getRemoteSocketAddress();
            System.out.println("Connection closed by client: " + remoteAddr);
            channel.close();
            key.cancel();
            return;
        }

        Socket socket = channel.socket();
        SocketAddress remoteAddr = socket.getRemoteSocketAddress();
        // System.out.println("Read From: " + remoteAddr);
        // get data
        byte[] data = new byte[numRead];
        System.arraycopy(buffer.array(), 0, data, 0, numRead);
        buffer.clear();
        // change to string
        String s = new String(data);
		if(s.split(" ")[0].equals("action")) {
			ss.handle(s);
		}
    }

    private void write(SelectionKey key) throws IOException {
        if (key.channel() instanceof SocketChannel) {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(10240);
            String s = "";
            for (Creature creature : ss.world().getCreatures()) {
                switch (creature.type()) {
                    case 0: // player
                        s += String.valueOf(creature.type()) + " " + creature.x() + " " + creature.y() + " ";
                        s += String.valueOf(creature.hp()) + " " + String.valueOf(creature.money()) + " ";
                        Player player = (Player)creature;
                        s += String.valueOf(player.id()); 
                        break;
                    case 1: // enemy
                        s += String.valueOf(creature.type()) + " " + creature.x() + " " + creature.y() + " ";
                        s += String.valueOf(creature.hp());
                        break;
                    case 2: // coin
                        s += String.valueOf(creature.type()) + " " + creature.x() + " " + creature.y();
                        break;
                    case 3: // ice
                        s += String.valueOf(creature.type()) + " " + creature.x() + " " + creature.y();
                        break;
                    case 4: // stone
                        s += String.valueOf(creature.type()) + " " + creature.x() + " " + creature.y();
                        break;
                    case 5: // bullet
                        s += String.valueOf(creature.type()) + " " + creature.x() + " " + creature.y() + " ";
                        Bullet bullet = (Bullet) creature;
                        s += String.valueOf(bullet.dir());
                        break;
                    case 6: // bomb
                        s += String.valueOf(creature.type()) + " " + creature.x() + " " + creature.y() + " ";
                        s += String.valueOf(creature.count());
                        break;
                }
                s += "\n";
            }
            byte[] data = s.getBytes();
            buffer.put(data);
            buffer.flip();
            channel.write(buffer);
            buffer.clear();
        }
    }

}