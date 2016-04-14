import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * PSO
 */
public class ParticleSwarmOptimisation implements LearningMethod {
    private static final int PARTICLE_BENCHMARKS = 1;
    private static final int PARTICLE_COUNT = 40;
    private static final int GENERATIONS = 1000;
    private static final double LEARNING_FACTOR_1 = 2;
    private static final double LEARNING_FACTOR_2 = LEARNING_FACTOR_1;

    private Random _randomizer;

    public ParticleSwarmOptimisation() {
        this._randomizer = new Random();
    }

    @Override
    public Heuristics learn() {
        // Prepare values
        Particle bestParticle = null;
        List<Particle> swarm = generateParticleSwarm();
        Scanner sc = new Scanner(System.in);

        for (int i = 0; i < GENERATIONS; i++) {
            final Particle localBestParticle = swarm.parallelStream()
                    .max(Particle::compareTo)
                    .orElse(null);
            if (localBestParticle == null) {
                break;
            }

            final double globalBest = localBestParticle.getPerformance();
            System.out.printf("%d\tFound gBest at [%s] with value %.0f\n",
                    i,
                    localBestParticle,
                    globalBest);

            swarm.parallelStream().forEach(particle -> {
                particle.updateVelocity(globalBest);
                System.out.printf("Particle [%s] tending towards [%s] with velocity %.4f\n",
                        particle,
                        localBestParticle,
                        particle.getVelocity());
                particle.updateValues(localBestParticle);
            });

            bestParticle = localBestParticle;
        }

        return new Heuristics(bestParticle.getValues());
    }

    public List<Particle> generateParticleSwarm() {
        return IntStream.range(0, PARTICLE_COUNT)
                .mapToObj(index -> Heuristics.randomHeuristics())
                .map(Heuristics::getValues)
                .map(Particle::new)
                .collect(Collectors.toList());
    }

    private class Particle extends Heuristics implements Comparable<Particle> {
        private Double _performance;
        private Double _bestPerformance;
        private Double _velocity;

        public Particle(double... heuristics) {
            super(heuristics);
            this._velocity = 0.0;
        }

        public Double getPerformance() {
            if (this._performance == null) {
                this._performance = new Benchmarker(this).benchmark(PARTICLE_BENCHMARKS);
            }
            if (this._bestPerformance == null || this._bestPerformance.compareTo(this._performance) < 0) {
                this._bestPerformance = this._performance;
            }
            return this._performance;
        }

        public Double getBestPerformance() {
            return this._bestPerformance;
        }

        public double getVelocity() {
            return this._velocity;
        }

        public void updateVelocity(double globalBest) {
            this._velocity = this.getVelocity() + LEARNING_FACTOR_1 * _randomizer.nextDouble() *
                    (this.getBestPerformance() - this.getPerformance()) + LEARNING_FACTOR_2 *
                    _randomizer.nextDouble() * (globalBest - this.getPerformance());
        }

        @Override
        public int compareTo(Particle o) {
            return this.getPerformance().compareTo(o.getPerformance());
        }

        public void updateValues(Particle o) {
            double[] distanceVector = IntStream.range(0, COUNT)
                    .mapToDouble(index -> o.getValues()[index] - this.getValues()[index])
                    .toArray();
            double vectorLength = Math.sqrt(Arrays.stream(distanceVector)
                    .map(value -> value * value) // Square
                    .sum());
            double[] unitVector = Arrays.stream(distanceVector)
                    .map(value -> value / vectorLength)
                    .toArray();
            double[] travel = Arrays.stream(unitVector)
                    .map(value -> value * this.getVelocity())
                    .toArray();

            double[] newValues = IntStream.range(0, COUNT)
                    .mapToDouble(index -> this.getValues()[index] + travel[index])
                    .toArray();
            this.setValues(newValues);
            // Prepare for new generation
            this._performance = null;
        }

    }
}
