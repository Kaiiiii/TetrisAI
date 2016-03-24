package ai;

import entities.Tetromino;
import logic.Game;
import logic.Player;

import java.util.Random;

/**
 * Created by maianhvu on 24/03/2016.
 */
public class RandomPlayer implements Player {
    @Override
    public Game.Action getNextMove(Game game) {
        Random random = new Random();
        Tetromino nextPiece = game.getNextPiece();

        Tetromino.Rotation[] availableRotations = game.getNextPiece().getUsefulRotations();
        Tetromino.Rotation chosenRotation = availableRotations[random.nextInt(availableRotations.length)];

        int[] availablePositions = game.getAvailablePositionsFor(chosenRotation);
        int chosenPosition = availablePositions[random.nextInt(availablePositions.length)];

        return new Game.Action(chosenRotation, chosenPosition);
    }
}
