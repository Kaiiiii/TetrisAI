package tetris.ai;

import java.util.Random;

import tetris.ui.State;
import tetris.ui.TFrame;

public class Player {

    //implement this function to have a working system
    public int[] pickMove(State s, int[][] legalMoves) {
        
        int[][] pHeights = s.getpHeight();
        int[][] pWidths = s.getpWidth();
        int[] pOrients = s.getpOrients();
        
        int possibleMoves = legalMoves.length;
        
        Random random = new Random();
        int[] move = legalMoves[random.nextInt(possibleMoves - 1)];
        
        for (int i = 0; i < possibleMoves; ++i) {
            // Search through the next set of moves here!
        }
        
        return move;
    }
    
    public static void main(String[] args) {
        State s = new State();
        new TFrame(s);
        Player p = new Player();
        while(!s.hasLost()) {
            
            try {
                s.makeMove(p.pickMove(s,s.legalMoves()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            s.draw();
            s.drawNext(0,0);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("You have completed "+s.getRowsCleared()+" rows.");
    }
    
}
