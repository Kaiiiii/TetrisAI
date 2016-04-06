package tetris.agents;

import tetris.model.*;

public class PlayerSkeleton {

	//piece, orientation, rows, cols
	//1 = block, 0 = hole
	public static final int[][][][] allPieces = {
			{{{1,1},{1,1}}},
			{{{1},{1},{1},{1}},{{1,1,1,1}}},
			{{{1,1},{1,0},{1,0}},{{1,0,0},{1,1,1}},{{0,1},{0,1},{1,1}},{{1,1,1},{0,0,1}}},
			{{{1,1},{0,1},{0,1}},{{1,1,1},{1,0,0}},{{1,0},{1,0},{1,1}},{{0,0,1},{1,1,1}}},
			{{{1,0},{1,1},{1,0}},{{0,1,0},{1,1,1}},{{0,1},{1,1},{0,1}},{{1,1,1},{0,1,0}}},
			{{{1,1,0},{0,1,1}},{{0,1},{1,1},{1,0}}},
			{{{0,1,1},{1,1,0}},{{1,0},{1,1},{0,1}}}
	};
	//default (current best)
	//private double aggregatedHeightRatio = -0.510066;
	//private double holeRatio = -0.35663;
	//private double bumpinessRatio = -0.184483;
	//private double clearRatio = -0.760666;
	//private double maxHeightRatio = -0;
	//private double maxSlopeRatio = -0.1;
	//private double totalHolesRatio = -0;
	
	private double aggregatedHeightRatio = 0;
	private double holeRatio = 0;
	private double bumpinessRatio = 0;
	private double clearRatio = 0;
	private double maxHeightRatio = 0;
	private double maxSlopeRatio = 0;
	private double totalHolesRatio = 0;

	//pieces ordering from State = O I L J T S Z
	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		int[] aggregatedHeightPoints = calHeightPoints(s, legalMoves)[0];
		int[] maxHeightPoints = calHeightPoints(s, legalMoves)[1];
		int[] holeCausedPoints = calHolePoints(s, legalMoves);
		int[] bumpinessPoints = calSlopePoints(s, legalMoves)[0];
		int[] maxSlopePoints = calSlopePoints(s, legalMoves)[1];
		int[] clearPoints = calFieldPoints(s, legalMoves)[0];
		int[] totalHolesPoints = calFieldPoints(s, legalMoves)[1];
		int result = 0;

		for (int i=1; i<legalMoves.length; i++) {
			double resultPoint = aggregatedHeightRatio*(double)aggregatedHeightPoints[result]*(-1) 
					+ holeRatio*(double)holeCausedPoints[result]*(-1)
							+ bumpinessRatio*(double)bumpinessPoints[result]*(-1) 
									+ clearRatio*(double)clearPoints[result]
											+ maxSlopeRatio*(double)maxSlopePoints[result]*(-1)
													+ maxHeightRatio*(double)maxHeightPoints[result]*(-1)
															+ totalHolesRatio*(double)totalHolesPoints[result]*(-1);
			double checkPoint = aggregatedHeightRatio*(double)aggregatedHeightPoints[i]*(-1) 
					+ holeRatio*(double)holeCausedPoints[i]*(-1) 
							+ bumpinessRatio*(double)bumpinessPoints[i]*(-1) 
									+ clearRatio*clearPoints[i]
											+ maxSlopeRatio*(double)maxSlopePoints[i]*(-1)
													+ maxHeightRatio*(double)maxHeightPoints[i]*(-1)
															+ totalHolesRatio*(double)totalHolesPoints[i]*(-1);
			if (resultPoint<=checkPoint){
				result = i;
			}
		}

