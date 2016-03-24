package entities;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by maianhvu on 24/03/2016.
 */
public class TetrominoFactoryTest {

    private static TetrominoFactory factory = TetrominoFactory.getInstance();

    @Test
    public void Tetromino_factory_generates_default_O_block_correctly() {
        Tetromino oBlock = new Tetromino(Tetromino.Type.O);
        boolean[][] oBitmap = new boolean[][] {
                {true, true},
                {true, true}
        };

        assertThat(factory.getBitmap(oBlock), is(equalTo(oBitmap)));
    }

    @Test
    public void Tetromino_factory_generates_default_T_block_correctly() {
        Tetromino tBlock = new Tetromino(Tetromino.Type.T);
        boolean[][] tBitmap = new boolean[][] {
                {true, true, true},
                {false, true, false}
        };

        assertThat(factory.getBitmap(tBlock), is(equalTo(tBitmap)));
    }

    @Test
    public void Tetromino_caches_based_on_both_type_and_rotation() {
        Tetromino blockI1 = new Tetromino(Tetromino.Type.I);
        Tetromino blockI2 = new Tetromino(Tetromino.Type.I, Tetromino.Rotation.ROTATED_90);
        factory.getBitmap(blockI1);
        assertThat(factory.getBitmap(blockI2), is(equalTo(new boolean[][] {
                {true},{true},{true},{true}
        })));
        assertThat(factory.getBitmap(blockI1), is(not(equalTo(factory.getBitmap(blockI2)))));
    }

    //-------------------------------------------------------------------------------------------------
    //
    // ROTATING LEFT
    //
    //-------------------------------------------------------------------------------------------------
    @Test
    public void Tetromino_factory_rotates_left_T_block_correctly() {
        Tetromino tBlockRotatedLeft = new Tetromino(Tetromino.Type.T, Tetromino.Rotation.ROTATED_270);
        boolean[][] bitmap = new boolean[][] {
                {true, false},
                {true, true},
                {true, false}
        };

        assertThat(factory.getBitmap(tBlockRotatedLeft), is(equalTo(bitmap)));
    }

    @Test
    public void Tetromino_factory_rotates_left_L_block_correctly() {
        Tetromino lBlockRotatedLeft = new Tetromino(Tetromino.Type.L, Tetromino.Rotation.ROTATED_270);
        boolean[][] bitmap = new boolean[][] {
                {true, true},
                {false, true},
                {false, true}
        };

        assertThat(factory.getBitmap(lBlockRotatedLeft), is(equalTo(bitmap)));
    }

    @Test
    public void Tetromino_factory_rotates_left_J_block_correctly() {
        Tetromino jBlockRotatedLeft = new Tetromino(Tetromino.Type.J, Tetromino.Rotation.ROTATED_270);
        boolean[][] bitmap = new boolean[][] {
                {true, true},
                {true, false},
                {true, false}
        };

        assertThat(factory.getBitmap(jBlockRotatedLeft), is(equalTo(bitmap)));
    }

    @Test
    public void Tetromino_factory_rotates_left_I_block_correctly() {
        Tetromino iBlockRotatedLeft = new Tetromino(Tetromino.Type.I, Tetromino.Rotation.ROTATED_270);
        boolean[][] bitmap = new boolean[][] {
                {true}, {true}, {true}, {true}
        };

        assertThat(factory.getBitmap(iBlockRotatedLeft), is(equalTo(bitmap)));
    }

    @Test
    public void Tetromino_factory_rotates_left_Z_block_correctly() {
        Tetromino zBlockRotatedLeft = new Tetromino(Tetromino.Type.Z, Tetromino.Rotation.ROTATED_270);
        boolean[][] bitmap = new boolean[][] {
                {false, true},
                {true, true},
                {true, false}
        };

        assertThat(factory.getBitmap(zBlockRotatedLeft), is(equalTo(bitmap)));
    }

    @Test
    public void Tetromino_factory_rotates_left_S_block_correctly() {
        Tetromino sBlockRotatedLeft = new Tetromino(Tetromino.Type.S, Tetromino.Rotation.ROTATED_270);
        boolean[][] bitmap = new boolean[][] {
                {true, false},
                {true, true},
                {false, true}
        };

        assertThat(factory.getBitmap(sBlockRotatedLeft), is(equalTo(bitmap)));
    }

    @Test
    public void Tetromino_factory_retains_O_shape_on_rotation() {
        Tetromino oBlockRotated = new Tetromino(Tetromino.Type.O, Tetromino.Rotation.ROTATED_270);
        assertThat(factory.getBitmap(oBlockRotated),
                is(equalTo(factory.getBitmap(new Tetromino(Tetromino.Type.O)))));
    }

    //-------------------------------------------------------------------------------------------------
    //
    // ROTATING RIGHT
    //
    //-------------------------------------------------------------------------------------------------

    @Test
    public void Tetromino_factory_rotates_right_T_block_correctly() {
        Tetromino tBlockRotatedRight = new Tetromino(Tetromino.Type.T, Tetromino.Rotation.ROTATED_90);
        boolean[][] bitmap = new boolean[][] {
                {false, true},
                {true, true},
                {false, true}
        };

        assertThat(factory.getBitmap(tBlockRotatedRight), is(equalTo(bitmap)));
    }

