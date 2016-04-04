package tetris.learner;

/**
 * Created by maianhvu on 28/03/2016.
 */
public class LocalSearcher {
	public static void main(String[] args) {
		LearnerState currentState = LearnerState.randomState();
		while (true) {
			LearnerState nextState = new LearnerState (-17, -11, -82, -12, -16);
			if (nextState == null) break;
			int desirability = nextState.compareTo(currentState);
			if (desirability < 0) {
				break;
			}
			System.out.printf("ASCENT from %s to %s, D(E) = %d.\n",
					nextState, currentState, desirability);
			currentState = nextState;
		}
		System.out.println(currentState);
	}
}
