
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;


/**
 * @author Joseph Cumbo (mooman219)
 */
public class ConnectFourServer {

    public static void main(String[] args) {
//        args = new String[]{"localhost", "31456"};
        if (args.length < 2) {
            System.err.println("Usage: ConnectFourServer <server host> <server port>");
            return;
        }
        int port;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Port must be a valid number.");
            return;
        }
        InetAddress bindAddr;
        try {
            bindAddr = InetAddress.getByName(args[0]);
        } catch (UnknownHostException ex) {
            System.err.println("Server host must be valid.");
            return;
        }
        ConnectFourServer server;
        try {
            server = new ConnectFourServer(port, bindAddr);
        } catch (IOException ex) {
            System.err.println("Unable to bind to " + args[0] + ":" + args[1]);
            return;
        }
        while (true) {
            server.nextSession();
        }
    }

    public final int port;
    public final InetAddress bindAddr;
    public final ServerSocket socket;

    public ConnectFourServer(int port, InetAddress bindAddr) throws IOException {
        this.port = port;
        this.bindAddr = bindAddr;
        this.socket = new ServerSocket(port, 16, bindAddr);
    }

    public void nextSession() {
        Connection.ToClient playerA = null;
        Connection.ToClient playerB = null;
        try {
            playerA = new Connection.ToClient(socket.accept());
            playerB = new Connection.ToClient(socket.accept());
            //System.out.println("[Created new session.]");
            playerA.start();
            playerB.start();
            new Session(playerA, playerB).start();
        } catch (IOException ex) {
            System.err.println("Unable to establish session.");
            if (playerA != null) {
                playerA.disconnect();
            }
            if (playerB != null) {
                playerB.disconnect();
            }
        }
    }

    public static class Session extends Thread {

        private final int[][] board = new int[C4BoardIntf.COLS][C4BoardIntf.ROWS];
        public final Connection.ToClient playerA;
        public final Connection.ToClient playerB;
        public int turn = 2;
        public boolean playerAName = false;
        public boolean playerBName = false;

        public Session(Connection.ToClient playerA, Connection.ToClient playerB) {
            this.playerA = playerA;
            this.playerB = playerB;
            playerA.getOutbound().add(new Message.ToClient.Number(1));
            playerB.getOutbound().add(new Message.ToClient.Number(2));
        }

        public void nextTurn() {
            if (turn == 1) {
                turn = 2;
            } else {
                turn = 1;
            }
            playerA.getOutbound().add(new Message.ToClient.Turn(turn));
            playerB.getOutbound().add(new Message.ToClient.Turn(turn));
        }

        public void makeMove(int player, int col) {
            if (player == turn) {
                if (col >= 0 && col < C4BoardIntf.COLS) {
                    for (int row = C4BoardIntf.ROWS - 1; row >= 0; row--) {
                        if (board[col][row] == 0) {
                            board[col][row] = player;
                            playerA.getOutbound().add(new Message.ToClient.Add(player, row, col));
                            playerB.getOutbound().add(new Message.ToClient.Add(player, row, col));
                            if (C4Board.hasWon(board, 1, 2) != null) {
                                playerA.getOutbound().add(new Message.ToClient.Turn(0));
                                playerB.getOutbound().add(new Message.ToClient.Turn(0));
                            } else {
                                nextTurn();
                            }
                            return;
                        }
                    }
                }
            }
        }

        public void setName(int player, String name) {
            switch (player) {
                case 1:
                    if (!playerAName) {
                        playerAName = true;
                        playerA.getOutbound().add(new Message.ToClient.Name(1, name));
                        playerB.getOutbound().add(new Message.ToClient.Name(1, name));
                        if (playerBName) {
                            nextTurn();
                        }
                    }
                    break;
                case 2:
                    if (!playerBName) {
                        playerBName = true;
                        playerA.getOutbound().add(new Message.ToClient.Name(2, name));
                        playerB.getOutbound().add(new Message.ToClient.Name(2, name));
                        if (playerAName) {
                            nextTurn();
                        }
                    }
                    break;
            }
        }

        public void clearBoard() {
            for (int c = 0; c < C4BoardIntf.COLS; c++) {
                for (int r = 0; r < C4BoardIntf.ROWS; r++) {
                    board[c][r] = 0;
                }
            }
            playerA.getOutbound().add(new Message.ToClient.Clear());
            playerB.getOutbound().add(new Message.ToClient.Clear());
            nextTurn();
        }

        @Override
        public void run() {
            while (playerA.isConnected() && playerB.isConnected()) {
                while (!playerA.getInbound().isEmpty()) {
                    Message.ToServer message = playerA.getInbound().remove();
                    //System.out.println("Processing " + message.toString());
                    message.apply(1, this);
                }
                while (!playerB.getInbound().isEmpty()) {
                    Message.ToServer message = playerB.getInbound().remove();
                    //System.out.println("Processing " + message.toString());
                    message.apply(2, this);
                }
            }
            playerA.disconnect();
            playerB.disconnect();
            //System.out.println("[Session closed.]");
        }
    }
}
