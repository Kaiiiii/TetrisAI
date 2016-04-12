import java.util.Arrays;
import java.util.Random;

/**
 * Created by maianhvu on 5/4/16.
 */
public class Heuristics {

    // Bounds
    public static final int COUNT_HEURISTICS = 5;
    public static final double LOWER_BOUND = 0.0;
    public static final double UPPER_BOUND = 1.0;

    // Result of last played game
    private double result = 0;

    private double[] _coefficients;

    public Heuristics(double... coefficients) {
        assert coefficients.length == COUNT_HEURISTICS;
        this._coefficients = coefficients;
    }

    public Double calculate(AnalysisResult result) {
        return this._coefficients[0] * result.getAggregateHeight() +
                this._coefficients[1] * result.getCompleteRows() +
                this._coefficients[2] * result.getHolesCount() +
                this._coefficients[3] * result.getBumpiness() +
                this._coefficients[4] * result.getLandingHeight();
    }

    public static Heuristics randomHeuristics() {
        Random randomizer = new Random();
        double stretch = UPPER_BOUND - LOWER_BOUND;
        double[] coefficients = new double[COUNT_HEURISTICS];

        for (int i = 0; i < COUNT_HEURISTICS; i++) {
            coefficients[i] = randomizer.nextDouble() * stretch + LOWER_BOUND;
        }

        return new Heuristics(coefficients);
    }

    public Heuristics mutateHeuristics(double mutation) {
        Random randomizer = new Random();
        int valueToMutate = randomizer.nextInt(COUNT_HEURISTICS);
        double[] newHeuristics = new double[COUNT_HEURISTICS];
        System.arraycopy(this._coefficients, 0, newHeuristics, 0, COUNT_HEURISTICS);

        boolean toIncrease = randomizer.nextBoolean();
        newHeuristics[valueToMutate] += mutation * (toIncrease ? 1 : -1);
        if (newHeuristics[valueToMutate] < LOWER_BOUND) {
            newHeuristics[valueToMutate] = LOWER_BOUND;
        }
        if (newHeuristics[valueToMutate] > UPPER_BOUND) {
            newHeuristics[valueToMutate] = UPPER_BOUND;
        }
        return new Heuristics(newHeuristics);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(this._coefficients).mapToObj(value -> String.format("%.3f", value))
                .forEachOrdered(valueString -> {
                    if (sb.length() != 0) {
                        sb.append(", ");
                    }
                    sb.append(valueString);
                });
        return sb.toString();
    }

    public String fullString() {
        return String.join(", ", (CharSequence[]) Arrays.stream(this._coefficients)
                .mapToObj(Double::toString)
                .toArray(String[]::new));
    }

    /**
     * betterThan compares the last game results between 2 heuristics and return the comparison.
     *
     * @param localBest
     * @return
     */
    public boolean betterThan(Heuristics localBest) {
        return this.result > localBest.result;
    }

    /**
     * setVelocity sets the direction and scalar change, given another better particle
     *
     * @param betterParticle
     */
    public void setVelocity(Heuristics betterParticle) {
        //TODO: Implement velocity equations with some randomness
    }

    /**
     * setPosition edits the heuristics coefficients based on the velocity or vector suggested.
     */
    public void setPosition() {
        //TODO: Implement change in position w.r.t velocity vectors

    }

    /**
     * flockTo directs this particle towards the better particle, with some randomness in the amount moved.
     *
     * @param localBest
     */
    public void flockTo(Heuristics localBest) {

        // ... set Velocity
        setVelocity(localBest);

        // ... set Position
        setPosition();
    }

    /**
     * updateResult updates the result field with the result of the last game played using this heuristics
     *
     * @param result
     */
    public void updateResult(double result) {
        this.result = result;
    }
}
