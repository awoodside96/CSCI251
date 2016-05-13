
import java.io.IOException;

/**
 * @author Joseph Cumbo (mooman219)
 */
public final class ConnectFour {

    public static void main(String[] args) {
//        args = new String[]{"localhost", "31456", "Joe"};
        if (args.length != 3) {
            System.err.println("Usage: ConnectFour <host> <port> <playername>");
            System.err.println(
                    "The project said we can implement the command line arguments however\n"
                    + "we want to. The example provided in the project description \n"
                    + "was if we set up the project as UDP. I chose to implement my\n"
                    + "client/server as binary TCP, so the client only requires 3 args.\n");
            return;
        }
        int port;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Port must be a valid number.");
            return;
        }
        Connection.ToServer connection;
        try {
            connection = new Connection.ToServer(args[0], port);
            connection.start();
        } catch (IOException ex) {
            System.err.println("Unable to connect to server.");
            return;
        }
        ConnectFour game = new ConnectFour(connection, args[2]);
        while (connection.isConnected()) {
            while (!connection.getInbound().isEmpty()) {
                Message.ToClient message = connection.getInbound().remove();
                //System.out.println("Processing " + message.toString());
                message.apply(game);
            }
        }
        game.ui.close();
    }

    private final Connection.ToServer connection;
    private final C4Board board = new C4Board();
    private final C4UI ui;

    private int turn = 0;
    private int id = -1;
    private int opponentid = -2;
    private String opponentname = "";

    public ConnectFour(Connection.ToServer connection, String playername) {
        this.connection = connection;
        this.ui = new C4UI(this, board, playername);
        connection.getOutbound().add(new Message.ToServer.Join(playername));
    }

    public void setName(int player, String name) {
        if (id != player) {
            opponentid = player;
            opponentname = name;
        }
    }

    public void requestAdd(int c) {
        if (turn == id && opponentid != -2) {
            connection.getOutbound().add(new Message.ToServer.Add(id, c));
        }
    }

    public void requestClear() {
        if (opponentid != -1) {
            connection.getOutbound().add(new Message.ToServer.Clear());
        }
    }

    public void addMove(int id, int r, int c) {
        board.set(id, r, c);
        ui.update();
    }

    public void clearBoard() {
        board.clear();
        ui.update();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        if (turn == 0) {
            ui.setMessage("Game over");
        } else if (turn == id) {
            ui.setMessage("Your turn");
        } else {
            ui.setMessage(opponentname + "'s turn");
        }
        this.turn = turn;
        ui.setNewGameEnabled(true);
    }

}
