
import java.io.IOException;

/**
 * @author Joseph Cumbo (mooman219)
 */
public final class ConnectFour {

    public static void main(String[] args) {
//        args = new String[]{"glados.cs.rit.edu", "42042", "JoeA"};
        if (args.length < 3) {
            System.err.println("Usage: ConnectFour <host> <port> <playername>");
            return;
        }
        int port;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Port must be a valid number.");
            return;
        }
        Connection connection;
        try {
            connection = new Connection(args[0], port);
            connection.start();
        } catch (IOException ex) {
            System.err.println("Unable to connect to server.");
            return;
        }
        ConnectFour game = new ConnectFour(connection, args[2]);
        game.requestJoin();
        while (true) {
            while (!connection.getInbound().isEmpty()) {
                InboundMessage message = connection.getInbound().remove();
                System.out.println("Read " + message.toString());
                message.apply(game);
            }
        }
    }

    private final String playername;
    private final Connection connection;
    private final C4Board board = new C4Board();
    private final C4UI ui;

    private int turn = 0;
    private int id = -1;
    private int opponentid = -2;
    private String opponentname = "";

    public ConnectFour(Connection connection, String playername) {
        this.connection = connection;
        this.playername = playername;
        this.ui = new C4UI(this, board, playername);
    }

    public void requestJoin() {
        if (id == -1) {
            connection.getOutbound().add(new OutboundMessage.Join(playername));
        }
    }

    public void requestAdd(int c) {
        if (turn == id && opponentid != -2) {
            connection.getOutbound().add(new OutboundMessage.Add(id, c));
        }
    }

    public void requestClear() {
        if (opponentid != -1) {
            connection.getOutbound().add(new OutboundMessage.Clear());
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
        board.setPlayer1(id);
        this.id = id;
    }

    public int getOpponentid() {
        return opponentid;
    }

    public void setOpponentid(int opponentid) {
        board.setPlayer2(opponentid);
        this.opponentid = opponentid;
    }

    public String getOpponentname() {
        return opponentname;
    }

    public void setOpponentname(String opponentname) {
        this.opponentname = opponentname;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        if(turn == 0) {
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
