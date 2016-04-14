import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Created by maianhvu on 5/4/16.
 */
public class Learner {

    protected static LearningMethod selectLearningMethod() {
        return new ParticleSwarmOptimisation();
    }

    public static void main(String[] args) throws Exception {
        (new File("data")).mkdirs();
        LearningMethod method = selectLearningMethod();
        BufferedWriter writer = new BufferedWriter(new FileWriter("data/pso.txt"));

        while (true) {
            Heuristics heuristics = method.learn();
            if (heuristics == null) break;
            double score = new Benchmarker(heuristics).benchmark(10);
            writer.write(heuristics.fullString());
            writer.newLine();
            writer.write(Double.toString(score));
            writer.newLine();
            writer.flush();
            System.out.printf("Agent [%s] earned %.0f points!",
                    heuristics.fullString(),
                    score);
        }
    }
}
