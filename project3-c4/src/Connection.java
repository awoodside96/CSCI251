
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * @author Joseph Cumbo (mooman219)
 */
public class Connection extends Thread {

    private final ConcurrentLinkedQueue<OutboundMessage> outbound = new ConcurrentLinkedQueue<OutboundMessage>();
    private final ConcurrentLinkedQueue<InboundMessage> inbound = new ConcurrentLinkedQueue<InboundMessage>();
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    public Connection(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public ConcurrentLinkedQueue<OutboundMessage> getOutbound() {
        return outbound;
    }

    public ConcurrentLinkedQueue<InboundMessage> getInbound() {
        return inbound;
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                while (!outbound.isEmpty()) {
                    OutboundMessage message = outbound.remove();
                    System.out.println("Sent " + message.toString());
                    message.send(out);
                }
                if (in.ready()) {
                    String[] inputs = in.readLine().split(" ");
                    switch (inputs[0]) {
                        case "number":
                            inbound.add(new InboundMessage.Number(Integer.parseInt(inputs[1])));
                            break;
                        case "name":
                            inbound.add(new InboundMessage.Name(Integer.parseInt(inputs[1]), inputs[2]));
                            break;
                        case "turn":
                            inbound.add(new InboundMessage.Turn(Integer.parseInt(inputs[1])));
                            break;
                        case "add":
                            inbound.add(new InboundMessage.Add(Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]), Integer.parseInt(inputs[3])));
                            break;
                        case "clear":
                            inbound.add(new InboundMessage.Clear());
                            break;
                    }
                }
            }
            out.close();
            in.close();
            socket.close();
        } catch (IOException ex) {
            System.err.println("Lost connection.");
        }
        System.out.println("CLOSED");
    }

}
