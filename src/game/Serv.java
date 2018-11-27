package game;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Waits for Players
 * Controls the state of the game and communicates with the Players
 */
public class Serv {
    // keep track of the players, player threads, and streams for each player
    private static ArrayList<Socket> players = new ArrayList<>();
    private static ArrayList<ObjectOutputStream> outputs = new ArrayList<>();
    private static ArrayList<ObjectInputStream> inputs = new ArrayList<>();
    private static ArrayList<Spawn> spawns = new ArrayList<>();
    // allows access to each player's mark
    private static char[] marks = {'X', 'O'};
    // store row and column of last move
    private static int row;
    private static int col;

    /**
     * Begins, plays, and ends game.
     * @param args
     */
    public static void main(String[] args) {
        try {
            Board b = new Board(13);
            ServerSocket sock = new ServerSocket(4242);
            System.out.println(LocalDateTime.now() + " : Server log : \n");
            // Wait for players
            for (int i = 0; i < 2; i++) {
                System.out.println(LocalDateTime.now() + " : Waiting on Player " + (i + 1) + ".");
                spawns.add(new Spawn(4242, i, i == 0));
                spawns.get(i).start();
                Socket temp = sock.accept();
                outputs.add(new ObjectOutputStream(temp.getOutputStream()));
                inputs.add(new ObjectInputStream(temp.getInputStream()));

                players.add(temp);

                System.out.println(LocalDateTime.now() + " : Player " + (i + 1) + " connected.\n");
                outputs.get(i).writeObject(b);
            }
            // Play until and end condition is met
            int currPlayer = 0;
            while (true) {
                currPlayer %= 2;
                ObjectOutputStream out = outputs.get(currPlayer);
                ObjectInputStream in = inputs.get(currPlayer);
                boolean choseMove = false;
                System.out.println(LocalDateTime.now() + " : Player " + (currPlayer + 1) + "'s turn.");
                out.reset();
                out.writeObject("go");
                out.reset();
                out.writeObject(b);
                Object input;
                // prompt for a move until a valid move is received
                while (!choseMove) {
                    input = in.readObject();
                    if (input instanceof Object[]) {
                        Object[] inputArr = ((Object[]) input);
                        row = (Integer) inputArr[0];
                        col = (Integer) inputArr[1];
                    }
                    choseMove = b.canMove(row, col);
                    out.reset();
                    out.writeObject(choseMove);
                }
                // determine if the last move won the game
                boolean won = b.makeMove(row, col, marks[currPlayer], currPlayer);
                out.reset();
                out.writeObject(b);
                // end condition: game is won
                // tell all players that the current player won and end the game, close everything and exit
                if (won) {
                    System.out.println(LocalDateTime.now() + " : Player " + (currPlayer + 1) + " won!");
                    for (ObjectOutputStream o : outputs) {
                        out.reset();
                        o.writeObject("");
                        out.reset();
                        o.writeObject(currPlayer + "|Win");
                    }
                    sock.close();
                    for (Spawn s : spawns) {
                        try {
                            s.join();
                        } catch (Exception e) {
                        }
                    }
                    System.exit(0);
                    // end condition: game is tied
                    // tell all players that the game is tie and end the game, close everything and exit
                } else if (b.full()) {
                    System.out.println(LocalDateTime.now() + " : Tie.");
                    for (ObjectOutputStream o : outputs) {
                        out.reset();
                        o.writeObject("");
                        out.reset();
                        o.writeObject("tie");
                    }
                    sock.close();
                    for (Spawn s : spawns) {
                        try {
                            s.join();
                        } catch (Exception e) {
                        }
                    }
                    System.exit(1);
                    // tell the current player that their turn is over
                } else{
                    out.reset();
                    out.writeObject("other");
                }
                currPlayer++;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
