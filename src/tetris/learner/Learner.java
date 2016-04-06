package tetris.learner;

import java.util.Random;

import tetris.agents.*;
import tetris.model.*;

public class Learner {

	private static final int clock = 1000;
	private static final int numberOfHeuristic = 7;
	private static final int numberOfRuns = 5;
	private static Random randomizer = new Random();

	private double[] bestRatio = new double[numberOfHeuristic];
	private int bestScore = 0;

	public static void main(String[] args) {
		Learner learner = new Learner();
		learner.geneticAlgo();
	}

	private void geneticAlgo() {
		double[] ratio1 = new double[numberOfHeuristic];
		double[] ratio2 = new double[numberOfHeuristic];
		double[] ratio3 = new double[numberOfHeuristic];
		double[] ratio4 = new double[numberOfHeuristic];
		double[] ratio5 = new double[numberOfHeuristic];
		ratio1 = randomRatio();
		updateBest(ratio1);
		int count = clock;
		while (count>0) {
			boolean flag = false;
			ratio2 = randomRatio();
			flag = updateBest(ratio2);

			ratio3 = crossover(ratio1,ratio2);
			flag = updateBest(ratio3) || flag;

			ratio4 = crossover(ratio2,ratio1);
			flag = updateBest(ratio4) || flag;

			int tries = 5;
			while (!flag && tries>0) {
				ratio5 = mutation(bestRatio);
				flag = updateBest(ratio5);
				tries--;
			}

			System.arraycopy(bestRatio, 0, ratio1, 0, numberOfHeuristic);
			System.out.println("Counter: "+count+" --- Average Score: "+bestScore);
			printArray(bestRatio);
			if(bestScore > 1000000) {
				break;
			}
			count--;
		}
	}

	private double[] mutation(double[] ratio) {
		int[] slot = new int[numberOfHeuristic];
		for (int i=0;i<numberOfHeuristic;i++) {
			slot[i] = randomizer.nextInt(numberOfHeuristic);
		}
		double adder = randomizer.nextDouble();
		double[] temp = new double[numberOfHeuristic];
		System.arraycopy(ratio, 0, temp, 0, numberOfHeuristic);
		for (int i=0;i<numberOfHeuristic;i++) {
			temp[slot[i]] += adder;
		}		
		return temp;
	}

	private boolean updateBest(double[] ratio) {
		boolean flag = false;
		int tempScore;
		tempScore = getScore(ratio);
		if (tempScore > bestScore) {
			flag = true;
			bestScore = tempScore;
			System.arraycopy(ratio, 0, bestRatio, 0, numberOfHeuristic);
		}
		return flag;
	}

	private void printArray(double[] ratio) {
		for (int i=0;i<ratio.length;i++) {
			System.out.print(ratio[i]+" ");
		}
		System.out.println();
	}

	private static double[] crossover(double[] ratio1, double[] ratio2) {
		double[] temp = new double[numberOfHeuristic];
		for (int i=0;i<numberOfHeuristic;i++) {
			if (i<4) {
				temp[i] = ratio1[i];
			} else {
				temp[i] = ratio2[i];
			}
		}
		return temp;
	}

	private double[] randomRatio() {
		double[] ratio = new double[numberOfHeuristic];
		for (int i=0;i<numberOfHeuristic;i++) {
			ratio[i] = randomizer.nextDouble();
		}
		return ratio;
	}

	private int getScore(double[] ratio) {
		int score = 0;
		for (int i=0;i<numberOfRuns;i++) {
			State s = new State();
			PlayerSkeleton p = new PlayerSkeleton();
			p.setRatios(ratio);
			while(!s.hasLost()) {
				s.makeMove(p.pickMove(s,s.legalMoves()));
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			score += s.getRowsCleared();
		}
		return score/numberOfRuns;
	}
}
