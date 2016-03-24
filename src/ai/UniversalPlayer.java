package ai;

import logic.Analysis;
import logic.HeuristicsPlayer;

/**
 * Created by maianhvu on 24/03/2016.
 */
public class UniversalPlayer extends HeuristicsPlayer {

    @Override
    protected int calculateHeuristics(Analysis analysis) {
        return -7 * analysis.getHeight() +
                -2 * analysis.getRoughness() +
                -3 * analysis.getHolesCount() +
                -5 * analysis.getCellsCount() +
                -1 * analysis.getHighestSlope();
    }
}
