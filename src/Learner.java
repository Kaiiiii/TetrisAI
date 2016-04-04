import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Created by maianhvu on 5/4/16.
 */
public class Learner {

    protected static LearningMethod selectLearningMethod() {
        return new SimulatedAnnealing();
    }

    public static void main(String[] args) throws Exception {
        (new File("data")).mkdirs();
        LearningMethod method = selectLearningMethod();
        BufferedWriter writer = new BufferedWriter(new FileWriter("data/learn.txt"));

        while (true) {
            Heuristics heuristics = method.learn();
            double score = new Benchmarker(heuristics).benchmark(1000);
            if (score > 1000.0) {
                writer.write(heuristics.fullString());
                writer.newLine();
                writer.flush();
            }
        }
    }
}
