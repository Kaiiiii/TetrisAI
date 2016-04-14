import java.util.Random;
import java.util.function.Function;

/**
 * Created by maianhvu on 5/4/16.
 */
public class SimulatedAnnealing implements LearningMethod {

    private static final double HEURISTICS_MUTATION = (Heuristics.UPPER_BOUND - Heuristics.LOWER_BOUND) / 1000000;
    private static final int COUNT_BENCHMARKS = 2;
    private static final int CLOCK = 2000;

    public Heuristics learn() {
        Heuristics initial = Heuristics.randomHeuristics();
        System.out.println(initial);
        return simulatedAnnealing(initial, time -> CLOCK - time);
    }

    private Heuristics simulatedAnnealing(Heuristics initial, Function<Integer, Integer> schedule) {
        Heuristics current = initial;
        double previousValue = new Benchmarker(current).benchmark(COUNT_BENCHMARKS);

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
            double nextValue = new Benchmarker(next).benchmark(COUNT_BENCHMARKS);
            double desirability = nextValue - previousValue;

            if (desirability > 0.0) {
                System.out.printf("ASCEND\t[%s]->[%s]\tΔE = %.5f\n",
                        current, next, desirability);
                current = next;
                previousValue = nextValue;
            } else {
                double threshold = Math.pow(Math.E, desirability / temperature);
                double probability = randomizer.nextDouble();

                if (probability <= threshold) {
                    System.out.printf("DESCEND\t[%s]->[%s]\tΔE = %.5f\tP = %.4f\n",
                            current, next, desirability, threshold);
                    current = next;
                    previousValue = nextValue;
                }
                else {
                    System.out.printf("NO CHANGE\t[%s]\tΔE = %.5f\tP = %.4f\n",
                            current, desirability, threshold);
                }
            }
        }
    }

}
