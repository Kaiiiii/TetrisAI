package ai;

import logic.Analysis;
import logic.HeuristicsPlayer;

/**
 * Created by maianhvu on 24/03/2016.
 */
public class MinimizeRoughnessAndHolesPlayer extends HeuristicsPlayer {
    @Override
    protected int calculateHeuristics(Analysis analysis) {
        return -1 * analysis.getRoughness() +
                -5 * analysis.getHolesCount();
    }
}
