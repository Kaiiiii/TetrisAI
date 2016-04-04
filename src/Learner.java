/**
 * Created by maianhvu on 5/4/16.
 */
public class Learner {

    protected static LearningMethod selectLearningMethod() {
        return new SimulatedAnnealing();
    }

    public static void main(String[] args) {
        LearningMethod method = selectLearningMethod();
        Heuristics heuristics = method.learn();
        System.out.println(heuristics.fullString());
    }
}
