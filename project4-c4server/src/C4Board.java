
/**
 * @author Joseph Cumbo (jwc6999)
 */
public class C4Board implements C4BoardIntf {

    public static final int PLAYER_A = 1;
    public static final int PLAYER_B = 2;
    private final int[][] board = new int[C4BoardIntf.COLS][C4BoardIntf.ROWS];

    @Override
    public boolean hasPlayer1Marker(int r, int c) {
        return board[c][r] == PLAYER_A;
    }

    @Override
    public boolean hasPlayer2Marker(int r, int c) {
        return board[c][r] == PLAYER_B;
    }

    @Override
    public int[] hasWon() {
        return hasWon(board, PLAYER_A, PLAYER_B);
    }

    public static int[] hasWon(int[][] board, int player1, int player2) {
        //does someone win in cols
        for (int x = 0; x < C4BoardIntf.COLS; x++) {
            for (int y = 0; y < C4BoardIntf.ROWS; y++) {
                if (board[x][y] == player1 || board[x][y] == player2) {
                    int checkVal = board[x][y];
                    if (x > 3) {
                        //we cannot have four in a row if there are not four left
                        continue;
                    }
                    int count = 1;
                    for (int i = x + 1; i < C4BoardIntf.COLS; i++) {
                        if (board[i][y] != checkVal) {
                            break;
                        }
                        count++;
                        if (count == 4) {
                            int[] wins = new int[4];
                            wins[0] = y;
                            wins[1] = x;
                            wins[2] = y;
                            wins[3] = i;
                            return wins;
                        }
                    }
                }
            }
        }

        //does someone win in rows
        for (int y = 0; y < C4BoardIntf.ROWS; y++) {
            for (int x = 0; x < C4BoardIntf.COLS; x++) {
                if (board[x][y] == player1 || board[x][y] == player2) {
                    int checkVal = board[x][y];
                    if (y > 3) {
                        continue;
                    }

                    int count = 1;
                    for (int i = y + 1; i < C4BoardIntf.ROWS; i++) {
                        if (board[x][i] != checkVal) {
                            break;
                        }
                        count++;
                        if (count == 4) {
                            int[] wins = new int[4];
                            wins[0] = y;
                            wins[1] = x;
                            wins[2] = i;
                            wins[3] = x;
                            return wins;
                        }
                    }
                }
            }
        }

        for (int x = 0; x < C4BoardIntf.COLS; x++) {
            for (int y = 0; y < C4BoardIntf.ROWS; y++) {
                if (board[x][y] == player1 || board[x][y] == player2) {
                    int val = board[x][y];
                    int xSave = x;
                    int ySave = y;
                    int count = 1;
                    while (xSave + 1 < C4BoardIntf.COLS && ySave + 1 < C4BoardIntf.ROWS) {
                        xSave++;
                        ySave++;
                        if (board[xSave][ySave] != val) {
                            break;
                        }
                        count++;
                        if (count == 4) {
                            int[] win = new int[4];
                            win[0] = y;
                            win[1] = x;
                            win[2] = ySave;
                            win[3] = xSave;
                            return win;
                        }
                    }
                }
            }
        }

        for (int x = 0; x < C4BoardIntf.COLS; x++) {
            for (int y = 0; y < C4BoardIntf.ROWS; y++) {
                if (board[x][y] == player1 || board[x][y] == player2) {
                    int val = board[x][y];
                    int xSave = x;
                    int ySave = y;
                    int count = 1;
                    while (xSave - 1 >= 0 && ySave + 1 < C4BoardIntf.ROWS) {
                        xSave--;
                        ySave++;
                        if (board[xSave][ySave] != val) {
                            break;
                        }
                        count++;
                        if (count == 4) {
                            int[] win = new int[4];
                            win[0] = y;
                            win[1] = x;
                            win[2] = ySave;
                            win[3] = xSave;
                            return win;
                        }
                    }
                }
            }
        }


        return null;
    }

    public void set(int player, int r, int c) {
        board[c][r] = player;
    }

    public void clear() {
        for (int c = 0; c < C4BoardIntf.COLS; c++) {
            for (int r = 0; r < C4BoardIntf.ROWS; r++) {
                board[c][r] = 0;
            }
        }
    }

}
