import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by maianhvu on 04/04/2016.
 */
public class StateAnalyserTest {

    @Test
    public void Analyst_calculates_aggregate_height_correctly() {
        ControlledState state = new ControlledState();
        state.forceMove(2, 0, 0);
        state.forceMove(4, 3, 2);
        state.forceMove(0, 0, 2);

        state.setNextPiece(1);
        AnalysisResult result = new StateAnalyser(state, 9).analyse();
    }

    @Test
    public void Analyst_calculates_well_sums_correctly() {
        ControlledState state = new ControlledState();
        state.forceMove(1, 0, 2);
        state.forceMove(0, 0, 4);
        state.forceMove(4, 3, 7);

        state.setNextPiece(1);
        AnalysisResult result = new StateAnalyser(state, 0).analyse();
        assertThat(result.getWellSums(), is(equalTo(15)));
    }
}
