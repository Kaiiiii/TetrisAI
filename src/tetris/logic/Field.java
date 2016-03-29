package tetris.logic;

public class Field {
	
	private static final int WIDTH_FIELD = 10;
	private static final int HEIGHT_FIELD = 21;
	
	private final int[][] _field;
	
	public Field() {
		this._field = new int[HEIGHT_FIELD][WIDTH_FIELD];
	}
}
