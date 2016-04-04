import java.util.stream.IntStream;

public class PlayerSkeleton {

    // implement this function to have a working system
    public int pickMove(State s, int[][] legalMoves) {
        return IntStream.range(0, legalMoves.length)
                .parallel()
                .mapToObj(move -> new FieldAnalyst().analyse(s, move))
                .sorted()
                .max(AnalysisResult::compareTo)
                .get()
                .getMoveIndex();
    }

    public static void main(String[] args) {
        State s = new State();
        new TFrame(s);
        PlayerSkeleton p = new PlayerSkeleton();
        while (!s.hasLost()) {
            s.makeMove(p.pickMove(s, s.legalMoves()));
//            s.draw();
//            s.drawNext(0, 0);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("You have completed " + s.getRowsCleared() + " rows.");
    }

}
