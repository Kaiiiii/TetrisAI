package tetris.logic;

import tetris.ui.State;

/**
 * Created by maianhvu on 24/03/2016.
 */
public interface Player {

    int[] getNextMove(State s, int[][] legalMoves);

}
