package ai;

import logic.Analysis;
import logic.HeuristicsPlayer;

/**
 * Created by maianhvu on 24/03/2016.
 */
public class UniversalPlayer extends HeuristicsPlayer {

    @Override
    protected int calculateHeuristics(Analysis analysis) {
        return -85 * analysis.getAggregateHeight() +
                -91 * analysis.getRoughness() +
                -99 * analysis.getHolesCount() +
                -19 * analysis.getCellsCount() +
                -53 * analysis.getHighestSlope();
    }
}
