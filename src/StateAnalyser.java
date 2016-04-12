import java.util.stream.IntStream;

public class StateAnalyser {

    /**
     * Fields
     */
    private final State _state;
    private final int _piece;

    private final int _moveId;
    private final int _orient;
    private final int _slot;

    private final int _base;

    /**
     * Constructs a new field analyst
     */
    public StateAnalyser(State state, int moveId) {
        _state = state;
        _piece = state.getNextPiece();

        _moveId = moveId;
        _orient = State.legalMoves[_piece][moveId][State.ORIENT];
        _slot = State.legalMoves[_piece][moveId][State.SLOT];

        _base = this.getBase();
    }

    public AnalysisResult analyse() {
        // Prepare results
        final int[] heuristics = new int[Heuristics.COUNT];

        // Get height of new move
        int maxHeight = IntStream.range(0, getPieceWidth())
                .map(pieceCol -> pieceCol + _slot)
                .map(this::getVirtualHeight)
                .max().getAsInt();
        if (maxHeight >= State.ROWS) {
            return AnalysisResult.losingMove(_moveId);
        }

        // LANDING HEIGHT
        heuristics[Heuristics.ID_LANDING_HEIGHT] = this.calculateLandingHeight();

        // ROWS ELIMINATED
        heuristics[Heuristics.ID_ROWS_ELIMINATED] = this.calculateRowsEliminated();

        // ROW TRANSITIONS
        heuristics[Heuristics.ID_ROW_TRANSITIONS] = this.calculateRowTransitions();

        // COL TRANSITIONS
        heuristics[Heuristics.ID_COLUMN_TRANSITIONS] = this.calculateColTransitions();

        // NUMBER OF HOLES
        heuristics[Heuristics.ID_HOLES_COUNT] = this.calculateHolesCount();

        // WELL SUMS
        heuristics[Heuristics.ID_WELL_SUMS] = this.calculateWellSums();

        return new AnalysisResult(_moveId, heuristics);
    }

    private int getBase() {
        return IntStream.range(0, getPieceWidth())
                .map(index -> _state.getTop()[_slot + index] - getPieceBottom(index))
                .max().orElse(0);
    }

    private int calculateLandingHeight() {
        return _base + (this.getPieceHeight() - 1) / 2;
    }

    private int calculateRowsEliminated() {
        int lowestRow = IntStream.range(0, State.COLS)
                .map(this::getVirtualHeight)
                .min().getAsInt();
        return (int) IntStream.range(0, lowestRow)
                .filter(row -> IntStream.range(0, State.COLS)
                        .filter(col -> hasCellAt(row, col))
                        .count() == 0)
                .count();
    }

    private int calculateRowTransitions() {
        int highestRow = IntStream.range(0, State.COLS)
                .map(this::getVirtualHeight)
                .max().getAsInt();
        return IntStream.range(0, highestRow)
                .map(row -> {
                    int transitions = 0;
                    for (int col = 1; col < State.COLS; col++) {
                        if (hasCellAt(row, col) ^ hasCellAt(row, col-1)) {
                            transitions++;
                        }
                    }
                    return transitions;
                })
                .sum();
    }

    private int calculateColTransitions() {
        return IntStream.range(0, State.COLS)
                .map(col -> {
                    int transitions = 0;
                    int rowHeight = getVirtualHeight(col);
                    for (int row = 0; row <= rowHeight; row++) {
                        if (hasCellAt(row, col) ^ hasCellAt(row + 1, col)) {
                            transitions++;
                        }
                    }
                    return transitions;
                })
                .sum();
    }

    private int calculateHolesCount() {
        return IntStream.range(0, State.COLS)
                .map(col -> {
                    int holes = 0;
                    int rowHeight = getVirtualHeight(col);
                    for (int row = 0; row < rowHeight - 1; row++) {
                        if (!hasCellAt(row, col)) {
                            holes++;
                        }
                    }
                    return holes;
                })
                .sum();
    }

    private int calculateWellSums() {
        int innerWellSums = IntStream.range(1, State.COLS - 1)
                .map(col -> {
                    int wellSize = 0;
                    int rowHeight = getVirtualHeight(col);
                    for (int row = rowHeight; row < State.ROWS; row++) {
                        if (!hasCellAt(row, col) && hasCellAt(row, col - 1) && hasCellAt(row, col + 1)) {
                            wellSize++;

                            // Keep counting from this cell downwards
                            for (int i = row - 1; i >= 0; i--) {
                                if (!hasCellAt(i, col)) {
                                    wellSize++;
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                    return wellSize;
                }).sum();

        int leftWellSums = 0;
        int firstColHeight = getVirtualHeight(0);
        for (int row = firstColHeight; row < State.ROWS; row++) {
            if (!hasCellAt(row, 0) && hasCellAt(row, 1)) {
                leftWellSums++;

                // Keep counting from this cell downwards
                for (int i = row - 1; i >= 0; i--) {
                    if (!hasCellAt(i, 0)) {
                        leftWellSums++;
                    } else {
                        break;
                    }
                }
            }
        }

        int rightWellSums = 0;
        int lastCol = State.COLS - 1;
        int lastColHeight = getVirtualHeight(lastCol);
        for (int row = lastColHeight; row < State.ROWS; row++) {
            if (!hasCellAt(row, lastCol) && hasCellAt(row, lastCol - 1)) {
                rightWellSums++;

                // Keep counting from this cell downwards
                for (int i = row - 1; i >= 0; i--) {
                    if (!hasCellAt(i, lastCol)) {
                        rightWellSums++;
                    } else {
                        break;
                    }
                }
            }
        }

        return innerWellSums + leftWellSums + rightWellSums;
    }

    private int getPieceTop(int pieceCol) {
        return State.getpTop()[_piece][_orient][pieceCol];
    }

    private int getPieceBottom(int pieceCol) {
        return State.getpBottom()[_piece][_orient][pieceCol];
    }

    private int getPieceWidth() {
        return State.getpWidth()[_piece][_orient];
    }

    private int getPieceHeight() {
        return State.getpHeight()[_piece][_orient];
    }

    private int getVirtualHeight(int column) {
        if (!isAffectedColumn(column)) {
            return _state.getTop()[column];
        }
        return _base + getPieceTop(column - _slot);
    }

    private boolean hasCellAt(int row, int col) {
        // More than height, nothing is here
        if (row >= getVirtualHeight(col)) {
            return false;
        }
        // If the old field has it there, it will still be there
        // in the context of this function
        if (_state.getField()[row][col] > 0) {
            return true;
        }
        // If outside of the affected area, and still it has nothing
        // here, then it will not have anything there anyway
        if (!isAffectedColumn(col) || row < _base) {
            return false;
        }

        // Now we can besure that it's within affected area
        col -= _slot;
        row -= _base;
        return row >= getPieceBottom(col);
    }

    private boolean isAffectedColumn(int column) {
        return column >= _slot && column < _slot + getPieceWidth();
    }
}
