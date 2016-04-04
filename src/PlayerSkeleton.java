import java.util.stream.IntStream;

public class PlayerSkeleton {

    private static final int DELAY_PLACE = 10;
    private static final Heuristics HEURISTICS_DEFAULT = new Heuristics(
            -.510066,
            .760666,
            -.35663,
            -.184483
    );

    // implement this function to have a working system
    public int pickMove(State s, int[][] legalMoves) {
        StateAnalyser analyser = new StateAnalyser(s);
        return IntStream.range(0, legalMoves.length).parallel()
                .mapToObj(analyser::analyse)
                .max((a1, a2) -> HEURISTICS_DEFAULT.calculate(a1).compareTo(HEURISTICS_DEFAULT.calculate(a2)))
                .get()
                .getMoveIndex();
    }

    public static void main(String[] args) {
//        State s = new State();
//        new TFrame(s);
//        PlayerSkeleton p = new PlayerSkeleton();
//        while (!s.hasLost()) {
//            s.makeMove(p.pickMove(s, s.legalMoves()));
//            s.draw();
//            s.drawNext(0, 0);
//            try {
//                Thread.sleep(DELAY_PLACE);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        System.out.println("You have completed " + s.getRowsCleared() + " rows.");
        double result = new Benchmarker(HEURISTICS_DEFAULT).benchmark(1000);
        System.out.println(result);
    }

}
