package tetris.logic;

import tetris.ui.State;

/**
 * Created by maianhvu on 24/03/2016.
 */
public abstract class HeuristicsPlayer implements Player {

    @Override
    public int[] getNextMove(State s, int[][] legalMoves) {

        Double heuristicOutput = null;
        FieldAnalyzer analyzer = new FieldAnalyzer(s);

        int[] desiredAction = null;
        
        for (int[] legalMove : legalMoves) {

            Analysis result = analyzer.analyze(legalMove);
            // ... process and compare analysis
            // ... update candidate move

            // Calculate heuristics and update accordingly
            double heuristics = this.calculateHeuristics(result);

            if (heuristicOutput == null || heuristics > heuristicOutput) {
                desiredAction = legalMove;
                heuristicOutput = heuristics;
            }
        }

        return desiredAction;
    }

    protected abstract double calculateHeuristics(Analysis analysis);
}
