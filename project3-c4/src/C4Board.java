
/**
 * @author Joseph Cumbo (jwc6999)
 */
public class C4Board implements C4BoardIntf {

    private int player1 = 1;
    private int player2 = 2;
    private final int[][] board = new int[C4BoardIntf.COLS][C4BoardIntf.ROWS];

    public int getPlayer1() {
        return player1;
    }

    public void setPlayer1(int player1) {
        this.player1 = player1;
    }

    public int getPlayer2() {
        return player2;
    }

    public void setPlayer2(int player2) {
        this.player2 = player2;
    }

    @Override
    public boolean hasPlayer1Marker(int r, int c) {
        return board[c][r] == player1;
    }

    @Override
    public boolean hasPlayer2Marker(int r, int c) {
        return board[c][r] == player2;
    }

    @Override
    public int[] hasWon() {
        int winner;
        int start;
        int count;
        for (int c = 0; c < C4BoardIntf.COLS; c++) {
            winner = 0;
            start = 0;
            for (int r = 0; r < C4BoardIntf.ROWS; r++) {
                if (winner != board[c][r]) {
                    winner = board[c][r];
                    start = r;
                }
                if (winner != 0 && r - start == 3) {
                    return new int[]{start, c, r, c};
                }
            }
        }
        for (int r = 0; r < C4BoardIntf.ROWS; r++) {
            winner = 0;
            start = 0;
            for (int c = 0; c < C4BoardIntf.COLS; c++) {
                if (winner != board[c][r]) {
                    winner = board[c][r];
                    start = c;
                }
                if (winner != 0 && c - start == 3) {
                    return new int[]{r, start, r, c};
                }
            }
        }
        /*
         * Diagonal
         */
        for (int r = 0; r < C4BoardIntf.ROWS; r++) {
            count = 0;
            winner = 0;
            int startRow = 0;
            int startCol = 0;
            int row, col;
            for (row = r, col = 0; row < C4BoardIntf.ROWS && col < C4BoardIntf.COLS; row++, col++) {
                count++;
                if (board[col][row] != winner) {
                    winner = board[col][row];
                    startRow = row;
                    startCol = col;
                    count = 0;
                }
                if (winner != 0 && count == 3) {
                    return new int[]{startRow, startCol, row, col};
                }
            }
            count = 0;
            winner = 0;
            for (row = r, col = C4BoardIntf.COLS - 1; row < C4BoardIntf.ROWS && col >= 0; row++, col--) {
                count++;
                if (board[col][row] != winner) {
                    winner = board[col][row];
                    startRow = row;
                    startCol = col;
                    count = 0;
                }
                if (winner != 0 && count == 3) {
                    return new int[]{startRow, startCol, row, col};
                }
            }
        }
        for (int c = 1; c < C4BoardIntf.COLS; c++) {
            count = 0;
            winner = 0;
            int startRow = 0;
            int startCol = 0;
            int row, col;
            for (row = 0, col = c; row < C4BoardIntf.ROWS && col < C4BoardIntf.COLS; row++, col++) {
                count++;
                if (board[col][row] != winner) {
                    winner = board[col][row];
                    startRow = row;
                    startCol = col;
                    count = 0;
                }
                if (winner != 0 && count == 3) {
                    return new int[]{startRow, startCol, row, col};
                }
            }
            count = 0;
            winner = 0;
            for (row = C4BoardIntf.ROWS - 1, col = c; row >= 0 && col < C4BoardIntf.COLS; row--, col++) {
                count++;
                if (board[col][row] != winner) {
                    winner = board[col][row];
                    startRow = row;
                    startCol = col;
                    count = 0;
                }
                if (winner != 0 && count == 3) {
                    return new int[]{startRow, startCol, row, col};
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
