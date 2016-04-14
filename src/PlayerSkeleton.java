import java.util.stream.IntStream;

public class PlayerSkeleton {

    private static final int DELAY_PLACE = 1;
    private static final Heuristics HEURISTICS_DEFAULT = new Heuristics(
            5.500158825082766,
            3.4181268101392694,
            3.2178882868487753,
            9.348695305445199,
            10.899265427351652,
            3.3855972247263626
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
