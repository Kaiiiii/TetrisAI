package tetris.logic;

import tetris.ui.State;

/**
 * Created by maianhvu on 24/03/2016.
 */
public class FieldAnalyzer {

	/**
	 * Fixed Properties
	 */
	private int[][] previewField;
	private State currentState = null;
	private int nextPiece = -1;
	public static final int[][][][] allPieces = {
			{{{1,1},{1,1}}},
			{{{1},{1},{1},{1}},{{1,1,1,1}}},
			{{{1,1},{1,0},{1,0}},{{1,0,0},{1,1,1}},{{0,1},{0,1},{1,1}},{{1,1,1},{0,0,1}}},
			{{{1,1},{0,1},{0,1}},{{1,1,1},{1,0,0}},{{1,0},{1,0},{1,1}},{{0,0,1},{1,1,1}}},
			{{{1,0},{1,1},{1,0}},{{0,1,0},{1,1,1}},{{0,1},{1,1},{0,1}},{{1,1,1},{0,1,0}}},
			{{{1,1,0},{0,1,1}},{{0,1},{1,1},{1,0}}},
			{{{0,1,1},{1,1,0}},{{1,0},{1,1},{0,1}}}
	};

	/**
	 * Analysed Move Properties
	 */
	 private int[] currMove = null;
	 private int currOrientation = -1;
	 private int currSlot = -1;
	 private int[] currBase = null;
	 private int currWidth = -1;
	 private int[][][] pBottom = null;

	 public FieldAnalyzer(State s) {
		 this.previewField = new int[State.ROWS][State.COLS];
		 currentState = s;
		 nextPiece = currentState.getNextPiece();
	 }

	 public Analysis analyze(int[] move) {

		 // Get information of current move
		 setMoveDetails(move);

		 // Lock in the piece
		 lockInPiece();

		 // Calculate analysis
		 return new Analysis(this.previewField);
	 }

	 private void setMoveDetails(int[] move) {
		 setCurrMove(move);
		 currOrientation = move[State.ORIENT];
		 currSlot = move[State.SLOT];
		 currBase = currentState.getTop();
		 int[][] widths = State.getpWidth();
		 currWidth = widths[nextPiece][currOrientation];
		 pBottom = State.getpBottom();
		 
		 // Copy state of current field to preview field
		 int[][] currentField = currentState.getField();
		 for (int fr=0;fr<20;fr++) {
			 for (int fc=0;fc<10;fc++) {
				 if (currentField[fr][fc] > 0) {
					 previewField[fr][fc] = 1;
				 } else {
					 previewField[fr][fc] = 0;
				 }
			 }
		 }
	 }

	 private void lockInPiece() {
		 int[] temp = new int[currWidth];

		 for (int j=0;j<currWidth;j++) {
			 temp[j]=currBase[j+currSlot];
		 }

		 int maxBottom = getpBottom(nextPiece, pBottom, currOrientation, currWidth);
		 addBottomHalf(nextPiece, pBottom, currOrientation, currWidth, temp, maxBottom);
		 int tempMax = getTempMax(currWidth, temp);
		 int firstAffectedRow = tempMax - maxBottom;
		 int [][] curPiece = allPieces[nextPiece][currOrientation];

		 for (int r=0;r<curPiece.length;r++) {
			 for (int c=0;c<curPiece[r].length;c++) {
				 if ((firstAffectedRow+r)>=20) {
					 break;
				 } else {
					 previewField[firstAffectedRow+r][currSlot+c] = curPiece[r][c];
				 }
			 }
		 }
	 }

	 //check all pBottom of piece in orientation 
	 public int getpBottom(int nextPiece, int[][][] pBottom, int pOrient, int pWidth) {
		 int maxBottom= 0;
		 for (int k=0;k<pWidth;k++) {
			 maxBottom = Math.max(maxBottom, pBottom[nextPiece][pOrient][k]);
		 }
		 return maxBottom;
	 }

	 //add the bottom part of piece
	 public void addBottomHalf(int nextPiece, int[][][] pBottom, int pOrient, int pWidth, int[] temp, int maxBottom) {

		 for (int a=0;a<pWidth;a++) {
			 if (pBottom[nextPiece][pOrient][a]==0) {
				 temp[a]+=maxBottom;
			 }
		 }
	 }

	 //compare all temp heights
	 public int getTempMax(int pWidth, int[] temp) {
		 int tempMax = 0;
		 for (int l=0;l<pWidth;l++) {
			 if (temp[l]>tempMax){
				 tempMax = temp[l];
			 }
		 }
		 return tempMax;
	 }

	 public int[] getCurrMove() {
		 return currMove;
	 }

	 public void setCurrMove(int[] currMove) {
		 this.currMove = currMove;
	 }

}
