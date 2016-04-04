
public class AnalysisResult implements Comparable<AnalysisResult> {

    /**
     * Constants
     */
    private static final double COEF_AGGREGATE_HEIGHT = -.510066;
    private static final double COEF_COMPLETED_LINES = .760666;
    private static final double COEF_HOLES_COUNT = -.35663;
    private static final double COEF_BUMPINESS = -.184483;

    /**
     * Properties
     */
    private final int _moveIndex;
    private final int _aggregateHeight;
    private final int _completeLines;
    private final int _holesCount;
    private final int _bumpiness;

    // Cached calculated value
    private Double _calculatedValue;

    /**
     * Constructs a new analysis result from the given calculated values
     * @param moveIndex
     * @param aggregateHeight
     * @param completedLines
     * @param holesCount
     * @param bumpiness
     */
    public AnalysisResult(int moveIndex, int aggregateHeight, int completedLines,
                          int holesCount, int bumpiness) {
        this._moveIndex = moveIndex;
        this._aggregateHeight = aggregateHeight;
        this._completeLines = completedLines;
        this._holesCount = holesCount;
        this._bumpiness = bumpiness;
    }

    public Double getValue() {
        if (this._calculatedValue == null) {
            this._calculatedValue = COEF_AGGREGATE_HEIGHT * this._aggregateHeight +
                    COEF_COMPLETED_LINES * this._completeLines +
                    COEF_HOLES_COUNT * this._holesCount +
                    COEF_BUMPINESS * this._bumpiness;
        }
        return this._calculatedValue;
    }

    public int getMoveIndex() {
        return this._moveIndex;
    }

    public int getAggregateHeight() {
        return this._aggregateHeight;
    }

    public int getCompleteLines() {
        return this._completeLines;
    }

    public int getHolesCount() {
        return this._holesCount;
    }

    public int getBumpiness() {
        return this._bumpiness;
    }

    public Double getCalculatedValue() {
        return this._calculatedValue;
    }

    @Override public int compareTo(AnalysisResult another) {
        return this.getValue().compareTo(another.getValue());
    }
}
