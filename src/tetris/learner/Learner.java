package tetris.learner;

import java.util.Random;

import tetris.agents.*;
import tetris.model.*;

public class Learner {

	private static final int CLOCK = 1000;
	private static final int NUMBER_OF_HEURISTICS = 7;
	private static final int MINIMUM_MUTATION = 3;
	private static final int NUMBER_OF_RUNS = 20;
	private static final int MAX_REPEATS = 5;
	private static Random randomizer = new Random();

	private double[] bestRatio = new double[NUMBER_OF_HEURISTICS];
	private double[] prevRatio = {0.5811005086815347, 0.6732932788752304, 0.1593129551063973, 1.1324943440833426, 0.27685699434549205, -0.015314259060849267, 0.11397418910816315};
	private int bestScore = 0;
	private int logger = 0;

	public static void main(String[] args) {
		Learner learner = new Learner();
		learner.geneticAlgo();
	}

	private void geneticAlgo() {
		double[] ratio1 = new double[NUMBER_OF_HEURISTICS];
		double[] ratio2 = new double[NUMBER_OF_HEURISTICS];
		double[] ratio3 = new double[NUMBER_OF_HEURISTICS];
		double[] ratio4 = new double[NUMBER_OF_HEURISTICS];
		double[] ratio5 = new double[NUMBER_OF_HEURISTICS];
		if (prevRatio == null) {
			ratio1 = randomRatio();
		} else {
			System.arraycopy(prevRatio, 0, ratio1, 0, NUMBER_OF_HEURISTICS);
		}
		ratio1 = randomRatio();
		updateBest(ratio1);
		int count = CLOCK;
		int repeated = 0;
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
				ratio5 = mutation(bestRatio, repeated);
				flag = updateBest(ratio5);
				tries--;
			}

			System.arraycopy(bestRatio, 0, ratio1, 0, NUMBER_OF_HEURISTICS);
			System.out.println("Counter: "+count+" --- Average Score: "+bestScore);
			printArray(bestRatio);
			if (bestScore != logger) {
				logger = bestScore;
				repeated = 0;
			} else {
				repeated++;
			}

			if (repeated==(MAX_REPEATS-1)) {
				//re-evaluation if ratio is stagnant for 10 cycle
				bestScore = getScore(bestRatio);
				System.out.println("Re-Evaluated Score: " + bestScore);
				repeated = 0;
			}

			if(bestScore > 1000000) {
				break;
			}
			count--;
		}
	}

	private double[] mutation(double[] ratio, int type) {
		int[] slot = new int[randomizer.nextInt(NUMBER_OF_HEURISTICS-MINIMUM_MUTATION)+MINIMUM_MUTATION];
		for (int i=0;i<slot.length;i++) {
			slot[i] = randomizer.nextInt(NUMBER_OF_HEURISTICS-MINIMUM_MUTATION)+MINIMUM_MUTATION;
		}
		double mutator = randomizer.nextDouble();
		double[] temp = new double[NUMBER_OF_HEURISTICS];
		System.arraycopy(ratio, 0, temp, 0, NUMBER_OF_HEURISTICS);
		for (int i=0;i<slot.length;i++) {
			if (type==5) {
				temp[slot[i]] /= mutator;
			} else if (type == 4) {
				temp[slot[i]] *= mutator;
			} else if (type == 3) {
				temp[slot[i]] -= mutator;
			} else {
				temp[slot[i]] += mutator;
			}
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
			System.arraycopy(ratio, 0, bestRatio, 0, NUMBER_OF_HEURISTICS);
		}
		return flag;
	}

	private void printArray(double[] ratio) {
		for (int i=0;i<ratio.length;i++) {
			System.out.print(ratio[i]+", ");
		}
		System.out.println();
	}

	private static double[] crossover(double[] ratio1, double[] ratio2) {
		double[] temp = new double[NUMBER_OF_HEURISTICS];
		for (int i=0;i<NUMBER_OF_HEURISTICS;i++) {
			if (i<4) {
				temp[i] = ratio1[i];
			} else {
				temp[i] = ratio2[i];
			}
		}
		return temp;
	}

	private double[] randomRatio() {
		double[] ratio = new double[NUMBER_OF_HEURISTICS];
		for (int i=0;i<NUMBER_OF_HEURISTICS;i++) {
			ratio[i] = randomizer.nextDouble();
		}
		return ratio;
	}

	private int getScore(double[] ratio) {
		int score = 0;
		for (int i=0;i<NUMBER_OF_RUNS;i++) {
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
		return score/NUMBER_OF_RUNS;
	}
}
