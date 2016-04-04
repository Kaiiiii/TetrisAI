import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by maianhvu on 04/04/2016.
 */
public class FieldAnalystTest {

    @Test
    public void Analyst_calculates_aggregate_height_correctly() {
        ControlledState state = new ControlledState();
        state.forceMove(2, 0, 0);
        state.forceMove(4, 3, 2);
        state.forceMove(0, 0, 2);

        state.setNextPiece(1);
        AnalysisResult result = new FieldAnalyst().analyse(state, 9);
        assertThat(result.getAggregateHeight(), is(equalTo(17)));
    }

    @Test
    public void Analyst_calculates_holes_correctly() {
        ControlledState state = new ControlledState();
    }
}
