package ai;

import logic.Analysis;
import logic.HeuristicsPlayer;

/**
 * Created by maianhvu on 24/03/2016.
 */
public class UniversalPlayer extends HeuristicsPlayer {

    @Override
    protected int calculateHeuristics(Analysis analysis) {
        return -5 * analysis.getHeight() +
                -3 * analysis.getRoughness() +
                -29 * analysis.getHolesCount() +
                -7 * analysis.getCellsCount() +
                -5 * analysis.getHighestSlope();
    }
}
