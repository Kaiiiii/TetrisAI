import java.util.stream.IntStream;

public class AnalysisResult {

    /**
     * Properties
     */
    private final int _moveIndex;
    private final int[] _heuristicValues;
    private final boolean _isLosingMove;

    /**
     * Constructs a new analysis result from the given calculated values
     * @param moveIndex the index of the move made
     * @param heuristicValues
     */
    public AnalysisResult(int moveIndex, int... heuristicValues) {
        assert heuristicValues.length == Heuristics.COUNT;
        this._moveIndex = moveIndex;
        this._heuristicValues = heuristicValues;
        this._isLosingMove = false;
    }

    /**
     * Getters
     */
    public int getMoveIndex() {
        return this._moveIndex;
    }

    public int getLandingHeight() {
        assert this._heuristicValues != null;
        return this._heuristicValues[Heuristics.ID_LANDING_HEIGHT];
    }

    public int getRowsEliminated() {
        assert this._heuristicValues != null;
        return this._heuristicValues[Heuristics.ID_ROWS_ELIMINATED];
    }

    public int getRowTransitions() {
        assert this._heuristicValues != null;
        return this._heuristicValues[Heuristics.ID_ROW_TRANSITIONS];
    }

    public int getColTransitions() {
        assert this._heuristicValues != null;
        return this._heuristicValues[Heuristics.ID_COLUMN_TRANSITIONS];
    }

    public int getHolesCount() {
        assert this._heuristicValues != null;
        return this._heuristicValues[Heuristics.ID_HOLES_COUNT];
    }

    public int getWellSums() {
        assert this._heuristicValues != null;
        return this._heuristicValues[Heuristics.ID_WELL_SUMS];
    }

    // Losing move constructor
    private AnalysisResult(int moveId) {
        this._moveIndex = moveId;
        this._isLosingMove = true;
        this._heuristicValues = null;
    }

    public static AnalysisResult losingMove(int moveId) {
        return new AnalysisResult(moveId);
    }

    public boolean isLosingMove() {
        return this._isLosingMove;
    }
}
