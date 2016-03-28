package learner;

import logic.Analysis;
import logic.Game;
import logic.HeuristicsPlayer;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

/**
 * Created by maianhvu on 28/03/2016.
 */
public class State implements Comparable<State> {

    private static Random randomizer = new Random();

    private static final int BOUND_HEURISTICS_LOWER = -100;
    private static final int BOUND_HEURISTICS_UPPER = -1;

    private static final int COUNT_VALUES_HEURISTICS = 5;
    private static final int COUNT_TRIALS_BENCHMARK = 10;

    private int[] _heuristicsValues;
    private Integer _score;

    public State(int... heuristicsValues) {
        assert heuristicsValues != null;
        assert heuristicsValues.length == COUNT_VALUES_HEURISTICS;
        this._heuristicsValues = heuristicsValues;
    }

    public static State randomState() {
        return new State(IntStream.range(0, COUNT_VALUES_HEURISTICS)
                .map(index -> randomizer.nextInt(
                        BOUND_HEURISTICS_UPPER - BOUND_HEURISTICS_LOWER + 1
                ) + BOUND_HEURISTICS_LOWER)
                .toArray());
    }

    public int[] getValues() {
        return this._heuristicsValues;
    }

    public State getNextRandomState() {
        int randomIndex = randomizer.nextInt(COUNT_VALUES_HEURISTICS);
        int[] newValues = new int[COUNT_VALUES_HEURISTICS];
        System.arraycopy(this._heuristicsValues, 0, newValues, 0, COUNT_VALUES_HEURISTICS);

        int randomValue = newValues[randomIndex];

        if (randomValue <= BOUND_HEURISTICS_LOWER) {
            newValues[randomIndex] = BOUND_HEURISTICS_LOWER + 1;
        } else if (randomValue >= BOUND_HEURISTICS_UPPER) {
            newValues[randomIndex] = BOUND_HEURISTICS_UPPER - 1;
        } else {
            boolean toIncrease = randomizer.nextBoolean();
            newValues[randomIndex] += toIncrease ? 1 : -1;
        }

        return new State(newValues);
    }

    @Override
    public String toString() {
        return Arrays.toString(this.getValues());
    }

    public State getNextBestState() {
        List<State> nextState = new ArrayList<>();
        // Enumerate all states
        IntStream.range(0, COUNT_VALUES_HEURISTICS)
                .forEach(index -> {
                    if (this._heuristicsValues[index] <= BOUND_HEURISTICS_LOWER) {
                        int[] newValues = new int[COUNT_VALUES_HEURISTICS];
                        System.arraycopy(this._heuristicsValues, 0, newValues, 0, COUNT_VALUES_HEURISTICS);
                        newValues[index] = BOUND_HEURISTICS_LOWER + 1;
                        nextState.add(new State(newValues));
                    } else if (this._heuristicsValues[index] >= BOUND_HEURISTICS_UPPER) {
                        int[] newValues = new int[COUNT_VALUES_HEURISTICS];
                        System.arraycopy(this._heuristicsValues, 0, newValues, 0, COUNT_VALUES_HEURISTICS);
                        newValues[index] = BOUND_HEURISTICS_UPPER - 1;
                        nextState.add(new State(newValues));
                    } else {
                        int[] newValues1 = new int[COUNT_VALUES_HEURISTICS];
                        int[] newValues2 = new int[COUNT_VALUES_HEURISTICS];
                        System.arraycopy(this._heuristicsValues, 0, newValues1, 0, COUNT_VALUES_HEURISTICS);
                        System.arraycopy(this._heuristicsValues, 0, newValues2, 0, COUNT_VALUES_HEURISTICS);
                        newValues1[index] += 1;
                        newValues2[index] -= 1;
                        nextState.add(new State(newValues1));
                        nextState.add(new State(newValues2));
                    }
                });

        // Benchmark all of them
        final State[] stateToReturn = new State[] { null };

        nextState.parallelStream()
                .max(Comparator.comparing(state -> state.compareTo(this)))
                .ifPresent(state -> stateToReturn[0] = state);

        return stateToReturn[0];
    }

    @Override
    public int compareTo(State s) {
        List<Thread> computeThreads = new ArrayList<>();
        final int[] scores = new int[2];
        final CountDownLatch latch = new CountDownLatch(2);

        if (this._score == null) {
            computeThreads.add(new Thread(() -> {
                scores[0] = benchmark(constructHeuristicsPlayer(this));
                latch.countDown();
            }));
        } else {
            scores[0] = this._score;
            latch.countDown();
        }

        if (s._score == null) {
            computeThreads.add(new Thread(() -> {
                scores[1] = benchmark(constructHeuristicsPlayer(s));
                latch.countDown();
            }));
        } else {
            scores[1] = s._score;
            latch.countDown();
        }

        computeThreads.stream().forEach(Thread::start);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this._score = scores[0];
        s._score = scores[1];

        return scores[0] - scores[1];
    }

    private static HeuristicsPlayer constructHeuristicsPlayer(State state) {
        return new HeuristicsPlayer() {
            @Override
            protected double calculateHeuristics(Analysis analysis) {
                return state.getValues()[0] * analysis.getAggregateHeight() +
                        state.getValues()[1] * analysis.getRoughness() +
                        state.getValues()[2] * analysis.getHolesCount() +
                        state.getValues()[3] * analysis.getCellsCount() +
                        state.getValues()[4] * analysis.getHighestSlope();
            }
        };
    }

    private static int benchmark(final HeuristicsPlayer player) {
        final int[] scores = new int[COUNT_TRIALS_BENCHMARK];
        final CountDownLatch latch = new CountDownLatch(COUNT_TRIALS_BENCHMARK);

        // Spawn threads and benchmark
        for (int i = 0; i < COUNT_TRIALS_BENCHMARK; i++) {
            final int benchmarkIndex = i;
            Thread thread = new Thread(() -> {
                scores[benchmarkIndex] = getScore(player);
                latch.countDown();
            });
            thread.start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return (int) Math.round(Arrays.stream(scores).average().getAsDouble());
    }

    private static int getScore(HeuristicsPlayer player) {
        Game game = new Game();
        game.start();

        while (game.isOngoing()) {
            Game.Action action = player.getNextMove(game);

            if (action == null) {
                game.stop();
                break;
            }

            game.performAction(action);
        }

        return game.getCurrentScore();
    }
}


