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
        final StateAnalyser analyser = new StateAnalyser(s);
        return IntStream.range(0, s.legalMoves().length).parallel()
                .mapToObj(analyser::analyse)
                .max((a1, a2) -> this._heuristics.calculate(a1).compareTo(this._heuristics.calculate(a2)))
                .get().getMoveIndex();
    }
}
