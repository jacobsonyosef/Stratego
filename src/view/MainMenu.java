package view;

import java.io.File;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * This class represents the main menu of the game, and has buttons to start a
 * game, view the rules, or close the window.
 * 
 * @author Emily Pencek
 *
 */
public class MainMenu extends Scene {

    private BorderPane pane;

    /**
     * Default constructor.
     * 
     * @param view The view StrategoView object that uses the main menu.
     */
    public MainMenu(StrategoView view) {
        super(new BorderPane());
        pane = (BorderPane) getRoot();

        Label title = new Label("Main Menu");
        title.setFont(new Font(32));
        title.setPadding(new Insets(0, 80, 0, 80));
        pane.setTop(title);

        Button newGame = new Button("Start Game");
        newGame.setPrefSize(100, 30);
        newGame.setOnAction(e -> {
            view.startGame();
        });

        Button rules = new Button("Rules");
        rules.setPrefSize(100, 30);
        rules.setOnAction(e -> {
            // TODO: Create rule page.
            new RulesPage();
        });

        Button exit = new Button("Quit");
        exit.setPrefSize(100, 30);
        exit.setOnAction(e -> {
            Platform.exit();
        });

        VBox v = new VBox(newGame, rules, exit);
        v.setPadding(new Insets(30, 115, 30, 115));
        v.setSpacing(3);
        pane.setCenter(v);

    }

    /**
     * This class is a simple window that displays an image containing the official
     * Stratego rules.
     * 
     * @author Yosef Jacobson
     *
     */
    class RulesPage extends Stage {

        public RulesPage() {
            this.setTitle("Rules");
            initModality(Modality.NONE);

            BorderPane window = new BorderPane();
            ImageView rules = new ImageView();
            rules.setImage(new Image("assets" + File.separator + "StrategoRules.jpg"));
            window.setCenter(rules);
            this.setScene(new Scene(window));
            this.show();
        }
    }
}