		return result;
	}

	public void setRatios(double[] ratios){
		if (ratios.length >= 1)
			aggregatedHeightRatio = ratios[0];
		if (ratios.length >= 2)
			holeRatio = ratios[1];
		if (ratios.length >= 3)
			bumpinessRatio = ratios[2];
		if (ratios.length >= 4)
			clearRatio = ratios[3];
		if (ratios.length >= 5)
			maxHeightRatio = ratios[4];
		if (ratios.length >= 6)
			maxSlopeRatio = ratios[5];
		if (ratios.length == 7)
			totalHolesRatio = ratios[6];
	}
	
	//calculate the points from resulted row cleared in each affected column.
	//return the resulted rows cleared after move. higher the better.
	//points[0] == rows cleared
	//points[1] == total holes count
	private int[][] calFieldPoints(State s, int[][] legalMoves) {    
		int nextPiece = s.getNextPiece();
		int[][] points = new int[2][legalMoves.length];
		int[] boardHeights = s.getTop();
		int[][] allWidth = State.getpWidth();
		int[][] field = s.getField();
		int[][][] pBottom = State.getpBottom();
		for (int i=0;i<legalMoves.length;i++){
			int pOrient = legalMoves[i][0];
			int pSlot = legalMoves[i][1];
			int pWidth = allWidth[nextPiece][pOrient];
			int[] temp = new int[pWidth];
			int[][] tempField = new int[20][10];
			for (int fr=0;fr<20;fr++) {
				for (int fc=0;fc<10;fc++) {
					tempField[fr][fc] = field[fr][fc];
				}
			}

			for (int j=0;j<pWidth;j++) {
				temp[j]=boardHeights[j+pSlot];
			}

			int maxBottom = getpBottom(nextPiece, pBottom, pOrient, pWidth);
			addBottomHalf(nextPiece, pBottom, pOrient, pWidth, temp, maxBottom);
			int tempMax = getTempMax(pWidth, temp);
			int firstAffectedRow = tempMax - maxBottom;
			int [][] curPiece = allPieces[nextPiece][pOrient];

			for (int r=0;r<curPiece.length;r++) {
				for (int c=0;c<curPiece[r].length;c++) {
					if ((firstAffectedRow+r)>=20) {
						break;
					} else {
						tempField[firstAffectedRow+r][pSlot+c] = curPiece[r][c];
					}
				}
			}

			int count = 0;
			int clear = 1;
			for (int a=0;a<curPiece.length;a++) {
				clear = 1;
				for (int b=0;b<10;b++) {
					if ((firstAffectedRow+a)>=20 || tempField[firstAffectedRow+a][b] < 1) {
						clear = 0;
						break;
					}
				}
				count += clear;
			}

			int holes = countHoles(tempField);

			points[0][i] = count;
			points[1][i] = holes;
		}

		return points;
	}

	private int countHoles(int[][] tempField) {
		int holes =0;
		boolean flag = false;
		for (int c=0;c<tempField[0].length;c++) {
			flag = false;
			for (int r=(tempField.length-1); r>=0; r--) {
				if (!flag && tempField[r][c]>0) {
					flag = true;
				}
				if (flag && tempField[r][c]==0) {
					holes+=1;
				}
			}
		}
		return holes;
	}

	//calculate the points from resulted holes in each affected column.
	//return the resulted holes after move. lower the better.
	//points[0] == bumpiness or roughness
	//points[1] == max slope
	private int[][] calSlopePoints(State s, int[][] legalMoves) {    
		int nextPiece = s.getNextPiece();
		int[][] points = new int[2][legalMoves.length];
		int[] boardHeights = s.getTop();
		int[][] allWidth = State.getpWidth();
		int[][][] pTop = State.getpTop();
		int[][][] pBottom = State.getpBottom();
		for (int i=0;i<legalMoves.length;i++){
			int pOrient = legalMoves[i][0];
			int pSlot = legalMoves[i][1];
			int pWidth = allWidth[nextPiece][pOrient];
			int[] temp = new int[pWidth];
			for (int j=0;j<pWidth;j++) {
				temp[j]=boardHeights[j+pSlot];
			}

			int maxBottom = getpBottom(nextPiece, pBottom, pOrient, pWidth);
			addBottomHalf(nextPiece, pBottom, pOrient, pWidth, temp, maxBottom);
			int tempMax = getTempMax(pWidth, temp);
			setAllTempMax(pWidth, temp, tempMax);
			addTopHalf(nextPiece, pTop, pOrient, pWidth, temp, maxBottom);

			int[] tempBoard = new int[boardHeights.length];
			for (int k=0;k<tempBoard.length;k++) {
				if (k>=pSlot && k<=(pSlot+pWidth-1)){
					tempBoard[k] = temp[k-pSlot];
				} else {
					tempBoard[k] = boardHeights[k];
				}
			}

			points[0][i] = sumForBumpiness(tempBoard);
			points[1][i] = getMaxSlope(tempBoard);
		}
		return points;
	}

	//sum of bumpiness
	private int sumForBumpiness(int[] tempBoard) {
		int temp = 0;
		for (int i=1;i<tempBoard.length;i++) {
			temp += Math.abs(tempBoard[i]-tempBoard[i-1]);
		}
		return temp;
	}

	//get the steepest slope (highest point to lowest point)
	private int getMaxSlope(int[] tempBoard) {
		int tempHigh = 0;
		int tempLow = 20;
		for (int i=0;i<tempBoard.length;i++) {
			if (tempBoard[i]>tempHigh) {
				tempHigh = tempBoard[i];
			}
			if (tempBoard[i]<tempLow) {
				tempLow = tempBoard[i];
			}
		}
		return Math.abs(tempLow-tempHigh);
	}

	//calculate the points from resulted holes in each affected column.
	//return the resulted holes after move. lower the better.
	private int[] calHolePoints(State s, int[][] legalMoves) {    
		int nextPiece = s.getNextPiece();
		int[] points = new int[legalMoves.length];
		int[] boardHeights = s.getTop();
		int[][] field = s.getField();
		int[][] allWidth = State.getpWidth();
		int[][][] pBottom = State.getpBottom();
		for (int i=0;i<legalMoves.length;i++){
			int pOrient = legalMoves[i][0];
			int pSlot = legalMoves[i][1];
			int pWidth = allWidth[nextPiece][pOrient];
			int[] temp = new int[pWidth];
			for (int j=0;j<pWidth;j++) {
				temp[j]=boardHeights[j+pSlot];
			}

			int maxBottom = getpBottom(nextPiece, pBottom, pOrient, pWidth);
			addBottomHalf(nextPiece, pBottom, pOrient, pWidth, temp, maxBottom);
			int tempMax = getTempMax(pWidth, temp);
			
			int holes = 0;
			for (int k=0;k<pWidth;k++){
				holes += Math.abs(temp[k]-tempMax);
			}
			
			//int[][] tempBoard = new int[20][pWidth];
			//for (int c=0;c<pWidth;c++) {
			//	for (int r=0;r<20;r++) {
			//		tempBoard[r][c] = field[r][c+pSlot];
			//	}
			//}
			
			//holes += countHoles(tempBoard);
			
			points[i] = holes;
		}
		return points;
	}

	//calculate the points from the height in each affected column.
	//return the increased in height after move. lower the better.
	//point[0] == aggregated height
	//point[1] == max height
	private int[][] calHeightPoints(State s, int[][] legalMoves) {
		int nextPiece = s.getNextPiece();
		int[][] points = new int[2][legalMoves.length];
		int[] boardHeights = s.getTop();
		int[][] allWidth = State.getpWidth();
		int[][][] pTop = State.getpTop();
		int[][][] pBottom = State.getpBottom();
		for (int i=0;i<legalMoves.length;i++){
			int pOrient = legalMoves[i][0];
			int pSlot = legalMoves[i][1];
			int pWidth = allWidth[nextPiece][pOrient];
			int[] temp = new int[pWidth];
			for (int j=0;j<pWidth;j++) {
				temp[j]=boardHeights[j+pSlot];
			}

			int maxBottom = getpBottom(nextPiece, pBottom, pOrient, pWidth);
			addBottomHalf(nextPiece, pBottom, pOrient, pWidth, temp, maxBottom);
			int tempMax = getTempMax(pWidth, temp);
			setAllTempMax(pWidth, temp, tempMax);
			addTopHalf(nextPiece, pTop, pOrient, pWidth, temp, maxBottom);

			int[] tempBoard = new int[boardHeights.length];
			for (int k=0;k<tempBoard.length;k++) {
				if (k>=pSlot && k<=(pSlot+pWidth-1)){
					tempBoard[k] = temp[k-pSlot];
				} else {
					tempBoard[k] = boardHeights[k];
				}
			}

			points[0][i] = sumForHeight(tempBoard);
			points[1][i] = getMaxHeight(tempBoard);
		}
		return points;
	}

	//sum of newHeight
	private int sumForHeight(int[] tempBoard) {
		int temp = 0;
		for (int i=0;i<tempBoard.length;i++) {
			temp += tempBoard[i];
		}
		return temp;
	}

	//get the highest height
	private int getMaxHeight(int[] tempBoard) {
		int temp = 0;
		for (int i=0;i<tempBoard.length;i++) {
			if(tempBoard[i]>=temp) {
				temp = tempBoard[i];
			}
		}
		return temp;
	}

	//add the top part of piece
	private void addTopHalf(int nextPiece, int[][][] pTop, int pOrient, int pWidth, int[] temp, int maxBottom) {
		for (int n=0;n<pWidth;n++) {
			temp[n]+=pTop[nextPiece][pOrient][n]-maxBottom;
		}
	}

	//set all temp as the max
	private void setAllTempMax(int pWidth, int[] temp, int tempMax) {
		for (int m=0; m<pWidth; m++){
			temp[m]=tempMax;
		}
	}

	//compare all temp heights
	private int getTempMax(int pWidth, int[] temp) {
		int tempMax = 0;
		for (int l=0;l<pWidth;l++) {
			if (temp[l]>tempMax){
				tempMax = temp[l];
			}
		}
		return tempMax;
	}

	//add the bottom part of piece
	private void addBottomHalf(int nextPiece, int[][][] pBottom, int pOrient, int pWidth, int[] temp, int maxBottom) {

		for (int a=0;a<pWidth;a++) {
			if (pBottom[nextPiece][pOrient][a]==0) {
				temp[a]+=maxBottom;
			}
		}
	}

	//check all pBottom of piece in orientation 
	private int getpBottom(int nextPiece, int[][][] pBottom, int pOrient, int pWidth) {
		int maxBottom= 0;
		for (int k=0;k<pWidth;k++) {
			maxBottom = Math.max(maxBottom, pBottom[nextPiece][pOrient][k]);
		}
		return maxBottom;
	}

	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}

}
