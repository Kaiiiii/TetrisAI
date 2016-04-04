import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
        AnalysisResult result = new StateAnalyser(state).analyse(9);
        assertThat(result.getAggregateHeight(), is(equalTo(17)));
    }

    @Test
    public void Analyst_calculates_holes_correctly() {
        ControlledState state = new ControlledState();
        state.forceMove(3, 1, 0);
        state.forceMove(4, 3, 2);
        state.forceMove(0, 0, 6);

        state.setNextPiece(2);
        AnalysisResult result = new StateAnalyser(state).analyse(9);
        assertThat(result.getHolesCount(), is(equalTo(5)));
    }

    @Test
    public void Analyst_calculates_roughness_correctly() {
        ControlledState state = new ControlledState();
        state.forceMove(3, 1, 0);
        state.forceMove(4, 3, 3);
        state.forceMove(0, 0, 2);
        state.forceMove(1, 0, 7);

        state.setNextPiece(6);
        AnalysisResult result = new StateAnalyser(state).analyse(16);
        assertThat(result.getBumpiness(), is(equalTo(13)));
    }

    @Test
    public void Analyst_calculates_complete_lines_correctly() {
        ControlledState state = new ControlledState();
        state.forceMove(4, 3, 1);
        state.forceMove(3, 3, 1);
        state.forceMove(0, 0, 4);
        state.forceMove(0, 0, 6);
        state.forceMove(0, 0, 8);
        state.forceMove(2, 3, 4);
        state.forceMove(2, 3, 7);

        state.setNextPiece(1);
        AnalysisResult result = new StateAnalyser(state).analyse(0);
        assertThat(result.getCompleteLines(), is(equalTo(2)));
    }
}