    @Test
    public void Tetromino_factory_rotates_right_L_block_correctly() {
        Tetromino lBlockRotatedRight = new Tetromino(Tetromino.Type.L, Tetromino.Rotation.ROTATED_90);
        boolean[][] bitmap = new boolean[][] {
                {true, false},
                {true, false},
                {true, true}
        };

        assertThat(factory.getBitmap(lBlockRotatedRight), is(equalTo(bitmap)));
    }

    @Test
    public void Tetromino_factory_rotates_right_J_block_correctly() {
        Tetromino jBlockRotatedRight = new Tetromino(Tetromino.Type.J, Tetromino.Rotation.ROTATED_90);
        boolean[][] bitmap = new boolean[][] {
                {false, true},
                {false, true},
                {true, true}
        };

        assertThat(factory.getBitmap(jBlockRotatedRight), is(equalTo(bitmap)));
    }

    @Test
    public void Tetromino_factory_rotates_right_I_block_correctly() {
        Tetromino iBlockRotatedRight = new Tetromino(Tetromino.Type.I, Tetromino.Rotation.ROTATED_90);
        boolean[][] bitmap = new boolean[][] {
                {true}, {true}, {true}, {true}
        };

        assertThat(factory.getBitmap(iBlockRotatedRight), is(equalTo(bitmap)));
    }

    @Test
    public void Tetromino_factory_rotates_right_Z_block_correctly() {
        Tetromino zBlockRotatedRight = new Tetromino(Tetromino.Type.Z, Tetromino.Rotation.ROTATED_90);
        boolean[][] bitmap = new boolean[][] {
                {false, true},
                {true, true},
                {true, false}
        };

        assertThat(factory.getBitmap(zBlockRotatedRight), is(equalTo(bitmap)));
    }

    @Test
    public void Tetromino_factory_rotates_right_S_block_correctly() {
        Tetromino sBlockRotatedRight = new Tetromino(Tetromino.Type.S, Tetromino.Rotation.ROTATED_90);
        boolean[][] bitmap = new boolean[][] {
                {true, false},
                {true, true},
                {false, true}
        };

        assertThat(factory.getBitmap(sBlockRotatedRight), is(equalTo(bitmap)));
    }

    //-------------------------------------------------------------------------------------------------
    //
    // ROTATING 180 DEGREES
    //
    //-------------------------------------------------------------------------------------------------

    @Test
    public void Tetromino_factory_rotates_180_deg_T_block_correctly() {
        Tetromino tBlockRotated180Deg = new Tetromino(Tetromino.Type.T, Tetromino.Rotation.ROTATED_180);
        boolean[][] bitmap = new boolean[][] {
                {false, true, false},
                {true, true, true}

        };

        assertThat(factory.getBitmap(tBlockRotated180Deg), is(equalTo(bitmap)));
    }

    @Test
    public void Tetromino_factory_rotates_180_deg_L_block_correctly() {
        Tetromino lBlockRotated180Deg = new Tetromino(Tetromino.Type.L, Tetromino.Rotation.ROTATED_180);
        boolean[][] bitmap = new boolean[][] {
                {true, true, true},
                {true, false, false}
        };

        assertThat(factory.getBitmap(lBlockRotated180Deg), is(equalTo(bitmap)));
    }

    @Test
    public void Tetromino_factory_rotates_180_deg_J_block_correctly() {
        Tetromino jBlockRotated180Deg = new Tetromino(Tetromino.Type.J, Tetromino.Rotation.ROTATED_180);
        boolean[][] bitmap = new boolean[][] {
                {true, false, false},
                {true, true, true}
        };

        assertThat(factory.getBitmap(jBlockRotated180Deg), is(equalTo(bitmap)));
    }

    @Test
    public void Tetromino_factory_rotates_180_deg_I_block_correctly() {
        Tetromino iBlockRotated180Deg = new Tetromino(Tetromino.Type.I, Tetromino.Rotation.ROTATED_180);
        boolean[][] bitmap = new boolean[][] {
                {true, true, true, true}
        };

        assertThat(factory.getBitmap(iBlockRotated180Deg), is(equalTo(bitmap)));
    }

    @Test
    public void Tetromino_factory_rotates_180_deg_Z_block_to_original() {
        Tetromino zBlockRotated180Deg = new Tetromino(Tetromino.Type.Z, Tetromino.Rotation.ROTATED_180);
        assertThat(factory.getBitmap(zBlockRotated180Deg), is(equalTo(
                factory.getBitmap(new Tetromino(Tetromino.Type.Z))
        )));
    }

    @Test
    public void Tetromino_factory_rotates_180_deg_S_block_to_original() {
        Tetromino sBlockRotated180Deg = new Tetromino(Tetromino.Type.S, Tetromino.Rotation.ROTATED_180);
        assertThat(factory.getBitmap(sBlockRotated180Deg), is(equalTo(
                factory.getBitmap(new Tetromino(Tetromino.Type.S))
        )));
    }
}
