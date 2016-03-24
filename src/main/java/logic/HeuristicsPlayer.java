package logic;

import entities.Tetromino;

/**
 * Created by maianhvu on 24/03/2016.
 */
public abstract class HeuristicsPlayer implements Player {

    @Override public Game.Action getNextMove(Game game) {
        Tetromino.Rotation[] meaningfulRotations =
                game.getNextPiece().getUsefulRotations();

        Game.Action desiredAction = null;
        Integer heuristicOutput = null;

        for (Tetromino.Rotation rotation : meaningfulRotations) {
            int[] availablePositions = game.getAvailablePositionsFor(rotation);

            // Create the action
            for (int position : availablePositions) {
                Game.Action action = new Game.Action(rotation, position);
                Analysis result = FieldAnalyzer.getInstance().analyze(game, action);

                // Skip invalid moves
                if (result == null) continue;

                // Calculate heuristics and update accordingly
                int heuristics = this.calculateHeuristics(result);

                if (heuristicOutput == null || heuristics > heuristicOutput) {
                    desiredAction = action;
                    heuristicOutput = heuristics;
                }
            }
        }

        return desiredAction;
    }

    protected abstract int calculateHeuristics(Analysis analysis);
}
