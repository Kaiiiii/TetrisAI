import java.util.Arrays;
import java.util.Random;

/**
 * Created by maianhvu on 5/4/16.
 */
public class Heuristics {

    /**
     * Constants
     */

    public static final double LOWER_BOUND = 0.0;
    public static final double UPPER_BOUND = 10.0;

    public static final int COUNT = 6;
    public static final int ID_LANDING_HEIGHT     = 0;
    public static final int ID_ROWS_ELIMINATED    = 1;
    public static final int ID_ROW_TRANSITIONS    = 2;
    public static final int ID_COLUMN_TRANSITIONS = 3;
    public static final int ID_HOLES_COUNT        = 4;
    public static final int ID_WELL_SUMS          = 5;

    /**
     * Properties
     */
    private double[] _coefficients;

    public Heuristics(double... coefficients) {
        assert coefficients.length == COUNT;
        this._coefficients = coefficients;
    }

    public double[] getValues() {
        return this._coefficients;
    }

    public void setValues(double... coefficients) {
        this._coefficients = coefficients;
    }

    public Double calculate(AnalysisResult result) {
        return -this._coefficients[ID_LANDING_HEIGHT] * result.getLandingHeight() +
                this._coefficients[ID_ROWS_ELIMINATED] * result.getRowsEliminated() +
                -this._coefficients[ID_ROW_TRANSITIONS] * result.getRowTransitions() +
                -this._coefficients[ID_COLUMN_TRANSITIONS] * result.getColTransitions() +
                -this._coefficients[ID_HOLES_COUNT] * result.getHolesCount() +
                -this._coefficients[ID_WELL_SUMS] * result.getWellSums();
    }

    public static Heuristics randomHeuristics() {
        Random randomizer = new Random();
        double stretch = UPPER_BOUND - LOWER_BOUND;
        double[] coefficients = new double[COUNT];

        for (int i = 0; i < COUNT; i++) {
            coefficients[i] = randomizer.nextDouble() * stretch + LOWER_BOUND;
        }

        return new Heuristics(coefficients);
    }

    public Heuristics mutateHeuristics(double mutation) {
        Random randomizer = new Random();
        int valueToMutate = randomizer.nextInt(COUNT);
        double[] newHeuristics = new double[COUNT];
        System.arraycopy(this._coefficients, 0, newHeuristics, 0, COUNT);

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

    @Override public String toString() {
        return String.join(", ", (CharSequence[]) Arrays.stream(this._coefficients)
                .mapToObj(coef -> String.format("%.4f", coef))
                .toArray(String[]::new));
    }

    public String fullString() {
        return String.join(", ", (CharSequence[]) Arrays.stream(this._coefficients)
                .mapToObj(Double::toString)
                .toArray(String[]::new));
    }
}
