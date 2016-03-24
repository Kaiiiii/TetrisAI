package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.Game;
import logic.Player;

public abstract class VisualPlayer extends Application {

    private ViewController _controller;
    private Stage _primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("../ui/Tetris.fxml"));
        Parent root = loader.load();
        this._controller = loader.getController();
        Scene scene = new Scene(root);

        this._primaryStage = primaryStage;
        this._primaryStage.setScene(scene);
        this._primaryStage.setTitle("Tetris");
        this._primaryStage.setResizable(false);

        this._primaryStage.show();

        this.play();
    }

    public void play() throws Exception {
        Game game = new Game();
        this._controller.registerGame(game);
        this._controller.startGame();

        if (this.letHumanPlay()) {
            this._controller.letHumanPlay(this._primaryStage);
        } else {
            this._controller.letAiPlay(this.getAI(), this._primaryStage);
        }
    }

    protected abstract boolean letHumanPlay();
    protected abstract Player getAI();
}
