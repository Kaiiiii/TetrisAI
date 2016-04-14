import java.util.stream.IntStream;

/**
 * Created by maianhvu on 5/4/16.
 */
public class Benchmarker {

    private final Heuristics _heuristics;

    public Benchmarker(Heuristics heuristics) {
        this._heuristics = heuristics;
    }

    public Double benchmark(int times) {
        return IntStream.range(0, times).parallel()
                .mapToObj(index -> new State())
                .mapToInt(game -> {
                    while (!game.hasLost()) {
                        game.makeMove(this.pickMove(game));
                    }

                    return game.getRowsCleared();
                })
                .average().getAsDouble();
    }

    private int pickMove(State s) {
        AnalysisResult result = IntStream.range(0, s.legalMoves().length).parallel()
                .mapToObj(moveId -> new StateAnalyser(s, moveId))
                .map(StateAnalyser::analyse)
                .filter(move -> !move.isLosingMove())
                .max((a1, a2) -> this._heuristics.calculate(a1).compareTo(this._heuristics.calculate(a2)))
                .orElse(null);
        if (result == null) return 0;
        return result.getMoveIndex();
    }

    public static void main(String[] args) {
        Heuristics h = new Heuristics(
                5.500158825082766,
                3.4181268101392694,
                3.2178882868487753,
                9.348695305445199,
                10.899265427351652,
                3.3855972247263626
        );
        System.out.println(new Benchmarker(h).benchmark(10));
    }
}
