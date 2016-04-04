/**
 * Created by maianhvu on 5/4/16.
 */
public class Heuristics {

    private static final int COUNT_HEURISTICS = 4;

    private double[] _coefficients;

    public Heuristics(double... coefficients) {
        assert coefficients.length == COUNT_HEURISTICS;
        this._coefficients = coefficients;
    }

    public Double calculate(AnalysisResult result) {
        return this._coefficients[0] * result.getAggregateHeight() +
                this._coefficients[1] * result.getCompleteLines() +
                this._coefficients[2] * result.getHolesCount() +
                this._coefficients[3] * result.getBumpiness();
    }
}
