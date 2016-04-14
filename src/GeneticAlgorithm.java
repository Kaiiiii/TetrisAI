
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

    public Heuristics learn(){
        return genetic();
    }

    private Heuristics genetic () {
        //Initialising a new population
        Heuristics[] population = new Heuristics[POPULATION_SIZE], nextPop = new Heuristics[POPULATION_SIZE];
        int totalScore = 0, prevScore = 0;
        double currScore = 0, bestScore = 0;

        for (int i = 0; i < POPULATION_SIZE; i++) {
            population[i] = Heuristics.randomHeuristics();
        }

        Heuristics bestState = population[0];
        Heuristics[] parents = new Heuristics[2];
        bestScore = new Benchmarker(population[0]).benchmark(1);
        for (int k = 0; k<GENERATION_CUTOFF; k++) {
            prevScore = totalScore;
            totalScore = 0;
            //Printing Population scores
            System.out.print("Population Scores: [");
            for (int i = 0; i < POPULATION_SIZE; i++) {
                currScore = new Benchmarker(population[i]).benchmark(1);
                System.out.print(currScore);
                if (currScore>bestScore) {
                    bestState = population[i];
                    bestScore = currScore;
                }
                totalScore += currScore;
                if (i == POPULATION_SIZE - 1) break;
                System.out.print(", ");
            }
            System.out.println("]");

            System.out.println("Average Population Score: " + totalScore/POPULATION_SIZE);

            //Printing best player
            System.out.print("Best Player: [");
            for (int i = 0; i<HEURISTIC_COUNT; i++){
                System.out.print(bestState.getValues()[i]);
                if (i == HEURISTIC_COUNT-1) break;
                System.out.print(", ");
            }
            System.out.println("]");

            double[] parentScore = new double[2];
            for (int j = 0; j<POPULATION_SIZE; j++) {
                parents[0] = population[0];
                parents[1] = population[1];

                parentScore[0] = new Benchmarker(parents[0]).benchmark(1);
                parentScore[1] = new Benchmarker(parents[1]).benchmark(1);
                for (int i = 2; i < POPULATION_SIZE; i++) {
                    if (parentScore[1]> parentScore[0])
                        swap(parents, parentScore);
                    currScore = new Benchmarker(population[i]).benchmark(1);
                    if (randomizer.nextDouble() * currScore > randomizer.nextDouble() * parentScore[0]) {
                        parents[0] = population[i];
                        parentScore[0] = currScore;
                    }
                    else if (randomizer.nextDouble() * currScore > randomizer.nextDouble() * parentScore[1]) {
                        parents[1] = population[i];
                        parentScore[1] = currScore;
                    }
                }
                nextPop[j] = nextGeneration(parents, parentScore, prevScore, totalScore);
            }
            population = nextPop;
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
            if (randomizer.nextDouble()<(randomizer.nextDouble()*prev/curr)) {
                heuristic[i] = heuristic[i] + randomizer.nextInt(100) - randomizer.nextInt(100);
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
