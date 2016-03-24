package ui;

import ai.MinimizeRoughnessPlayer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Pair;
import logic.*;

import java.util.stream.IntStream;

/**
 * Created by maianhvu on 16/03/2016.
 */
public class ViewController implements Runnable {

    private static final double HEIGHT_COL_LABEL = 30.0;
    private static final double SIZE_CELL = 20.0;
    private static final double STROKE_WIDTH_BORDER_CELL = 2.0;
    private static final double STROKE_WIDTH_BORDER_BLOCK = 1.0;
    private static final double PADDING_FIELD = 0.0;
    private static final int THRESHOLD_DIFFING = 5;

    private static final Color[] COLORS_FILL_CELL = new Color[] {
            Color.rgb(244, 67, 54),
            Color.rgb(3, 169, 244),
            Color.rgb(139, 195, 74),
            Color.rgb(255, 193, 7),
            Color.rgb(103, 58, 183),
            Color.rgb(121, 85, 72),
            Color.rgb(96, 125, 139)
    };
    private static final Color[] COLORS_STROKE_CELL = new Color[] {
            Color.rgb(181, 48, 38),
            Color.rgb(2, 119, 189),
            Color.rgb(85, 139, 47),
            Color.rgb(255, 143, 0),
            Color.rgb(69, 39, 160),
            Color.rgb(78, 52, 46),
            Color.rgb(55, 71, 79)
    };
    private static final Color COLOR_STROKE_BORDER_FIELD = Color.rgb(0,0,0);

    private static final int DELAY_PUT_PIECE = 150;
    private static final int DELAY_TERMINATION = 2000;

    private double _drawOriginX;
    private double _drawOriginY;
    private double _drawWidth;
    private double _drawHeight;
    private double _blockSize;

    private Player _player;
    private int[][] _previousField;
    private int _diffCounter = 0;

    private Stage _primaryStage;

    /**
     * Properties
     */
    @FXML private Canvas _fieldCanvas;
    @FXML private Canvas _previewCanvas;
    @FXML private Label _turnLabel;
    @FXML private Label _scoreLabel;

    private Game _game;

    @FXML public void initialize() {
        // Draw border
        drawBorder(this._fieldCanvas);
        drawBorder(this._previewCanvas);
    }

    public void registerGame(Game game) {
        this._game = game;

        // Copy the state of the game
        this._previousField = new int[game.getField().getHeight()][game.getField().getWidth()];
        this.copyGameState();

        double fieldWidth = this._fieldCanvas.getWidth() - PADDING_FIELD * 2;
        double fieldHeight = this._fieldCanvas.getHeight() - PADDING_FIELD * 2;

        this._blockSize = Math.min(
                fieldWidth / game.getField().getWidth(),
                fieldHeight / game.getField().getHeight()
        );

        this._drawWidth = this._blockSize * game.getField().getWidth();
        this._drawHeight = this._blockSize * game.getField().getHeight();

        this._drawOriginX = PADDING_FIELD;
        this._drawOriginY = this._fieldCanvas.getHeight() - PADDING_FIELD - this._drawHeight - HEIGHT_COL_LABEL;

        // Draw column labels
        GraphicsContext context = this._fieldCanvas.getGraphicsContext2D();
        context.setStroke(Color.BLACK);
        context.setTextAlign(TextAlignment.CENTER);
        context.setTextBaseline(VPos.CENTER);

        double drawTextY = this._fieldCanvas.getHeight() - HEIGHT_COL_LABEL / 2;
        IntStream.range(0, game.getField().getWidth())
                .mapToObj(index -> new Pair<>(index + 1, (index + 0.5) * this._blockSize))
                .forEach(drawData -> context.fillText(
                        drawData.getKey().toString(),
                        drawData.getValue(),
                        drawTextY)
                );

        // Draw field boundaries
        this.drawFieldBoundaries();

    }

    private void drawFieldBoundaries() {
        GraphicsContext context = this._fieldCanvas.getGraphicsContext2D();
        context.setStroke(COLOR_STROKE_BORDER_FIELD);
        context.strokeRect(this._drawOriginX, this._drawOriginY,
                this._drawWidth, this._drawHeight);
    }

    public void startGame() {
        // Start the game
        this._game.start();
        this.renderGameState();
    }

    public void letAiPlay(Player player, Stage primaryStage) {
        this._player = player;
        this._primaryStage = primaryStage;
        Platform.runLater(this);
    }

