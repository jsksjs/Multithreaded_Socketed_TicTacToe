package game;

import java.io.Serializable;

/**
 * Creates and keeps track of a board that will be passed around sockets
 */
public class Board implements Serializable {
    private char[][] board;
    private int[] pointCount = new int[28];

    /**
     * Constructs a board with a given size (will be 13 for this project)
     * Fills the board with periods to communicate blank spaces
     * @param size the size of the board to be created (always square)
     */
    public Board(int size) {
        board = new char[size][size];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j] = '.';
            }
        }
        for (int i = 0; i < pointCount.length; i++) {
            pointCount[i] = 0;
        }
    }

    /**
     * Always fills a space on the board with a mark
     * Adds the player score once the move is made
     * Checks for a winning move
     * @param row row of the mark
     * @param col column of the mark
     * @param playerMark the mark to place in the row,col
     * @return if the move won the game
     */
    public boolean makeMove(int row, int col, char playerMark, int playerNum) {
        board[row][col] = playerMark;
        if (playerNum == 0) {
            pointCount[row] += 1;
            pointCount[board.length + col] += 1;
            if (row == col)
                pointCount[board.length * 2] += 1;
            if (12 - col == row)
                pointCount[board.length + 1] += 1;
        } else if (playerNum == 1) {
            pointCount[row] -= 1;
            pointCount[board.length + col] -= 1;
            if (row == col)
                pointCount[board.length * 2] -= 1;
            if (12 - col == row)
                pointCount[board.length + 1] -= 1;
        }
        return won();
    }

    /**
     * Determines if a move can be made in the given row,col
     * @param row row of the space
     * @param col column of the space
     * @return if the space is valid (empty)
     */
    public boolean canMove(int row, int col) {
        if (board[row][col] == '.') {
            return true;
        }
        return false;
    }

    /**
     * Prints the current game board
     */
    public void printBoard() {
        int size = board.length;
        System.out.println("\nCurrent board: ");
        // col nums
        StringBuilder sb = new StringBuilder("    ");
        for (int col = 0; col < size; col++) {
            sb.append(col).append("  ");
        }
        System.out.println(sb.toString());
        // data
        for (int row = 0; row < size; row++) {
            sb = new StringBuilder();
            sb.append(row);
            for (int j = 4 - String.valueOf(row).length(); j > 0; j--) {
                sb.append(" ");
            }
            for (int col = 0; col < size; col++) {
                sb.append(board[row][col]);
                for (int j = String.valueOf(col).length() + 1; j > 0; j--) {
                    sb.append(" ");
                }
            }
            System.out.println(sb.toString());
        }
        System.out.println();
    }

    /**
     * Checks a space for a relative win
     * @return whether the game has been won
     */
    private boolean won() {
        for (int i = 0; i < pointCount.length; i++) {
            if (pointCount[i] == board.length || pointCount[i] == -board.length) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks the board for capacity
     * @return if the entire board is filled (tie condition)
     */
    public boolean full() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] == '.')
                    return false;
            }
        }
        return true;
    }

    /**
     * Gets the length of the board (board is always square)
     * @return length of board
     */
    public int getLength(){
        return board.length;
    }
}