import java.util.Arrays;
import java.util.stream.IntStream;

public class StateAnalyser {
    private static final int COUNT_METRICS = 4;
    private static final int INDEX_METRIC_AGGREGATE_HEIGHT = 0;
    private static final int INDEX_METRIC_COMPLETE_LINES = 1;
    private static final int INDEX_METRIC_HOLES = 2;
    private static final int INDEX_METRIC_BUMPINESS = 3;

    /**
     * Fields
     */
    private final State _state;

    /**
     * Constructs a new field analyst
     */
    public StateAnalyser(State state) {
        this._state = state;
    }
    
    public AnalysisResult analyse(int move) {
        int[] moveData = this._state.legalMoves()[move];

        // Get the orientation and column of the move
        int orient = moveData[State.ORIENT];
        int column = moveData[State.SLOT];

        // Get the width of the piece
        int pieceWidth = State.getpWidth()[this._state.getNextPiece()][orient];
        
        // Find out the base
        int base = IntStream.range(0, pieceWidth)
                .map(index -> this._state.getTop()[column + index] -
                        State.getpBottom()[this._state.getNextPiece()][orient][index])
                .max().orElse(0);

        // Get new tops/heights
        final int[] newTop = new int[pieceWidth];
        IntStream.range(0, pieceWidth)
                .forEach(index -> {
                    newTop[index] = State.getpTop()[this._state.getNextPiece()][orient][index] +
                            base;
                });

        // Prepare metrics
        final int[] metrics = new int[COUNT_METRICS];

        // Find out number of holes of special columns
        metrics[INDEX_METRIC_HOLES] = IntStream.range(0, pieceWidth)
                .map(index -> base + State.getpBottom()[this._state.getNextPiece()][orient][index] -
                        this._state.getTop()[column + index])
                .sum();


        // Keep track of lowest column for checking of complete rows
        int lowestRow = State.ROWS - 1;

        // Keep track of previous column's height
        Integer previousColHeight = null;

        // Okay, now for all other columns, run normal aggregate
        for (int col = 0; col < State.COLS; col++) {
            final int currentCol = col;
            // AGGREGATE HEIGHT
            // We want to get the current column's height first
            int currentColHeight = (col < column || col >= column + pieceWidth) ?
                    this._state.getTop()[col] : newTop[col - column];
            // Update the lowest row along the way
            if (currentColHeight < lowestRow) {
                lowestRow = currentColHeight;
            }
            // Return losing move is height is more than what is allowed
            if (currentColHeight >= State.ROWS) {
                return AnalysisResult.losingMove(move);
            }

            // Then add on to the aggregate height
            metrics[INDEX_METRIC_AGGREGATE_HEIGHT] += currentColHeight;

            // HOLES
            // The holes of the special columns were already taken into account above
            // we only count the rest here
            metrics[INDEX_METRIC_HOLES] += IntStream.range(0, this._state.getTop()[currentCol])
                    .parallel()
                    .filter(row -> this._state.getField()[row][currentCol] == 0)
                    .count();

            // BUMPINESS
            // If the previous col height is null, then there isn't any value yet,
            // we just assign the current height to it and skip
            if (previousColHeight == null) {
                previousColHeight = currentColHeight;
                continue;
            }
            metrics[INDEX_METRIC_BUMPINESS] += Math.abs(currentColHeight - previousColHeight);
            previousColHeight = currentColHeight;
        }

        // Now, calculate the number of completed rows
        // If base row is below the lowest row, there is none, so we don't consider this case
        // We will try to measure the rows in which they are both affected by the placing, and
        // also higher than the lowest row
        for (int row = base; row < lowestRow; row++) {
            final int currentRow = row;
            // Filter out unaffected columns, if they contains an empty cell then we can skip
            // right away
            int emptyCells = (int) IntStream.range(0, State.COLS).parallel()
                    .filter(col -> col < column || col >= column + pieceWidth)
                    .filter(col -> this._state.getField()[currentRow][col] == 0)
                    .count();
            if (emptyCells > 0) {
                continue;
            }

            // Try to calculate if there is any hole here
            emptyCells = (int) IntStream.range(0, pieceWidth).parallel()
                    .map(index -> currentRow - base - State.getpBottom()[this._state.getNextPiece()][orient][index])
                    .filter(value -> value < 0)
                    .count();
            if (emptyCells > 0) {
                continue;
            }

            // No holes, increment count of filled cells
            metrics[INDEX_METRIC_COMPLETE_LINES]++;
        }

        return new AnalysisResult(move,
                metrics[INDEX_METRIC_AGGREGATE_HEIGHT],
                metrics[INDEX_METRIC_COMPLETE_LINES],
                metrics[INDEX_METRIC_HOLES],
                metrics[INDEX_METRIC_BUMPINESS]);
    }

}
