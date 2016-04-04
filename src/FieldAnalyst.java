import java.util.Arrays;
import java.util.stream.IntStream;

public class FieldAnalyst {
    private static final int COUNT_METRICS = 4;
    private static final int INDEX_METRIC_AGGREGATE_HEIGHT = 0;
    private static final int INDEX_METRIC_COMPLETE_LINES = 1;
    private static final int INDEX_METRIC_HOLES = 2;
    private static final int INDEX_METRIC_BUMPINESS = 3;
    
    /**
     * Constructs a new field analyst
     */
    public FieldAnalyst() {}
    
    public AnalysisResult analyse(State state, int move) {
        int[] moveData = state.legalMoves()[move];

        // Get the orientation and column of the move
        int orient = moveData[State.ORIENT];
        int column = moveData[State.SLOT];

        // Get the width of the piece
        int pieceWidth = State.getpWidth()[state.getNextPiece()][orient];
        
        // Find out the base
        int base = IntStream.range(0, pieceWidth)
                .map(index -> State.getpBottom()[state.getNextPiece()][orient][index] -
                        state.getTop()[column + index])
                .max().orElse(0);

        // Get new tops/heights
        final int[] newTop = new int[pieceWidth];
        IntStream.range(0, pieceWidth)
                .forEach(index -> {
                    newTop[index] = State.getpTop()[state.getNextPiece()][orient][index] +
                            base;
                });

        // Prepare metrics
        final int[] metrics = new int[COUNT_METRICS];

        // Find out number of holes of special columns
        IntStream.range(0, pieceWidth)
                .forEach(index -> {
                    metrics[INDEX_METRIC_HOLES] += base + State.getpBottom()[state.getNextPiece()][orient][index] -
                            state.getTop()[column + index];
                });

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
                    state.getTop()[col] : newTop[col - column];
            // Update the lowest row along the way
            if (currentColHeight < lowestRow) {
                lowestRow = currentColHeight;
            }
            // Then add on to the aggregate height
            metrics[INDEX_METRIC_AGGREGATE_HEIGHT] += currentColHeight;

            // HOLES
            // The holes of the special columns were already taken into account above
            // we only count the rest here
            metrics[INDEX_METRIC_HOLES] += IntStream.range(0, currentColHeight).parallel()
                    .filter(row -> state.getField()[row][currentCol] == 0)
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
        metrics[INDEX_METRIC_COMPLETE_LINES] = (int) IntStream.range(0, lowestRow + 1)
                .parallel()
                .filter(row -> {
                    long holes = Arrays.stream(state.getField()[row]).filter(value -> value == 0).count();
                    return holes == 0;
                })
                .count();

        return new AnalysisResult(move,
                metrics[INDEX_METRIC_AGGREGATE_HEIGHT],
                metrics[INDEX_METRIC_COMPLETE_LINES],
                metrics[INDEX_METRIC_HOLES],
                metrics[INDEX_METRIC_BUMPINESS]);
    }

}
