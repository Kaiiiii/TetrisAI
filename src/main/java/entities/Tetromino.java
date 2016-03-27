package entities;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;

/**
 * Created by maianhvu on 16/03/2016.
 */
public class Tetromino {

    private static final int SIZE_HEIGHT_BITMAP = 2;
    private static final int SIZE_ROW_BITMAP = 4;

    /**
     * Types
     */
    public enum Type {
        T(0b11100100),
        I(0b11110000),
        Z(0b11000110),
        S(0b01101100),
        O(0b11001100),
        L(0b00101110),
        J(0b11100010);

        final boolean bitmap[][] = new boolean[SIZE_HEIGHT_BITMAP][SIZE_ROW_BITMAP];
        Type(int bits) {
            // Populate boolean array
            for (int i = SIZE_HEIGHT_BITMAP - 1; i >= 0; i--) {
                for (int j = SIZE_ROW_BITMAP - 1; j >= 0; j--) {
                    bitmap[i][j] = (bits & 1) == 1;
                    bits >>= 1;
                }
            }
        }
    }

    public enum Rotation {
        DEFAULT,
        ROTATED_90,
        ROTATED_180,
        ROTATED_270
    }

    /**
     * Constants
     */
    private static final Rotation[] ROTATIONS_USEFUL_O = new Rotation[] { Rotation.DEFAULT };
    private static final Rotation[] ROTATIONS_USEFUL_I = new Rotation[] { Rotation.DEFAULT, Rotation.ROTATED_90 };
    private static final Rotation[] ROTATIONS_USEFUL_T = Rotation.values();
    private static final Rotation[] ROTATIONS_USEFUL_J = Rotation.values();
    private static final Rotation[] ROTATIONS_USEFUL_L = Rotation.values();
    private static final Rotation[] ROTATIONS_USEFUL_Z = new Rotation[] {
            Rotation.DEFAULT, Rotation.ROTATED_90
    };
    private static final Rotation[] ROTATIONS_USEFUL_S = new Rotation[]{
            Rotation.DEFAULT, Rotation.ROTATED_90
    };

    /**
     * Properties
     */
    private final Type _type;
    private Rotation _rotation;

    public Tetromino(Type t, Rotation r) {
        this._type = t;
        this._rotation = r;
    }

    public Tetromino(Type type) {
        this(type, Rotation.DEFAULT);
    }

    @Override
    public int hashCode() {
        return new Integer(this._type.ordinal() * 127 + this._rotation.ordinal()).hashCode();
    }

    @Override public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (!(o instanceof Tetromino)) return false;
        Tetromino t = (Tetromino) o;
        if (t._type != this._type) return false;
        if (t._rotation != this._rotation) return false;
        return true;
    }

    @Override public String toString() {
        return String.format("[Block %s\t%s]", this._type, this._rotation);
    }

    public Type getType() {
        return this._type;
    }

    public Rotation getRotation() {
        return this._rotation;
    }

    public void setRotation(Rotation rotation) {
        this._rotation = rotation;
    }

    public int getBlockId() { return this._type.ordinal() + 1; }

    public Rotation[] getUsefulRotations() {
        switch (this._type) {
            case O:
                return ROTATIONS_USEFUL_O;
            case I:
                return ROTATIONS_USEFUL_I;
            case T:
                return ROTATIONS_USEFUL_T;
            case J:
                return ROTATIONS_USEFUL_J;
            case L:
                return ROTATIONS_USEFUL_L;
            case Z:
                return ROTATIONS_USEFUL_Z;
            case S:
                return ROTATIONS_USEFUL_S;
            default:
                return new Rotation[] {};
        }
    }
}

