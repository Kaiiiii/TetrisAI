package tetris.ai;

import java.util.Random;

import tetris.ui.State;
import tetris.ui.TFrame;

public class Player {

    // implement this function to have a working system
    public int[] pickMove(State s, int[][] legalMoves) {
        
        int possibleMoves = legalMoves.length;
        
        // Random Player LOL
        Random random = new Random();
        int[] candidateMove = legalMoves[random.nextInt(possibleMoves - 1)];
        
//        for (int i = State.ROWS - 1; i >= 0; --i) {
//            String output = new String();
//            for (int j = 0; j < State.COLS ; ++j) {
//                output += " " + s.getField()[i][j] + " ";                
//            }
//            System.out.println(output);
//        }
        
//        System.out.println();

        
        // Real Stuff Here ...
        UniversalPlayer smartPlayer = new UniversalPlayer();
        
        candidateMove = smartPlayer.getNextMove(s, legalMoves);
        
        return candidateMove;
    }

    public static void main(String[] args) {
        State s = new State();
        new TFrame(s);
        Player p = new Player();
        while (!s.hasLost()) {

            try {
                s.makeMove(p.pickMove(s, s.legalMoves()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            s.draw();
            s.drawNext(0, 0);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("You have completed " + s.getRowsCleared() + " rows.");
    }

}
