import java.util.ArrayList;
import java.util.Random;

/**
 * ParticleSwarmOptimizer explores the vector space for optimized heuristics coefficients.
 *
 * @author Huang Lie Jun
 *         Created by Larry on 12/4/16.
 */
public class ParticleSwarmOptimizer {

    // Sample flock size
    private int flockSize = 1000;

    // The best bird in town
    private Heuristics localBest = null;

    // All the birds
    private ArrayList<Heuristics> particles = null;

    // Random generator
    private Random randomNumberGenerator = null;

    // Number of rounds to play
    private static final int NUMBER_ROUNDS = 10;

    // Value Bounds for Aggregate Heights
    private static final double LOWER_BOUND_AGGREGATE_HEIGHT = 0.0;
    private static final double UPPER_BOUND_AGGREGATE_HEIGHT = 1.0;

    // Value Bounds for Complete Rows
    private static final double LOWER_BOUND_COMPLETE_ROWS = 0.0;
    private static final double UPPER_BOUND_COMPLETE_ROWS = 1.0;

    // Value Bounds for Holes Count
    private static final double LOWER_BOUND_HOLES = 0.0;
    private static final double UPPER_BOUND_HOLES = 1.0;

    // Value Bounds for Bumpiness
    private static final double LOWER_BOUND_BUMPINESS = 0.0;
    private static final double UPPER_BOUND_BUMPINESS = 1.0;

    // Value Bounds for Landing Height
    private static final double LOWER_BOUND_LANDING_HEIGHT = 0.0;
    private static final double UPPER_BOUND_LANDING_HEIGHT = 1.0;

    // Value Bounds for Row Transitions
    private static final double LOWER_BOUND_ROW_TRANSITION = 0.0;
    private static final double UPPER_BOUND_ROW_TRANSITION = 1.0;

    // Value Bounds for Column Transitions
    private static final double LOWER_BOUND_COLUMN_TRANSITION = 0.0;
    private static final double UPPER_BOUND_COLUMN_TRANSITION = 1.0;

    // Value Bounds for Well Sum
    private static final double LOWER_BOUND_WELL_SUM = 0.0;
    private static final double UPPER_BOUND_WELL_SUM = 1.0;


    /**
     * ParticleSwarmOptimizer will create a new random flock.
     */
    public ParticleSwarmOptimizer() {
        particles = new ArrayList<Heuristics>();
        randomNumberGenerator = new Random();
    }

    /**
     * ParticleSwarmOptimizer will create a new random flock.
     *
     * @param flockSize determines the sample size of the search.
     */
    public ParticleSwarmOptimizer(int flockSize) {
        this.flockSize = flockSize;
        particles = new ArrayList<Heuristics>();
        randomNumberGenerator = new Random();
    }


    /**
     * swarm searches the vector space for local best set of heuristics values.
     *
     * @return candidateHeuristics
     */
    public Heuristics swarm() {


        /* ***************** */
        /* Initializing part */
        /* ***************** */

        // Randomly initialize and disperse a fixed size of heuristics.
        for (int i = 0; i < flockSize; ++i) {

            // Randomly plot the particle in the vector space
            Heuristics particle = initializeParticle();

            // Add particle to collection
            particles.add(particle);
        }

        /* ***************** */
        /*  Searching part   */
        /* ***************** */


        // State the number of games to play
        int rounds = 0;

        // Flock and sample until desired state
        while (rounds < NUMBER_ROUNDS) {

            // Update all particles in flock
            for (Heuristics particle : particles) {

                // Start a game session
                Benchmarker benchmark = new Benchmarker(particle);

                // ... play game (higher, the better)
                double result = benchmark.benchmark(1);

                // Update result
                particle.updateResult(result);

                // Keep track of leader
                if (particle.betterThan(localBest)) {
                    localBest = particle;
                }

            }

            // All flock towards the best in JB some say Batam
            for (Heuristics particle : particles) {

                // Move and update velocity and position
                particle.flockTo(localBest);
            }

            rounds += 1;
        }

        return localBest;
    }

    /**
     * initializeParticle returns a new particle with random heuristics coefficients.
     *
     * @return randomParticle
     */
    private Heuristics initializeParticle() {

        double[] randomHeuristics = new double[Heuristics.COUNT_HEURISTICS];

        for (int i = 0; i < randomHeuristics.length; ++i) {
            randomHeuristics[i] = randomNumberGenerator.nextDouble();
        }

        Heuristics randomParticle = new Heuristics(randomHeuristics);

        return randomParticle;
    }
}