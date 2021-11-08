package view;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import network.NetworkSetupDialog;

/**
 * This class starts and runs the GUI.
 */
public class StrategoView extends Application {

    private Stage stage;

    /**
     * Start the GUI for game.
     */
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Stratego");

        stage.setScene(new MainMenu(this));
        stage.sizeToScene();
        stage.show();
    }

    /**
     * Starts the game when the start game button is clicked. Shows a window
     * allowing player to choose server/client/host name/port number options.
     */
    void startGame() {
        NetworkSetupDialog dialog = new NetworkSetupDialog();
        dialog.showAndWait().ifPresent(result -> {
            try {
                BoardScene scene = new BoardScene(!result.isServer(), result.isServer(), result.getHost(),
                        result.getPort());
                stage.setScene(scene);
                stage.setOnCloseRequest(event -> scene.onClosed());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        stage.sizeToScene();
        stage.show();
    }
}
