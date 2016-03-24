package logic;

import entities.Debugger;
import entities.Tetromino;
import entities.TetrominoFactory;

import java.util.stream.IntStream;

/**
 * Created by maianhvu on 24/03/2016.
 */
public class FieldAnalyzer {

    /**
     * Properties
     */
    private int[][] _workingArea;

    /**
     * Singleton class
     */
    private static FieldAnalyzer instance = new FieldAnalyzer();

    private FieldAnalyzer()  {
        this._workingArea = null;
    }

    public static FieldAnalyzer getInstance() {
        return instance;
    }

    public Analysis analyze(Game game, Game.Action action) {
        this.copyFieldFrom(game);

        Tetromino model = new Tetromino(game.getNextPiece().getType(), action.getRotation());
        boolean[][] blockBitmap = TetrominoFactory.getInstance().getBitmap(model);

        // Find the row to place
        int row = -1;
        while (canPut(blockBitmap, row + 1, action.getPosition())) {
            row++;
        }

        // If cannot place at all, then return infinity
        if (row == -1) {
            return null;
        }

        // Lock in the piece
        this.lockIn(blockBitmap, row, action.getPosition());

        // Calculate analysis
        return new Analysis(this._workingArea);
    }

    /**
     *
     */
    private void copyFieldFrom(Game game) {
        int[][] field = game.getField().getState();

        // Prepare working area
        if (this._workingArea == null || this._workingArea.length != field.length ||
                this._workingArea[0].length != field.length) {
            this._workingArea = new int[game.getField().getHeight()][game.getField().getWidth()];
        }

        // Copy data over
        for (int row = 0; row < this._workingArea.length; row++) {
            System.arraycopy(field[row], 0, this._workingArea[row], 0, this._workingArea[0].length);
        }
    }

    private boolean canPut(boolean[][] block, int row, int col) {
        assert this._workingArea != null;
        assert block != null;

        // Invalid
        if (row < 0 || col < 0) return false;

        // Out of bounds
        if (row + block.length > this._workingArea.length ||
                col + block[0].length > this._workingArea[0].length) {
            return false;
        }

        // Run through bitmap footprint and check for available space
        for (int i = 0; i < block.length; i++) {
            for (int j = 0; j < block[0].length; j++) {
                if (!block[i][j]) continue;
                if (this._workingArea[row+i][col+j] != 0) return false;
            }
        }
        return true;
    }

    private void lockIn(boolean[][] block, int row, int col) {
        assert this._workingArea != null;
        assert block != null;

        for (int i = 0; i < block.length; i++) {
            for (int j = 0; j < block[0].length; j++) {
                if (!block[i][j]) continue;
                this._workingArea[row+i][col+j] = 1;
            }
        }
    }
}
