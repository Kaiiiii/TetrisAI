/**
 * Created by maianhvu on 04/04/2016.
 */
public class ControlledState extends State {
    private static final String CELL_OCCUPIED = "██";
    private static final String CELL_EMPTY = "░░";

    public void setNextPiece(int piece) {
        this.nextPiece = piece;
    }

    public void forceMove(int piece, int orient, int slot) {
        this.setNextPiece(piece);
        this.makeMove(orient, slot);
    }

    public void forceMove(int piece, int move) {
        this.setNextPiece(piece);
        this.makeMove(move);
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = ROWS - 1; row >= 0; row--) {
            for (int col = 0; col < COLS; col++) {
                sb.append(this.getField()[row][col] > 0 ? CELL_OCCUPIED : CELL_EMPTY);
            }
            if (row != 0) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
