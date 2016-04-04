package tetris.ai;

import tetris.logic.Analysis;
import tetris.logic.HeuristicsPlayer;

/**
 * Created by maianhvu on 24/03/2016.
 */
public class UniversalPlayer extends HeuristicsPlayer {

    @Override
    protected double calculateHeuristics(Analysis analysis) {
        return -17.0 * analysis.getAggregateHeight() +
                -11.0 * analysis.getRoughness() +
                -82.0 * analysis.getHolesCount() +
                -12.0 * analysis.getCellsCount() +
                -16.0 * analysis.getHighestSlope();
//        return -0 * analysis.getAggregateHeight() +
//                -0 * analysis.getRoughness() +
//                -1 * analysis.getHolesCount() +
//                -0 * analysis.getCellsCount() +
//                -0 * analysis.getHighestSlope();
    }
}
