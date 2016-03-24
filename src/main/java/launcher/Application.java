package launcher;

import ai.UniversalPlayer;
import logic.Player;
import ui.VisualPlayer;

/**
 * Created by maianhvu on 24/03/2016.
 */
public class Application extends VisualPlayer {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected boolean letHumanPlay() {
        return false;
    }

    @Override
    protected Player getAI() {
        return new UniversalPlayer();
    }

    @Override
    protected int getAiDelay() {
        return 10;
    }
}
