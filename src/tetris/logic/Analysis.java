package tetris.logic;

/**
 * Created by maianhvu on 24/03/2016.
 */
public class Analysis {

	private final int _holes;
	private final int _height;
	private final int _cells;
	private final int _highestSlope;
	private final int _roughness;

	public Analysis(int[][] field) {
		int holes = 0;
		int roughness = 0;
		@SuppressWarnings("unused")
        int aggregatedHeight = 0;
		int maxHeight = 0;
		int cells = 0;
		int highestSlope = 0;

		Integer previousColumnHeight = null;

		for (int col = 0; col < field[0].length; col++) {
			boolean columnHasBlocks = false;
			int distanceFromTop = 0;

			for (int row = 19; row >= 0; row--) {
				// This should be pretty straight-forward
				if (field[row][col] > 0) {
					columnHasBlocks = true;
					cells++;
				}

				// ROUGHNESS & HEIGHT
				// We can calculate the height of the column by first calculating
				// the distance from the top, then substract this distance away
				// from the total height of the column
				if (!columnHasBlocks && field[row][col] == 0) distanceFromTop++;

				// HOLES
				// If the column already has some block earlier up, and suddenly this row
				// below has an empty value, we know that it is a hole
				if (columnHasBlocks && field[row][col] == 0) holes++;
			}

			// HEIGHT update.
			int currentHeight = field.length - distanceFromTop;
			if (currentHeight>maxHeight){
				maxHeight = currentHeight;
			}
			aggregatedHeight += currentHeight;

			// ROUGHNESS
			// When this column is the first column to be considered, we don't want (or don't have)
			// to calculate the roughness at this moment. Just update the previous column height and
			// move on.
			if (previousColumnHeight == null) {
				previousColumnHeight = currentHeight;
			}
			// Add the difference of height onto the roughness, and set the previous column height
			// to the current height so that it can be used to calculate the roughness in the next
			// iteration of this loop.
			else {
				int slope = Math.abs(currentHeight - previousColumnHeight);

				// HIGHEST SLOPE update
				if (slope > highestSlope) highestSlope = slope;

				// ROUGHNESS update
				roughness += slope;
				previousColumnHeight = currentHeight;
			}
		}

		// After we have already calculated the variables, set all of them
		this._cells = cells;
		this._height = maxHeight;
//		this._height = aggregatedHeight;
		this._highestSlope = highestSlope;
		this._holes = holes;
		this._roughness = roughness;
	}

	public int getCellsCount() { return this._cells; }
	public int getAggregateHeight() { return this._height; }
	public int getHighestSlope() { return this._highestSlope; }
	public int getHolesCount() { return this._holes; }
	public int getRoughness() { return this._roughness; }

	@Override public String toString() {
		return String.format("Cells: %d\nHeight: %d\nHighest Slope: %d\nHoles: %d\nRoughness: %d",
				this.getCellsCount(),
				this.getAggregateHeight(),
				this.getHighestSlope(),
				this.getHolesCount(),
				this.getRoughness());
	}
}
