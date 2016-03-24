package ai;

import logic.Analysis;
import logic.HeuristicsPlayer;

/**
 * Created by maianhvu on 24/03/2016.
 */
public class MinimizeRoughnessPlayer extends HeuristicsPlayer {

    @Override
    protected int calculateHeuristics(Analysis analysis) {
        return -analysis.getRoughness();
    }
}
