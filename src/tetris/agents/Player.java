package tetris.agents;

import java.util.*;

import tetris.model.State;
import tetris.model.TFrame;

public class Player {

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

	//pieces ordering from State = O I L J T S Z
	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		int[] heightPoints = calHeightPoints(s, legalMoves);
		int[] holePoints = calHolePoints(s, legalMoves);
		int[] bumpinessPoints = calBumpinessPoints(s, legalMoves);
		int[] clearPoints = calClearPoints(s, legalMoves);

		int result = 0;
		double a = -0.510066;
		double b = -0.35663;
		double c = -0.184483;
		double d = 0.760666;
		for (int i=1; i<legalMoves.length; i++) {
			double resultPoint = a*(double)heightPoints[result] + b*(double)holePoints[result] + c*(double)bumpinessPoints[result] + d*clearPoints[result];
			double checkPoint = a*(double)heightPoints[i] + b*(double)holePoints[i] + c*(double)bumpinessPoints[i] + d*clearPoints[i];
			if (resultPoint<=checkPoint){
				result = i;
			}
		}

		return result;
	}

	//calculate the points from resulted row cleared in each affected column.
	//return the resulted rows cleared after move. higher the better.
	public int[] calClearPoints(State s, int[][] legalMoves) {    
		int nextPiece = s.getNextPiece();
		int[] points = new int[legalMoves.length];
		int[] boardHeights = s.getTop();
		int[][] allWidth = s.getpWidth();
		int[][] field = s.getField();
		int[][][] pTop = s.getpTop();
		int[][][] pBottom = s.getpBottom();
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

			points[i] = count;
		}

		return points;
	}

	//calculate the points from resulted holes in each affected column.
	//return the resulted holes after move. lower the better.
	public int[] calBumpinessPoints(State s, int[][] legalMoves) {    
		int nextPiece = s.getNextPiece();
		int[] points = new int[legalMoves.length];
		int[] boardHeights = s.getTop();
		int[][] allWidth = s.getpWidth();
		int[][][] pTop = s.getpTop();
		int[][][] pBottom = s.getpBottom();
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

			points[i] = sumForBumpiness(tempBoard);
		}
		return points;
	}

	//sum of bumpiness
	public int sumForBumpiness(int[] tempBoard) {
		int bumpiness = 0;
		for (int i=1;i<tempBoard.length;i++) {
			bumpiness += Math.abs(tempBoard[i]-tempBoard[i-1]);
		}
		return bumpiness;
	}

	//calculate the points from resulted holes in each affected column.
	//return the resulted holes after move. lower the better.
	public int[] calHolePoints(State s, int[][] legalMoves) {    
		int nextPiece = s.getNextPiece();
		int[] points = new int[legalMoves.length];
		int[] boardHeights = s.getTop();
		int[][] allWidth = s.getpWidth();
		int[][][] pTop = s.getpTop();
		int[][][] pBottom = s.getpBottom();
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

			for (int k=0;k<pWidth;k++){
				points[i] += Math.abs(temp[k]-tempMax);
			}
		}
		return points;
	}

	//calculate the points from the height in each affected column.
	//return the increased in height after move. lower the better.
	public int[] calHeightPoints(State s, int[][] legalMoves) {
		int nextPiece = s.getNextPiece();
		int[] points = new int[legalMoves.length];
		int[] boardHeights = s.getTop();
		int[][] allWidth = s.getpWidth();
		int[][][] pTop = s.getpTop();
		int[][][] pBottom = s.getpBottom();
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

			points[i] = sumForHeight(tempBoard);
		}
		return points;
	}

	//sum of newHeight
	public int sumForHeight(int[] tempBoard) {
		int newHeight = 0;
		for (int i=0;i<tempBoard.length;i++) {
			newHeight += tempBoard[i];
		}
		return newHeight;
	}

	//add the top part of piece
	public void addTopHalf(int nextPiece, int[][][] pTop, int pOrient, int pWidth, int[] temp, int maxBottom) {
		for (int n=0;n<pWidth;n++) {
			temp[n]+=pTop[nextPiece][pOrient][n]-maxBottom;
		}
	}

	//set all temp as the max
	public void setAllTempMax(int pWidth, int[] temp, int tempMax) {
		for (int m=0; m<pWidth; m++){
			temp[m]=tempMax;
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

	//add the bottom part of piece
	public void addBottomHalf(int nextPiece, int[][][] pBottom, int pOrient, int pWidth, int[] temp, int maxBottom) {

		for (int a=0;a<pWidth;a++) {
			if (pBottom[nextPiece][pOrient][a]==0) {
				temp[a]+=maxBottom;
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

	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		Player p = new Player();
		while(!s.hasLost()) {

			try {
				s.makeMove(p.pickMove(s,s.legalMoves()));
			} catch (Exception e) {
				e.printStackTrace();
			}

			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}

}
