package tetris.logic;

import java.util.ArrayList;

import tetris.ui.State;

/**
 * Created by maianhvu on 24/03/2016.
 */
public class FieldAnalyzer {

    /**
     * Fixed Properties
     */
    private int[][] previewField;
    private State currentState = null;
    private int type = -1;

    /**
     * Analysed Move Properties
     */
    private int[] currMove = null;
    private int currOrientation = -1;
    private int currSlot = -1;
    private int[] currBase = null;

    public FieldAnalyzer(State s) {
        this.previewField = new int[State.ROWS][State.COLS];
        currentState = s;
        type = currentState.getNextPiece();

    }

    public Analysis analyze(int[] move) {

        // Get information of current move
        setMoveDetails(move);

        // Find the row to place
        int baseRow = getBaseRow();

        // Lock in the piece
        lockInPiece(baseRow);

        // Calculate analysis
        return new Analysis(this.previewField);
    }

    private void lockInPiece(int baseRow) {
        int moveWidth = State.getpWidth()[type][currOrientation];
        int[] moveHeight = State.getpTop()[type][currOrientation];

        // Copy state of current field to preview field
        int[][] currentField = currentState.getField();

        for (int i = 0; i < State.ROWS; ++i) {
            System.arraycopy(currentField[i], 0, this.previewField[i], 0, this.previewField[0].length);
        }
        
        // Place piece onto preview field
        for (int i = 0; i < moveWidth; ++i) {
            int fromBottom = currBase[i] + 1;
            int toTop = moveHeight[i] - 1;
            for (int j = fromBottom; j < toTop; ++j) {
                previewField[j + baseRow][i + currSlot] = 1;
            }
        }

    }

    private void setMoveDetails(int[] move) {
        setCurrMove(move);
        currOrientation = move[State.ORIENT];
        currSlot = move[State.SLOT];
        currBase = State.getpBottom()[type][currOrientation];
    }

    private int getBaseRow() {

        ArrayList<Integer> relativeBase = new ArrayList<Integer>();

        // Remember column indices that have the lowest (base) blocks
        for (int i = 0; i < currBase.length; ++i) {
            if (currBase[i] == 0) {
                relativeBase.add(i + currSlot);
            }
        }

        int highestRow = 0;
        // Get the highest row among all existing blocks that comes in contact
        // with the base blocks of the current move
        for (int relativeBaseIndex : relativeBase) {
            if (currentState.getTop()[relativeBaseIndex] > highestRow) {
                highestRow = currentState.getTop()[relativeBaseIndex];
            }
        }

        return highestRow;
    }

    public int[] getCurrMove() {
        return currMove;
    }

    public void setCurrMove(int[] currMove) {
        this.currMove = currMove;
    }

}
