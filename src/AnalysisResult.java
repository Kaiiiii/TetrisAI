
public class AnalysisResult {

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
}
