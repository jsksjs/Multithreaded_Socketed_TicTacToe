package game;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Represents either a human or computer player for a game of tic-tac-toe using sockets and threads
 */
public class Player {

    /**
     * Runs local game play
     * @param args
     */
    public static void main(String[] args) {
        try {
            boolean human = Boolean.valueOf(args[2]);
            String status = "";
            Pattern numbers = Pattern.compile("\\d+([,]\\d+)");
            if (!human)
                System.setOut(new PrintStream(new PrintStream(new OutputStream() { public void write(int a) { } })));
            else
                System.out.println("You are Player " + args[1]);
            Socket sock = new Socket("localhost", Integer.valueOf(args[0]));
            ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());
            ObjectInputStream is = new ObjectInputStream(sock.getInputStream());
            Board b = new Board(1);
            // Continue game until an end condition is communicated by the server
            while (!status.equals("done") && !status.equals("tie") && !status.contains("Win")) {
                // Get the status of the game
                Object input = is.readObject();
                if (input instanceof String) {
                    status = (String) input;
                }
                if (status.equals("go")) {
                    try {
                        // Must get the current board from server
                        input = is.readObject();
                        if (input instanceof Board) {
                            b = (Board) input;
                        }
                        b.printBoard();
                        if (human)
                            System.out.println("\n~~~Your turn.~~~\n");
                        boolean valid = false;
                        // Prompt player until a valid move is accepted by the server
                        while (!valid) {
                            int col;
                            int row;
                            // human player needs more I/O and format checking
                            if (human) {
                                System.out.println("Enter a valid space: row,col");
                                Scanner s = new Scanner(System.in);
                                String line = s.nextLine();
                                // if entered space matches expected format
                                if (numbers.matcher(line).matches()) {
                                    String[] splitLine = line.split(",");
                                    row = Integer.parseInt(splitLine[0]);
                                    col = Integer.parseInt(splitLine[1]);
                                    os.writeObject(new Object[]{row, col});
                                    input = is.readObject();
                                    if (input instanceof Boolean) {
                                        valid = (boolean) input;
                                        if (!valid)
                                            System.out.println("Invalid space.");
                                    }
                                }
                            // computer just needs a random space to communicate
                            } else {
                                row = (int) (Math.random() * b.getLength());
                                col = (int) (Math.random() * b.getLength());
                                os.writeObject(new Object[]{row, col});
                                input = is.readObject();
                                if (input instanceof Boolean) {
                                    valid = (boolean) input;
                                }
                            }
                        }
                        input = is.readObject();
                        if (input instanceof Board) {
                            b = (Board) input;
                        }
                        b.printBoard();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                // print that it is the other player's turn
                } else if (human && status.equals("other")) {
                    System.out.println("~~~Other player's turn.~~~");
                }
            }
            // once one of these end conditions have been met (loop is exited)
            // the final board is shown and an end message is printed
            // player has to press ENTER to close the program
            if (human && status.equals("tie")) {
                b.printBoard();
                System.out.println("It's a tie. (Press ENTER to exit.)");
            } else if (human && status.contains("Win")) {
                b.printBoard();
                System.out.println("Player " + (Integer.valueOf(status.split("\\|")[0]) + 1) + " wins! (Press ENTER to exit.)");
            }
            if (human) {
                Scanner s = new Scanner(System.in);
                s.nextLine();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}