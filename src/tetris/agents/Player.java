package tetris.agents;

import java.util.*;

import tetris.model.State;
import tetris.model.TFrame;

public class Player {

    //implement this function to have a working system
    public int pickMove(State s, int[][] legalMoves) {
	//hPoints - points allocated to height for each legalMoves
	int[] hPoints = calHeightPoints(s, legalMoves);
	int result = 0;
	for (int i=1; i<legalMoves.length; i++) {
	    if (hPoints[result]>hPoints[i]){
		result = i;
	    }
	}
	int nextPiece = s.getNextPiece();
	String[] piece = {"O", "I", "L", "J", "T", "S", "Z"};
	//	    System.out.println("Piece: "+piece[nextPiece]+" Results: "+result+" "+legalMoves[result][0]+" "+legalMoves[result][1]);

	return result;
    }

    //calculate the points from the height in each affected column.
    //return the increased in height after move. lower the better.
    //pieces ordering from State = O I L J T S Z
    public int[] calHeightPoints(State s, int[][] legalMoves) {
	int nextPiece = s.getNextPiece();
	int[] points = new int[legalMoves.length];
	int[] boardHeights = s.getTop();
	int[][] allWidth = s.getpWidth();
	int[][][] pTop = s.getpTop();
	int[][][] pBottom = s.getpBottom();
	for (int i=0;i<legalMoves.length;i++){
	    int newHeight = 0;
	    int pOrient = legalMoves[i][0];
	    int pSlot = legalMoves[i][1];
	    int pWidth = allWidth[nextPiece][pOrient];
	    int[] temp = new int[pWidth];
	    for (int j=0;j<pWidth;j++) {
		temp[j]=boardHeights[j+pSlot];
	    }

	    //check all pBottom of piece in orientation
	    int maxBottom= 0;
	    for (int k=0;k<pWidth;k++) {
		maxBottom = Math.max(maxBottom, pBottom[nextPiece][pOrient][k]);
	    }
	    //add the bottom part of piece
	    for (int a=0;a<pWidth;a++) {
		if (pBottom[nextPiece][pOrient][a]==0) {
		    temp[a]+=maxBottom;
		}
	    }
	    //compare all temp heights
	    int tempMax = 0;
	    for (int l=0;l<pWidth;l++) {
		if (temp[l]>tempMax){
		    tempMax = temp[l];
		}
	    }
	    //set all temp as the max
	    for (int m=0; m<pWidth; m++){
		temp[m]=tempMax;
	    }
	    //add the top part of piece
	    for (int n=0;n<pWidth;n++) {
		temp[n]+=pTop[nextPiece][pOrient][n]-maxBottom;
	    }
	    //sum of temp = newHeight
	    for (int o=0;o<pWidth;o++) {
		newHeight+=temp[o];
	    }
	    points[i] = newHeight;
	}
	return points;
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
		Thread.sleep(300);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
	System.out.println("You have completed "+s.getRowsCleared()+" rows.");
    }

}
