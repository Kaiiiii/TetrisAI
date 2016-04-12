import java.util.stream.IntStream;

public class PlayerSkeleton {

    private static final int DELAY_PLACE = 1;
    private static final Heuristics HEURISTICS_DEFAULT = new Heuristics(
            0.025447084296227995, // Height
            0.6576589572714789, // Cleared
            0.8390092702535551, // Holes
            0.1990402741152839 // Bumpiness
    );

    // implement this function to have a working system
    public int pickMove(State s, int[][] legalMoves) {
        StateAnalyser analyser = new StateAnalyser(s);
        AnalysisResult winningMove = IntStream.range(0, legalMoves.length).parallel()
                .mapToObj(analyser::analyse)
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
//        double result = new Benchmarker(HEURISTICS_DEFAULT).benchmark(1000);
//        System.out.println(result);
    }

}
