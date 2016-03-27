package learner;

import logic.Analysis;
import logic.Game;
import logic.HeuristicsPlayer;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Created by maianhvu on 26/03/2016.
 */
public class HeuristicsSearcher {

    private static final int COUNT_BENCHMARK_TESTS = 100;
    private static final int CLOCK = 10000;
    private static final int COUNT_TESTER_TESTS = 1000;
    /**
     * Singleton
     */
    private static HeuristicsSearcher instance = new HeuristicsSearcher();
    private HeuristicsSearcher() {}
    public static HeuristicsSearcher getInstance() { return instance; }
    private static Random randomizer = new Random();

    /**
     * Constants
     */
    private static final int BOUND_LOWER_HEURISTICS = -100;
    private static final int BOUND_UPPER_HEURISTICS = -1;
    private static final int COUNT_HEURISTICS = 5;

    public static void main(String[] args) {
        HeuristicsSearcher searcher = HeuristicsSearcher.getInstance();
        searcher.search();
    }

    public void search() {
        State state = State.random();
        State finalState = simulatedAnnealing(state, time -> CLOCK - time);

        final HeuristicsPlayer tester = constructHeuristicsPlayer(finalState);
        final int[] results = new int[COUNT_TESTER_TESTS];
        final CountDownLatch latch = new CountDownLatch(COUNT_TESTER_TESTS);

        for (int i = 0; i < COUNT_TESTER_TESTS; i++) {
            final int testIndex = i;

            Thread thread = new Thread(() -> {
                Game game = playGame(tester);
                results[testIndex] = game.getCurrentScore();
                latch.countDown();
            });
            thread.start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        double averageScore = Arrays.stream(results).average().getAsDouble();
        System.out.printf("\n%s\nAverage score: %.1f.\n", finalState, averageScore);
    }

    private State simulatedAnnealing(State initialState, Function<Integer, Integer> schedule) {
        State currentState = initialState;
        int time = 0;

        while (true) {
            time++;
            int temperature = schedule.apply(time);
            if (temperature <= 0) {
                return currentState;
            }

            State nextState = currentState.next();
            double desirability = compare(nextState, currentState);

            System.out.printf("TEMP = %d\tΔE = %.1f\n", temperature, desirability);

            if (desirability > 0) {
                System.out.printf("ASCENT from %s to %s.\n", currentState, nextState);
                currentState = nextState;
            } else {
                double threshold = Math.pow(Math.E, desirability / temperature);
                double randomProb = randomizer.nextDouble();

                // Pass threshold!
                if (randomProb <= threshold) {
                    System.out.printf("DESCENT from %s to %s with probability %.4f.\n",
                            currentState,
                            nextState,
                            threshold);
                    currentState = nextState;
                } else {
                    System.out.printf("NO DESCENT from %s with probability %.3f.\n",
                            currentState,
                            threshold);
                }
            }
        }
    }

    private double compare(final State s1, final State s2) {
        // Benchmark the two players in a separate thread
        final double[] benchmarks = new double[2];
        final CountDownLatch latch = new CountDownLatch(2);

        Thread player1Benchmarker = new Thread(() -> {
            benchmarks[0] = benchmark(constructHeuristicsPlayer(s1));
            latch.countDown();
        });
        Thread player2Benchmarker = new Thread(() -> {
            benchmarks[1] = benchmark(constructHeuristicsPlayer(s2));
            latch.countDown();
        });
        player1Benchmarker.start();
        player2Benchmarker.start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return benchmarks[0] - benchmarks[1];
    }

    private static double benchmark(HeuristicsPlayer player) {
        final int[] turns = new int[COUNT_BENCHMARK_TESTS];
        final int[] scores = new int[COUNT_BENCHMARK_TESTS];
        final CountDownLatch latch = new CountDownLatch(COUNT_BENCHMARK_TESTS);


        for (int i = 0; i < COUNT_BENCHMARK_TESTS; i++) {
            final int benchmarkerIndex = i;

            Thread benchmarker = new Thread(() -> {
                Game game = playGame(player);

                turns[benchmarkerIndex] = game.getCurrentTurn();
                scores[benchmarkerIndex] = game.getCurrentScore();

                latch.countDown();
            });

            benchmarker.start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final double[] result = new double[] { 0.0 };

        IntStream.range(0, COUNT_BENCHMARK_TESTS)
                .map(index -> turns[index] * scores[index])
                .average().ifPresent(value -> result[0] = value * 100);
        return result[0];
    }

    private static Game playGame(HeuristicsPlayer player) {
        Game game = new Game();
        game.start();

        while (game.isOngoing()) {
            Game.Action action = player.getNextMove(game);

            // No available action, game over
            if (action == null) {
                game.stop();
                break;
            }

            game.performAction(action);
        }
        return game;
    }

    private static HeuristicsPlayer constructHeuristicsPlayer(State state) {
        return new HeuristicsPlayer() {
            @Override
            protected int calculateHeuristics(Analysis analysis) {
                return state.getValue()[0] * analysis.getHeight() +
                        state.getValue()[1] * analysis.getRoughness() +
                        state.getValue()[2] * analysis.getHolesCount() +
                        state.getValue()[3] * analysis.getCellsCount() +
                        state.getValue()[4] * analysis.getHighestSlope();
            }
        };
    }

    private static class State {
        private int[] _heuristicsValues;

        private State(int... values) {
            assert values.length == COUNT_HEURISTICS;
            this._heuristicsValues = values;
        }

        public static State random() {
            int[] values = IntStream.range(0, COUNT_HEURISTICS)
                    .map(index -> randomizer.nextInt(
                            (BOUND_UPPER_HEURISTICS - BOUND_LOWER_HEURISTICS)
                    ) + BOUND_LOWER_HEURISTICS + 1)
                    .toArray();
            return new State(values);
        }

        public int[] getValue() {
            return this._heuristicsValues;
        }

        public State next() {
            // Prepare new heuristics
            int[] newHeuristics = new int[COUNT_HEURISTICS];
            System.arraycopy(this._heuristicsValues, 0, newHeuristics, 0, COUNT_HEURISTICS);

            // Find a heuristics value to modify
            int valueIndex = randomizer.nextInt(COUNT_HEURISTICS);

            // Modify value
            if (this._heuristicsValues[valueIndex] <= BOUND_LOWER_HEURISTICS) {
                newHeuristics[valueIndex] = BOUND_LOWER_HEURISTICS + 1;
            } else if (this._heuristicsValues[valueIndex] >= BOUND_UPPER_HEURISTICS) {
                newHeuristics[valueIndex] = BOUND_UPPER_HEURISTICS - 1;
            } else {
                boolean toIncrease = randomizer.nextBoolean();
                newHeuristics[valueIndex] += toIncrease ? 1 : -1;
            }

            // Create new state
            return new State(newHeuristics);
        }

        @Override public String toString() {
            return Arrays.toString(this._heuristicsValues);
        }
    }
}
