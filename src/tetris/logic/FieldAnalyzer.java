package tetris.logic;

import tetris.ui.State;

/**
 * Created by maianhvu on 24/03/2016.
 */
public class FieldAnalyzer {

    /**
     * Properties
     */
    private int[][] previewState;
    private State currentState = null;

    public FieldAnalyzer(State s)  {
        this.previewState = null;
        currentState = s;
    }

    public Analysis analyze(int[] move) {

        // Find the row to place
        

        // Lock in the piece

        
        // Calculate analysis
        return new Analysis(this.previewState);
    }

 
}
