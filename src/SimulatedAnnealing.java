import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

/**
 * Created by maianhvu on 5/4/16.
 */
public class SimulatedAnnealing implements LearningMethod {

    private static final double HEURISTICS_MUTATION = (Heuristics.UPPER_BOUND - Heuristics.LOWER_BOUND) / 100;
    private static final int COUNT_BENCHMARKS = 100;
    private static final int CLOCK = 1000;

    public Heuristics learn() {
        Heuristics initial = Heuristics.randomHeuristics();
        System.out.println(initial);
        return simulatedAnnealing(initial, time -> CLOCK - time);
    }

    private Heuristics simulatedAnnealing(Heuristics initial, Function<Integer, Integer> schedule) {
        Heuristics current = initial;
        int time = 1;
        Random randomizer = new Random();

        while (true) {
            // Update temperature and check
            time++;
            int temperature = schedule.apply(time);
            if (temperature < 0) {
                return current;
            }
            System.out.printf("#%d (%d)\t", time, temperature);

            Heuristics next = current.mutateHeuristics(HEURISTICS_MUTATION);
            double desirability = Arrays.asList(current, next).parallelStream()
                    .map(Benchmarker::new)
                    .mapToDouble(bmarker -> bmarker.benchmark(COUNT_BENCHMARKS))
                    .reduce((currentValue, nextValue) -> currentValue - nextValue)
                    .getAsDouble();
            if (desirability > 0.0) {
                System.out.printf("ASCEND\t[%s]->[%s]\tΔE = %.5f\n",
                        current, next, desirability);
                current = next;
            } else {
                double threshold = Math.pow(Math.E, desirability * CLOCK / temperature);
                double probability = randomizer.nextDouble();

                if (probability <= threshold) {
                    System.out.printf("DESCEND\t[%s]->[%s]\tΔE = %.5f\tP = %.4f\n",
                            current, next, desirability, threshold);
                    current = next;
                }
                else {
                    System.out.printf("NO CHANGE\t[%s]\tΔE = %.5f\tP = %.4f\n",
                            current, desirability, threshold);
                }
            }
        }
    }

}
