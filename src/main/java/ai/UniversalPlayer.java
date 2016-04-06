package ai;

import logic.Analysis;
import logic.HeuristicsPlayer;

/**
 * Created by maianhvu on 24/03/2016.
 */
public class UniversalPlayer extends HeuristicsPlayer {

    @Override
    protected double calculateHeuristics(Analysis analysis) {
        return -53.0 * analysis.getAggregateHeight() +
                -17.0 * analysis.getRoughness() +
                -87.0 * analysis.getHolesCount() +
                -94.0 * analysis.getCellsCount() +
                -14.0 * analysis.getHighestSlope();
    }
}
