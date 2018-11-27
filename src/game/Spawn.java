package game;


import java.io.*;

/**
 * Creates a thread that runs a player in the game of tic-tac-toe
 */
public class Spawn extends Thread implements Runnable {

    // the process containing the player
    private ProcessBuilder pb;

    /**
     * Creates the corresponding process based on what the Player is (human or computer)
     * @param port passed as an argument to the Player, what the socket should connect to
     * @param playerNum passed as an argument to the Player, which player this is
     * @param human passed as an argument to the Player, whether this Player is human or not
     */
    public Spawn(int port, int playerNum, boolean human) {
        String s = Spawn.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1);
        if (human) {
            pb = new ProcessBuilder("cmd", "/c", "start", "java", "-cp", s, "game.Player",
                    Integer.toString(port), Integer.toString(playerNum + 1), "true");
        }
        else{
            pb = new ProcessBuilder("java", "-cp", s, "game.Player",
                    Integer.toString(port), Integer.toString(playerNum + 1), "false");
        }
    }

    /**
     * Start the process and wait for exit
     */
    @Override
    public void run() {
        try {
            Process p = pb.start();
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
