package logic;

import entities.Tetromino;
import entities.TetrominoFactory;

import java.util.stream.IntStream;

/**
 * Created by maianhvu on 16/03/2016.
 */
public class Game {

    private Field _field;
    private Tetromino _nextPiece;
    private boolean _isOngoing;
    private int _currentTurn;
    private int _currentScore;

    public Game() {
        this._field = new Field();
        this._isOngoing = false;
        this._currentTurn = 0;
        this._currentScore = 0;
    }

    /**
     * Starts the game
     */
    public void start() {
        this._isOngoing = true;
        this._nextPiece = generateNextPiece();
        this._currentTurn = 1;
    }

    public Field getField() {
        return this._field;
    }

    public boolean performAction(Action action) {
        assert this._isOngoing;
        assert action.getPosition() >= 0;

        Tetromino piece = this.getNextPiece();
        assert piece != null;
        piece.setRotation(action.getRotation());

        boolean[][] bitmap = TetrominoFactory.getInstance().getBitmap(piece);
        assert action.getPosition() <= this._field.getWidth() - bitmap[0].length;

        int result = this._field.put(action.getPosition(), bitmap, piece.getBlockId());

        if (result == Field.CODE_LOSING_MOVE) {
            // Game over
            this.stop();
        } else if (result == Field.CODE_INVALID_MOVE) {
            // Don't do anything, go to next move
            return true;
        } else {
            this._currentScore += result;

            // Generate next piece if game is ongoing
            this._nextPiece = generateNextPiece();
            this._currentTurn++;
        }

        return this._isOngoing;
    }

    public boolean putPiece(int position) {
        return this.performAction(new Action(this.getNextPiece().getRotation(), position));
    }

    public boolean isOngoing() {
        return this._isOngoing;
    }

    public Tetromino getNextPiece() {
        if (!this._isOngoing) {
            return null;
        }

        return this._nextPiece;
    }

    public int getNextPieceIdentifier() {
        assert this.getNextPiece() != null;
        return this.getNextPiece().getBlockId();
    }

    public int getCurrentTurn() {
        return this._currentTurn;
    }

    public int getCurrentScore() {
        return this._currentScore;
    }

    private static Tetromino generateNextPiece() {
        return Tetromino.randomPiece();
    }

    public int[] getAvailablePositionsFor(Tetromino.Rotation rotation) {
        Tetromino model = new Tetromino(this.getNextPiece().getType(), rotation);
        int width = TetrominoFactory.getInstance().getBitmap(model)[0].length;
        return IntStream.range(0, this.getField().getWidth() - width + 1).toArray();
    }

    public void rotateLeft() {
        assert this.isOngoing();
        assert this.getNextPiece() != null;

        int newRotationId = this.getNextPiece().getRotation().ordinal() - 1;
        if (newRotationId < 0) newRotationId = Tetromino.Rotation.values().length - 1;
        Tetromino.Rotation newRotation = Tetromino.Rotation.values()[newRotationId];

        this.getNextPiece().setRotation(newRotation);
    }

    public void rotateRight() {
        assert this.isOngoing();
        assert this.getNextPiece() != null;

        int newRotationId = this.getNextPiece().getRotation().ordinal() + 1;
        if (newRotationId >= Tetromino.Rotation.values().length) {
            newRotationId = 0;
        }
        Tetromino.Rotation newRotation = Tetromino.Rotation.values()[newRotationId];

        this.getNextPiece().setRotation(newRotation);
    }

    public void stop() {
        this._isOngoing = false;
    }

    public static class Action {
        private final Tetromino.Rotation _rotation;
        private final int _position;

        public Action(Tetromino.Rotation rotation, int position) {
            this._rotation = rotation;
            this._position = position;
        }

        public Action(int position) {
            this(Tetromino.Rotation.DEFAULT, position);
        }

        public Tetromino.Rotation getRotation() { return this._rotation; }
        public int getPosition() { return this._position; }

        @Override public String toString() {
            return String.format("[Rotation: %s\tposition: %s]", this.getRotation(), this.getPosition());
        }
    }
}
