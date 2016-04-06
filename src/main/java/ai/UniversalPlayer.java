package ai;

import logic.Analysis;
import logic.HeuristicsPlayer;

/**
 * Created by maianhvu on 24/03/2016.
 */
public class UniversalPlayer extends HeuristicsPlayer {

    @Override
    protected double calculateHeuristics(Analysis analysis) {
        return -100.0 * analysis.getAggregateHeight() +
                -18.0 * analysis.getRoughness() +
                -1.0 * analysis.getHolesCount() +
                -100.0 * analysis.getCellsCount() +
                -1.0 * analysis.getHighestSlope();
    }
}
