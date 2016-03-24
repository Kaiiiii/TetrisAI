package logic;


import entities.Tetromino;
import entities.TetrominoFactory;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by maianhvu on 24/03/2016.
 */
public class FieldTest {

    private static final boolean[][] PIECE_O = new boolean[][] {
            {true, true},
            {true, true}
    };
    private static final boolean[][] PIECE_I = new boolean[][] {
            {true}, {true}, {true}, {true}
    };
    private static final boolean[][] PIECE_T = new boolean[][] {
            {false, true},
            {true, true},
            {false, true}
    };
    private static final boolean[][] PIECE_I_90 = new boolean[][] {
            {true, true, true, true}
    };
    private static final boolean[][] PIECE_Z = new boolean[][] {
            {true, true, false},
            {false, true, true}
    };

    private Field _field;

    @Before
    public void setUp() {
        this._field = new Field();
    }

    @Test
    public void Field_can_put_and_read() {
        boolean[][] piece = PIECE_O;

        assertThat(this._field.put(0, piece, 1), is(true));

        assertThat(this._field.hasPieceAt(
                this._field.getHeight() - piece.length,
                0,
                piece,
                1
        ), is(true));
    }

    @Test
    public void Field_will_put_value_given() {
        boolean[][] piece = PIECE_I;

        this._field.put(4, piece, 5);

        assertThat(this._field.hasPieceAt(
                this._field.getHeight() - piece.length,
                4,
                piece,
                5
        ), is(true));
    }

    @Test
    public void Field_can_put_all_pieces_on_top_of_each_other() {
        HashSet<Pair<Tetromino.Type, Tetromino.Type>> exceptions = new HashSet<>();
        exceptions.add(new Pair<>(Tetromino.Type.Z, Tetromino.Type.J));
        exceptions.add(new Pair<>(Tetromino.Type.O, Tetromino.Type.J));
        exceptions.add(new Pair<>(Tetromino.Type.L, Tetromino.Type.T));
        exceptions.add(new Pair<>(Tetromino.Type.L, Tetromino.Type.S));
        exceptions.add(new Pair<>(Tetromino.Type.L, Tetromino.Type.O));


        TetrominoFactory factory = TetrominoFactory.getInstance();

        List<Tetromino> firstPieces = new ArrayList<>();
        List<Tetromino> secondPieces = new ArrayList<>();

        Arrays.asList(Tetromino.Type.values()).stream()
                .map(type -> new Tetromino(type))
                .forEach(block -> {
                    firstPieces.add(block);
                    secondPieces.add(block);
                });

        firstPieces.stream().forEach(firstBlock -> {
            secondPieces.stream().forEach(secondBlock -> {
                boolean[][] firstBitmap = factory.getBitmap(firstBlock);
                boolean[][] secondBitmap = factory.getBitmap(secondBlock);

                Field field = new Field();

                field.put(0, firstBitmap, 1);
                field.put(0, secondBitmap, 2);

                assertThat(field.hasPieceAt(
                        field.getHeight() - firstBitmap.length,
                        0,
                        firstBitmap,
                        1
                ), is(true));

                int secondPieceRow = field.getHeight() - firstBitmap.length - secondBitmap.length;
                Pair<Tetromino.Type, Tetromino.Type> blockPair =
                        new Pair<>(firstBlock.getType(), secondBlock.getType());
                if (exceptions.contains(blockPair)) {
                    secondPieceRow++;
                }

                assertThat(field.hasPieceAt(
                        secondPieceRow,
                        0,
                        secondBitmap,
                        2
                ), is(true));

            });
        });

    }

    @Test
    public void Field_can_put_at_extremes() {
        assertThat(this._field.put(0, PIECE_I, 1), is(true));
        assertThat(this._field.put(9, PIECE_I, 1), is(true));
    }

    @Test
    public void Field_clears_when_filled() {
        assert this._field.getWidth() % 2 == 0; // Even width

        int failures = (int) IntStream.range(0, this._field.getWidth() / 2)
                .map(col -> col * 2)
                .mapToObj(col -> this._field.put(col, PIECE_O, 1))
                .filter(success -> success == Field.CODE_LOSING_MOVE)
                .count();

        assertThat(failures, is(0));

        int[][] fieldValues = this._field.getState();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < this._field.getWidth(); j++) {
                assertThat(fieldValues[this._field.getHeight() - i - 1][j], is(0));
            }
        }
    }

    @Test
    public void Field_clears_correctly_with_complicated_state() {
        // Constructs state
        this._field.put(1, PIECE_I_90, 1);
        this._field.put(5, PIECE_I_90, 1);
        this._field.put(9, PIECE_I, 1);
        IntStream.of(1, 3, 5).forEach(pos -> this._field.put(pos, PIECE_Z, 2));
        this._field.put(7, PIECE_T, 3);
        this._field.put(0, PIECE_I, 1);

        // Expect the third row from bottom to be empty
        int[][] fieldValues = this._field.getState();
        int row = this._field.getHeight() - 3;
        assertThat(
                (int) IntStream.range(0, this._field.getWidth())
                        .filter(index -> fieldValues[row][index] > 0)
                        .count(),
                is(equalTo(0))
        );
    }
}
