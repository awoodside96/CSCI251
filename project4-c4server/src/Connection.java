
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author Joseph Cumbo (mooman219)
 */
public abstract class Connection<I extends Message, O extends Message> extends Thread {

    private final ConcurrentLinkedQueue<I> inbound = new ConcurrentLinkedQueue<I>();
    private final ConcurrentLinkedQueue<O> outbound = new ConcurrentLinkedQueue<O>();
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final AtomicBoolean connected = new AtomicBoolean(true);

    public Connection(Socket socket) throws IOException {
        socket.setSoTimeout(50);
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public ConcurrentLinkedQueue<I> getInbound() {
        return inbound;
    }

    public ConcurrentLinkedQueue<O> getOutbound() {
        return outbound;
    }

    public void disconnect() {
        connected.set(false);
    }

    public boolean isConnected() {
        return connected.get();
    }

    @Override
    public void run() {
        try {
            while (connected.get()) {
                while (!outbound.isEmpty()) {
                    O message = outbound.remove();
                    //System.out.println("Sent " + message.toString());
                    message.send(out);
                    out.flush();
                }
                try {
                    int result = in.read();
                    if (result == -1) {
                        break;
                    } else {
                        I message = read((byte) result, in);
                        if (message != null) {
                            //System.out.println("Read " + message.toString());
                            inbound.add(message);
                        }
                    }
                } catch (SocketTimeoutException ex) {
                    //System.out.println("Timeout");
                }
            }
        } catch (IOException ex) {
            //System.out.println("Lost connection.");
        }
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException ex) {
            //System.out.println("Error while closing resources.");
        }
        connected.set(false);
        //System.out.println("Connection closed");
    }

    public abstract I read(byte id, DataInputStream in) throws IOException;

    public static class ToServer extends Connection<Message.ToClient, Message.ToServer> {

        public ToServer(String host, int port) throws IOException {
            super(new Socket(host, port));
        }

        @Override
        public Message.ToClient read(byte id, DataInputStream in) throws IOException {
            switch (id) {
                case Message.ToClient.Number.MESSAGE_ID:
                    return new Message.ToClient.Number(in);
                case Message.ToClient.Name.MESSAGE_ID:
                    return new Message.ToClient.Name(in);
                case Message.ToClient.Turn.MESSAGE_ID:
                    return new Message.ToClient.Turn(in);
                case Message.ToClient.Add.MESSAGE_ID:
                    return new Message.ToClient.Add(in);
                case Message.ToClient.Clear.MESSAGE_ID:
                    return new Message.ToClient.Clear();
                default:
                    return null;
            }
        }
    }

    public static class ToClient extends Connection<Message.ToServer, Message.ToClient> {

        public ToClient(Socket socket) throws IOException {
            super(socket);
        }

        @Override
        public Message.ToServer read(byte id, DataInputStream in) throws IOException {
            switch (id) {
                case Message.ToServer.Add.MESSAGE_ID:
                    return new Message.ToServer.Add(in);
                case Message.ToServer.Clear.MESSAGE_ID:
                    return new Message.ToServer.Clear();
                case Message.ToServer.Join.MESSAGE_ID:
                    return new Message.ToServer.Join(in);
                default:
                    return null;
            }
        }
    }
}
