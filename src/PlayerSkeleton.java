import java.util.stream.IntStream;

public class PlayerSkeleton {

    private static final int DELAY_PLACE = 1;
    private static final Heuristics HEURISTICS_DEFAULT = new Heuristics(
            3.6015, 2.4525, 2.7295, 8.4978, 9.1914, 2.6200
    );

    // implement this function to have a working system
    public int pickMove(State s, int[][] legalMoves) {
        AnalysisResult winningMove = IntStream.range(0, legalMoves.length).parallel()
                .mapToObj(moveId -> new StateAnalyser(s, moveId))
                .map(StateAnalyser::analyse)
                .filter(move -> !move.isLosingMove())
                .max((a1, a2) -> HEURISTICS_DEFAULT.calculate(a1).compareTo(HEURISTICS_DEFAULT.calculate(a2)))
                .orElse(null);
        if (winningMove == null) return 0;
        return winningMove.getMoveIndex();
    }

    public static void main(String[] args) {
        State s = new State();
        new TFrame(s);
        PlayerSkeleton p = new PlayerSkeleton();
        while (!s.hasLost()) {
            s.makeMove(p.pickMove(s, s.legalMoves()));
            s.draw();
            s.drawNext(0, 0);
            try {
                Thread.sleep(DELAY_PLACE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("You have completed " + s.getRowsCleared() + " rows.");
    }

}
