package learner;

import java.util.Random;
import java.util.function.Function;

/**
 * Created by maianhvu on 28/03/2016.
 */
public class Blacksmith {

    private static final int CLOCK = 1000;
    private static Random randomizer = new Random();

    public static void main(String[] args) {
        Blacksmith smith = new Blacksmith();
        State initialState = State.randomState();
        System.out.printf("Starting from random state %s.\n", initialState);
        State finalState = smith.simulatedAnnealing(initialState, time -> CLOCK - time);
        System.out.println(finalState);

        // TODO: Benchmark final state

    }

    private State simulatedAnnealing(State initialState, Function<Integer, Integer> schedule) {
        State currentState = initialState;
        int time = 0;

        while (true) {
            time++;
            int temperature = schedule.apply(time);
            if (temperature <= 0) return currentState;

            State nextState = currentState.getNextRandomState();
            int desirability = nextState.compareTo(currentState);
            System.out.printf("TEMP = %d\tD(E) = %d\n",
                    temperature, desirability);

            if (desirability > 0) {
                System.out.printf("ASCENT from %s to %s.\n", currentState, nextState);
                currentState = nextState;
            } else {
                double threshold = Math.pow(Math.E, desirability / (double) temperature);
                double probability = randomizer.nextDouble();

                if (probability <= threshold) {
                    System.out.printf("DESCENT from %s to %s with probability %.4f.\n",
                            currentState,
                            nextState,
                            threshold);
                    currentState = nextState;
                } else {
                    // No action
                    System.out.printf("NO CHANGE from %s.\n", currentState);
                }
            }
        }
    }

}
