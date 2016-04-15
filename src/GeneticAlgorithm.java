
import java.util.Random;

/*
 * Created by maianhvu on 28/03/2016.
 */

public class GeneticAlgorithm implements LearningMethod {

    private static final int POPULATION_SIZE = 10;
    private static final int HEURISTIC_COUNT = Heuristics.COUNT;
    private static final double LOWER = Heuristics.LOWER_BOUND;
    private static final double UPPER = Heuristics.UPPER_BOUND;
    private static Random randomizer = new Random();

    public int GENERATION_CUTOFF = 100;
    public double MUTATION_RATE = 0.1;

    public Heuristics learn(){
        return genetic();
    }

    private Heuristics genetic () {
        //Initialising a new population
        Heuristics[] population = new Heuristics[POPULATION_SIZE], nextPop = new Heuristics[POPULATION_SIZE];
        int totalScore = 0, prevScore = 0;
        double[] popScore = new double[POPULATION_SIZE];

        for (int i = 0; i < POPULATION_SIZE; i++) {
            population[i] = Heuristics.randomHeuristics();
        }

        Heuristics bestState = population[0];
        Heuristics[] parents = new Heuristics[2];
        double bestScore = new Benchmarker(population[0]).benchmark(1);

        for (int k = 0; k<GENERATION_CUTOFF; k++) {
            prevScore = totalScore;
            totalScore = 0;
            //Printing Population scores
            System.out.print("Population Scores: [");
            for (int i = 0; i < POPULATION_SIZE; i++) {
                popScore[i] = new Benchmarker(population[i]).benchmark(1);
                System.out.print(popScore[i]);
                if (popScore[i]>bestScore) {
                    //System.out.println(population[i].toString());
                    bestState = population[i];
                    bestScore = popScore[i];
                }
                totalScore += popScore[i];
                if (i == POPULATION_SIZE - 1) break;
                System.out.print(", ");
            }
            System.out.println("]");

            System.out.println("Average Population Score: " + totalScore/POPULATION_SIZE);

            //Printing best player
            System.out.print("Best Player: [" + bestState.toString());
            System.out.println("], Score: " + bestScore);

            double[] parentScore = new double[2];
            for (int j = 0; j<POPULATION_SIZE; j++) {
                parents[0] = population[0];
                parents[1] = population[1];

                parentScore[0] = popScore[0];
                parentScore[1] = popScore[1];
                for (int i = 2; i < POPULATION_SIZE; i++) {
                    if (parentScore[1]> parentScore[0])
                        swap(parents, parentScore);
                    if (randomizer.nextDouble() * popScore[i] > randomizer.nextDouble() * parentScore[0]) {
                        parents[0] = population[i];
                        parentScore[0] = popScore[i];
                    }
                    else if (randomizer.nextDouble() * popScore[i]> randomizer.nextDouble() * parentScore[1]) {
                        parents[1] = population[i];
                        parentScore[1] = popScore[i];
                    }
                }
                nextPop[j] = nextGeneration(parents, parentScore, prevScore, totalScore);
            }
            population = nextPop;
            System.out.println();
        }
        return bestState;
    }

    //Parents should be arranged in decreasing weight order.
    private Heuristics nextGeneration(Heuristics[] parents, double[] parentScore, int prev, int curr){
        double[] heuristic = new double[HEURISTIC_COUNT];

        for (int i = 0; i<HEURISTIC_COUNT; i++) {
            if (parentScore[0] * randomizer.nextDouble() >= parentScore[1] * randomizer.nextDouble()) {
                heuristic[i] = parents[0].getValues()[i];
            } else heuristic[i] = parents[1].getValues()[i];
            if (randomizer.nextDouble()*(prev/curr)>MUTATION_RATE) {
                heuristic[i] = heuristic[i] + randomizer.nextDouble() - randomizer.nextDouble();
            }
            if (heuristic[i] < LOWER) heuristic[i] = LOWER;
            else if (heuristic[i] > UPPER) heuristic[i] = UPPER;
        }
        return new Heuristics(heuristic);
    }

    private void swap(Heuristics[] s, double[] d){
        Heuristics tempH = s[0];
        s[0] = s[1];
        s[1] = tempH;

        double tempD = d[0];
        d[0] = d[1];
        d[1] = tempD;
    }
}
