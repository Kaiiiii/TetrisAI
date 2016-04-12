import java.util.stream.IntStream;

public class PlayerSkeleton {

    private static final int DELAY_PLACE = 10;
//    private static final Heuristics HEURISTICS_DEFAULT = new Heuristics(
////            -0.510066, 0.760666, -0.35663, -0.184483
//            -0.9635067495559927, 0.5900638947967912, -0.010675920224679562, -0.13982186073511282
//    );


    private static final Heuristics HEURISTICS_DEFAULT = new Heuristics(
          // AggregateHeight |    CompleteLines   |      HolesCount     |     Bumpiness      |     LandingHeight
            -0.9635067495559927, 0.5900638947967912, -0.010675920224679562, -0.13982186073511282, -0.500158825082766
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
