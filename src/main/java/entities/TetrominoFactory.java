package entities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by maianhvu on 16/03/2016.
 */
public class TetrominoFactory {

    private static TetrominoFactory instance = new TetrominoFactory();

    public static TetrominoFactory getInstance() {
        return instance;
    }

    /**
     * Properties
     */
    private HashMap<Tetromino, boolean[][]> _cacheMap;
    private final Random _randomizer = new Random();

    /**
     * Constructs a TetrominoFactory with a new cache for tetris blocks
     */
    private TetrominoFactory() {
        this._cacheMap = new HashMap<>();
    }

    public boolean[][] getBitmap(Tetromino block) {
        // Find in cache
        boolean[][] cachedBitmap = this._cacheMap.get(block);

        // Instantiate bitmap
        if (cachedBitmap == null) {
            // Calculate width and height
            boolean[][] originalBitmap = block.getType().bitmap;

            // Rotate if the rotation is not default
            if (block.getRotation() != Tetromino.Rotation.DEFAULT) {
                originalBitmap = rotateBitmap(originalBitmap, block.getRotation());
            }

            cachedBitmap = trim(originalBitmap);

            // Put into hash map
            this._cacheMap.put(block, cachedBitmap);
        }

        return cachedBitmap;
    }

    private static boolean[][] trim(boolean[][] bitmap) {
        int topLeftRow = bitmap.length;
        int topLeftCol = bitmap[0].length;
        int bottomRightRow = -1;
        int bottomRightCol = -1;

        for (int row = 0; row < bitmap.length; row++) {
            for (int col = 0; col < bitmap[0].length; col++) {
                // Skip empty cells
                if (!bitmap[row][col]) continue;

                if (row < topLeftRow) topLeftRow = row;
                if (col < topLeftCol) topLeftCol = col;
                if (row > bottomRightRow) bottomRightRow = row;
                if (col > bottomRightCol) bottomRightCol = col;
            }
        }

        // Empty map
        if (topLeftRow == bitmap.length || topLeftCol == bitmap[0].length ||
                bottomRightRow == -1 || bottomRightCol == -1) {
            return null;
        }

        // Trip map
        boolean[][] newMap = new boolean[bottomRightRow - topLeftRow + 1][bottomRightCol - topLeftCol + 1];

        // Copy values
        for (int i = 0; i < newMap.length; i++) {
            for (int j = 0; j < newMap[0].length; j++) {
                newMap[i][j] = bitmap[topLeftRow + i][topLeftCol + j];
            }
        }

        return newMap;
    }

    private static boolean[][] rotateBitmap(boolean[][] bitmap, Tetromino.Rotation rotation) {
        boolean[][] newBitmap = null;
        int bitmapHeight = bitmap.length;
        int bitmapWidth = bitmap[0].length;

        switch (rotation) {
            case ROTATED_90:
                // Swap dimensions
                newBitmap = new boolean[bitmapWidth][bitmapHeight];

                // Populate values
                for (int i = 0; i < bitmapHeight; i++) {
                    for (int j = 0; j < bitmapWidth; j++) {
                        newBitmap[j][bitmapHeight - i - 1] = bitmap[i][j];
                    }
                }
                return newBitmap;
            case ROTATED_180:
                // Retain dimensions
                newBitmap = new boolean[bitmapHeight][bitmapWidth];

                // Populate values
                for (int i = 0; i < bitmapHeight; i++) {
                    for (int j = 0; j < bitmapWidth; j++) {
                        newBitmap[bitmapHeight - i - 1][bitmapWidth - j - 1] = bitmap[i][j];
                    }
                }
                return newBitmap;
            case ROTATED_270:
                // Swap dimensions
                newBitmap = new boolean[bitmapWidth][bitmapHeight];

                // Populate values
                for (int i = 0; i < bitmapHeight; i++) {
                    for (int j = 0; j < bitmapWidth; j++) {
                        newBitmap[bitmapWidth - j - 1][i] = bitmap[i][j];
                    }
                }
                return newBitmap;
            default:
                return bitmap;
        }
    }

    public static void printBitmap(boolean[][] bitmap) {
        StringBuilder builder = new StringBuilder();
        Arrays.stream(bitmap).forEach(row -> {
            IntStream.range(0, row.length).mapToObj(index -> row[index] ? "██" : "░░")
                    .forEach(builder::append);
            builder.append("\n");
        });
        System.out.print(builder);
    }

    public Tetromino randomPiece() {
        Tetromino.Type type = Tetromino.Type.values()[this._randomizer.nextInt(Tetromino.Type.values().length)];
        return new Tetromino(type);
    }

}
