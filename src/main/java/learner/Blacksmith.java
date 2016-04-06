package learner;

import java.util.Random;
import java.util.function.Function;

/**
 * Created by maianhvu on 28/03/2016.
 */
public class Blacksmith {
    private static final int POPULATION_SIZE = 100;
    private static final int HEURISTIC_COUNT = 5;
    private static final int UPPER = -1;
    private static final int LOWER = -100;
    private static final int CLOCK = 1000;
    private static Random randomizer = new Random();

    public static void main(String[] args) {
        Blacksmith smith = new Blacksmith();
        State initialState = State.randomState();
        smith.geneticAlgorithm();
        //System.out.printf("Starting from random state %s.\n", initialState);
        //State finalState = smith.simulatedAnnealing(initialState, time -> CLOCK - time);
        //System.out.println(finalState);

        // TODO: Benchmark final state

    }

    private void geneticAlgorithm () {
        //Initialising a new population
        State[] population = new State[POPULATION_SIZE], nextPop = new State[POPULATION_SIZE];
        int maxScore = 0;

        for (int i = 0; i < POPULATION_SIZE; i++) {
            population[i] = State.randomState();
        }

        State[] parents = new State[2];
        while (true) {
            //Printing Population scores
            System.out.print("Population Scores: [");
            for (int i = 0; i < POPULATION_SIZE; i++) {
                System.out.print(population[i].getScore());
                if (population[i].getScore()>population[maxScore].getScore())
                    maxScore = i;
                if (i == POPULATION_SIZE - 1) break;
                System.out.print(", ");
            }
            System.out.println("]");

            //Printing best player
            System.out.print("Best Player: [");
            for (int i = 0; i<HEURISTIC_COUNT; i++){
                System.out.print(population[maxScore].getValues()[i]);
                if (i == HEURISTIC_COUNT-1) break;
                System.out.print(", ");
            }
            System.out.println("]");


            for (int j = 0; j<POPULATION_SIZE; j++) {
                parents[0] = population[0];
                parents[1] = population[1];

                for (int i = 2; i < POPULATION_SIZE; i++) {
                    if (parents[1].getScore() > parents[0].getScore())
                        swap(parents[0], parents[1]);
                    if (randomizer.nextDouble() * population[i].getScore() > randomizer.nextDouble() * parents[0].getScore())
                        parents[0] = population[i];
                    else if (randomizer.nextDouble() * population[i].getScore() > randomizer.nextDouble() * parents[1].getScore())
                        parents[1] = population[i];
                }
                nextPop[j] = nextGeneration(parents);
            }
            population = nextPop;
        }
    }

    private void swap(State s1, State s2){
        State temp = s1;
        s1 = s2;
        s2 = temp;
    }

    /*private double[] findWeights(State[] population){
        double[] weights = new double[population.length];
        double totalWeight = 0;

        for (int i = 0; i<population.length; i++){
            weights[i] = population[i].getScore();
            totalWeight += weights[i];
        }

        for (int i = 0; i<population.length; i++){
            weights[i]/=totalWeight;
        }

        return weights;
    }*/

    //Parents should be arranged in decreasing weight order.
    private State nextGeneration(State[] parents){
        int[] heuristic = new int[HEURISTIC_COUNT];

        for (int i = 0; i<HEURISTIC_COUNT; i++){
            if (parents[0].getScore()*randomizer.nextDouble() >= parents[1].getScore()*randomizer.nextDouble()) {
                heuristic[i] = parents[0].getValues()[i];
            }else heuristic[i] = parents[1].getValues()[i];
            if (randomizer.nextInt(101)<=1) heuristic[i] = heuristic[i] + randomizer.nextInt(100) - randomizer.nextInt(100);
            if (heuristic[i]<-100) heuristic[i] = -100;
            else if (heuristic[i]>-1) heuristic[i] = -1;

        }
        return new State(heuristic);
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