    public void letHumanPlay(Stage primaryStage) {
        // Remember the stage
        this._primaryStage = primaryStage;

        primaryStage.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                this._game.rotateLeft();
            } else if (event.getCode() == KeyCode.RIGHT) {
                this._game.rotateRight();
            } else try {
                int position = Integer.parseInt(event.getText()) - 1;
                if (position < 0) position = 9;

                Game.Action action = new Game.Action(
                        this._game.getNextPiece().getRotation(),
                        position
                );
//                System.out.println(FieldAnalyzer.getInstance().analyze(
//                        this._game,
//                        action));

                this._game.putPiece(position);
            } catch (NumberFormatException e) {
                event.consume();
            }
            this.renderGameState();
        });
    }

    private void renderGameState() {
        this.drawField();
        this.drawPreview();

        this._turnLabel.setText(Integer.toString(this._game.getCurrentTurn()));
        this._scoreLabel.setText(Integer.toString(this._game.getCurrentScore()));
    }

    public void drawPreview() {
        assert this._game.isOngoing();
        assert this._game.getNextPiece() != null;

        boolean[][] blockBitmap = this._game.getNextPieceBitmap();
        int blockId = this._game.getNextPieceIdentifier();

        double previewWidth = blockBitmap[0].length * SIZE_CELL;
        double previewHeight = blockBitmap.length * SIZE_CELL;

        double originX = (this._previewCanvas.getWidth() - previewWidth) / 2;
        double originY = (this._previewCanvas.getHeight() - previewHeight)/ 2;

        GraphicsContext context = this._previewCanvas.getGraphicsContext2D();

        int colorIndex = blockId - 1;
        context.setFill(COLORS_FILL_CELL[colorIndex]);
        context.setStroke(COLORS_STROKE_CELL[colorIndex]);
        context.setLineWidth(STROKE_WIDTH_BORDER_CELL);

        // Clear first
        context.clearRect(1, 1, this._previewCanvas.getWidth() - 2, this._previewCanvas.getWidth() - 2);

        for (int i = 0; i < blockBitmap.length; i++) {
            for (int j = 0; j < blockBitmap[0].length; j++) {
                if (!blockBitmap[i][j]) continue;
                context.fillRect(originX + j * SIZE_CELL, originY + i * SIZE_CELL, SIZE_CELL, SIZE_CELL);
                context.strokeRect(originX + j * SIZE_CELL, originY + i * SIZE_CELL, SIZE_CELL, SIZE_CELL);
            }
        }
    }

    private void drawBorder(Canvas canvas) {
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.setStroke(Color.BLACK);
        context.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void drawField() {

        // TODO: Implement smart diffing
        GraphicsContext context = this._fieldCanvas.getGraphicsContext2D();
        int[][] fieldState = this._game.getField().getState();

        // Calculate drawing data
        context.setLineWidth(STROKE_WIDTH_BORDER_BLOCK);

        boolean isDiffing = this.isDiffing();

        if (!isDiffing) {
            context.clearRect(this._drawOriginX, this._drawOriginY, this._drawWidth, this._drawHeight);
            this.drawFieldBoundaries();
        }

        for (int row = 0; row < fieldState.length; row++) {
            for (int col = 0; col < fieldState[0].length; col++) {
                // Implement diffing
                if (isDiffing && this._previousField[row][col] == fieldState[row][col]) {
                    continue; // Skip similar values
                }

                int value = fieldState[row][col];

                // Clear field
                if (value == 0) {
                    if (isDiffing) {
                        context.clearRect(
                                this._drawOriginX + col * this._blockSize,
                                this._drawOriginY + row * this._blockSize,
                                this._blockSize,
                                this._blockSize
                        );
                    }
                    continue;
                }

                // Draw block
                int colorIndex = value - 1;
                context.setFill(COLORS_FILL_CELL[colorIndex]);
                context.setStroke(COLORS_STROKE_CELL[colorIndex]);
                context.fillRect(
                        this._drawOriginX + col * this._blockSize,
                        this._drawOriginY + row * this._blockSize,
                        this._blockSize, this._blockSize
                        );
                context.strokeRect(
                        this._drawOriginX + col * this._blockSize,
                        this._drawOriginY + row * this._blockSize,
                        this._blockSize,
                        this._blockSize
                        );
            }
        }

        // Reset new field
        this.copyGameState();
    }

    @Override
    public void run() {
        if (this._game.isOngoing() && this._game.getNextPiece() != null) {
            this.renderGameState();

            Game.Action action = this._player.getNextMove(this._game);
            if (action == null) {
                this._game.stop();
                this.scheduleForTermination();

            } else {
                // Action valid, game is still going on
                this._game.performAction(this._player.getNextMove(this._game));

                try {
                    Thread.sleep(DELAY_PUT_PIECE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(this);
            }
        }
    }

    private void scheduleForTermination() {
        assert this._primaryStage != null;
        Platform.runLater(() -> {
            try {
                Thread.sleep(DELAY_TERMINATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this._primaryStage.close();
        });
    }

    private void copyGameState() {
        int[][] gameState = this._game.getField().getState();
        IntStream.range(0, gameState.length)
                .forEach(index -> {
                    System.arraycopy(
                            gameState[index], 0,
                            this._previousField[index], 0,
                            this._game.getField().getWidth()
                    );
                });
    }

    private boolean isDiffing() {
        this._diffCounter = (this._diffCounter + 1) % THRESHOLD_DIFFING;
        return this._diffCounter == 0;
    }
}
