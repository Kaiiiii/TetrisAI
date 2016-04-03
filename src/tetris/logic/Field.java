package tetris.logic;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Created by maianhvu on 24/03/2016.
 */
public class Field {

    public static final int CODE_LOSING_MOVE = -1;
    public static final int CODE_INVALID_MOVE = -2;
    /**
     * Constants
     */
    private static final int WIDTH_FIELD = 10;
    private static final int HEIGHT_FIELD = 21;
    private static final int[] ROW_EMPTY = IntStream.range(0, WIDTH_FIELD)
            .map(index -> 0).toArray();

    private final int[][] _field;

    public Field() {
        this._field = new int[HEIGHT_FIELD][WIDTH_FIELD];
    }

    public int put(int position, boolean[][] pieceBitmap, int id) {
        assert pieceBitmap != null;
        assert id > 0; // Does not allow empty placement

        if (position < 0 || position + pieceBitmap[0].length > this.getWidth()) {
            return CODE_INVALID_MOVE;
        }

//        int pieceHeight = pieceBitmap.length;
//        int pieceWidth = pieceBitmap[0].length;

        // Linear search for an empty row
        int rowToPut = 0;
        while (canFit(rowToPut+1, position, pieceBitmap)) {
            rowToPut++;
        }

        // After searching for the row to put already, but the move is still
        // invalid, we conclude that this is a losing move
        if (!canFit(rowToPut, position, pieceBitmap)) {
            return CODE_LOSING_MOVE;
        }

        // Register the piece there
        this.lockIn(rowToPut, position, pieceBitmap, id);

        // Piece fits, try to empty any full rows
        // and return the number of rows cleared
        return this.checkAndClear();
    }

    public int[][] getState() {
        return this._field;
    }

    public int getHeight() {
        return this._field.length;
    }

    public int getWidth() {
        return this._field[0].length;
    }

    private boolean canFit(int row, int col, boolean[][] pieceBitmap) {
        assert pieceBitmap != null;

        // Invalid
        if (row < 0 || col < 0) return false;

        // Out of field
        if (row + pieceBitmap.length > this.getHeight()) return false;
        if (col + pieceBitmap[0].length > this.getWidth()) return false;

        // Try to fit the piece
        for (int rowOff = 0; rowOff < pieceBitmap.length; rowOff++) {
            for (int colOff = 0; colOff < pieceBitmap[0].length; colOff++) {
                // Skip empty bits in the bitmap, there is nothing to be checked here
                if (!pieceBitmap[rowOff][colOff]) continue;
                if (this._field[row + rowOff][col + colOff] != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    private void lockIn(int row, int col, boolean[][] pieceBitmap, int value) {
        for (int i = 0; i < pieceBitmap.length; i++) {
            for (int j = 0; j < pieceBitmap[0].length; j++) {
                if (!pieceBitmap[i][j]) continue;
                this._field[row+i][col+j] = value;
            }
        }
    }

    public boolean hasPieceAt(int row, int col, boolean[][] pieceBitmap, int value) {
        if (row < 0 || col < 0) return false; // Invalid locations

        // Out of bounds
        if (row + pieceBitmap.length > this.getHeight()) return false;
        if (col + pieceBitmap[0].length > this.getWidth()) return false;

        for (int i = 0; i < pieceBitmap.length; i++) {
            for (int j = 0; j < pieceBitmap[0].length; j++) {
                if (!pieceBitmap[i][j]) continue; // Skip empty bits
                if (this._field[row+i][col+j] != value) return false;
            }
        }

        return true;
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                sb.append(this._field[i][j]);
            }
            if (i == this.getHeight() - 1) continue; // No break
            sb.append("\n");
        }
        return sb.toString();
    }

    private int checkAndClear() {
        Set<Integer> rowsToClear = new HashSet<>();
        // Scan rows
        IntStream.range(0, this.getHeight())
                .filter(this::isRowFull)
                .forEach(row -> rowsToClear.add(row));

        if (rowsToClear.isEmpty()) {
            return 0; // Nothing to be cleared
        }

        int score = rowsToClear.size(); // Save the number of initially cleared rows
        int top = 0; // Make top dynamic

        int row = this.getHeight() - 1;
        int copyFromRow = row;

        // Scan from the bottom and replace the cleared rows with another row on top
        for (row = this.getHeight() - 1; row >= top && !isRowEmpty(row); row--) {

            // If row is not to be clear and that the row corresponds to the old row,
            // we leave the row intact
            if (!rowsToClear.contains(row) && row == copyFromRow) {
                copyFromRow--;
                continue;
            }
            // If the row doesn't correspond, we select a row from above that
            // is not one of those rows to be cleared and set it to replace
            while (rowsToClear.contains(copyFromRow) && copyFromRow >= 0) {
                copyFromRow--;
            }

            top++;
            int[] rowToCopyFrom;

            // If the row to copy from is existing, we use it to be the
            // row to be copied
            if (copyFromRow >= 0) {
                rowToCopyFrom = this._field[copyFromRow];
                rowsToClear.add(copyFromRow);
            }
            // If not we use an empty row
            else {
                rowToCopyFrom = ROW_EMPTY;
            }

            System.arraycopy(rowToCopyFrom, 0, this._field[row], 0, this._field[row].length);
        }

        // Make sure all the rows above are clear
        for (int i = 0; i <= row; i++) {
            System.arraycopy(ROW_EMPTY, 0, this._field[i], 0, ROW_EMPTY.length);
        }

        return score;
    }

    private boolean isRowEmpty(int row) {
        for (int col = 0; col < this.getWidth(); col++) {
            if (this._field[row][col] != 0) return false;
        }
        return true;
    }

    private boolean isRowFull(int row) {
        for (int col = 0; col < this.getWidth(); col++) {
            if (this._field[row][col] == 0) return false;
        }
        return true;

    }
}
