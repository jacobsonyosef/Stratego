package view;

import java.io.File;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import model.PieceType;

/**
 * Class that deals with the Animation of the Pieces. Handles battle, bomb, tie,
 * and winner animations.
 * 
 */
public class PieceAnimation extends Pane {

    // String for the images.
    private String winnerImageStr;
    private String loserImageStr;

    // ImageViews of common images.
    private ImageView winnerImage;
    private ImageView loserImage;
    private ImageView explosion;

    // Common Tranisitions/Timelines.
    private FadeTransition fade;
    private Timeline timeline;

    /**
     * Constructor.
     */
    public PieceAnimation() {
    }

    /**
     * Takes in two pieces, get their images and creates an animation of the winner
     * battling the loser. The winner grows and hits the loser. Loser shakes and
     * falls down. Animation ends. If the winner is a bomb, then play the bomb
     * animation instead.
     * 
     * @param winner Piece that won the battle.
     * @param loser  Piece that lost the battle.
     */
    public void battle(Piece winner, Piece loser) {

        // Starts up animation.
        reset();
        begin(winner, loser);

        // If winner is a bomb, play bomb animation. Else continue.
        if (winner.getPieceType() == PieceType.BOMB) {
            bombExplode();
        } else {

            setImages();

            // Event to be played after the descent. Enlarge winner.
            EventHandler<ActionEvent> afterDescent = new EventHandler<ActionEvent>() {

                private Timeline enlarge;

                @Override
                public void handle(ActionEvent t) {

                    enlarge = new Timeline();
                    enlarge.setCycleCount(1);
                    enlarge.setAutoReverse(false);

                    // Event to be played after the enlarge. Fight scene.
                    EventHandler<ActionEvent> afterEnlarge = new EventHandler<ActionEvent>() {

                        private Timeline fight;

                        @Override
                        public void handle(ActionEvent t) {

                            fight = new Timeline();
                            fight.setCycleCount(1);
                            fight.setAutoReverse(false);

                            // Event to be played after the fight. Loser dies.
                            EventHandler<ActionEvent> afterFight = new EventHandler<ActionEvent>() {

                                Timeline die;

                                @Override
                                public void handle(ActionEvent t) {

                                    die = new Timeline();
                                    die.setCycleCount(1);
                                    die.setAutoReverse(false);

                                    // Event to be played after the animation. Set visibility to false.
                                    EventHandler<ActionEvent> closeAnimation = new EventHandler<ActionEvent>() {

                                        @Override
                                        public void handle(ActionEvent t) {

                                            setVisible(false);

                                        }

                                    };

                                    // Loser shakes and falls off screen.
                                    die.getKeyFrames()
                                            .addAll(new KeyFrame(Duration.millis(100),
                                                    new KeyValue(loserImage.rotateProperty(), 25)),
                                                    new KeyFrame(Duration.millis(300),
                                                            new KeyValue(loserImage.rotateProperty(), -25)),
                                                    new KeyFrame(Duration.millis(400),
                                                            new KeyValue(loserImage.rotateProperty(), 25)),
                                                    new KeyFrame(Duration.millis(700),
                                                            new KeyValue(loserImage.rotateProperty(), -25)),
                                                    new KeyFrame(Duration.millis(800),
                                                            new KeyValue(loserImage.rotateProperty(), 25)),
                                                    new KeyFrame(Duration.millis(1000),
                                                            new KeyValue(loserImage.rotateProperty(), -25)),
                                                    new KeyFrame(Duration.millis(1500), closeAnimation,
                                                            new KeyValue(loserImage.yProperty(), 900)));

                                    die.play();

                                }

                            };

                            // Winner goes to loser and 'attacks'.
                            fight.getKeyFrames().addAll(
                                    new KeyFrame(Duration.millis(400), new KeyValue(winnerImage.xProperty(), 400)),
                                    new KeyFrame(Duration.millis(600), new KeyValue(winnerImage.xProperty(), 100)),
                                    new KeyFrame(Duration.millis(400), afterFight,
                                            new KeyValue(winnerImage.scaleXProperty(), 1),
                                            new KeyValue(winnerImage.scaleYProperty(), 1)));

                            fight.play();

                        }

                    };

                    // Enlarge the winner image.
                    try {
                        enlarge.getKeyFrames()
                                .add(new KeyFrame(Duration.millis(500), afterEnlarge,
                                        new KeyValue(winnerImage.scaleXProperty(), 1.5),
                                        new KeyValue(winnerImage.scaleYProperty(), 1.5)));
                    } catch (NullPointerException e) {
                    }

                    enlarge.play();

                }
            };

            // Descend the images.
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(2000), afterDescent,
                    new KeyValue(winnerImage.xProperty(), 100), new KeyValue(loserImage.xProperty(), 400)));

            // Add images to the Pane.
            getChildren().addAll(winnerImage, loserImage);

        }

    }

    /**
     * Plays when two pieces tie. Both pieces enlarge and attack each other.
     * Although they are labeled winner and loser, there is no winner and loser. It
     * is used to determine difference of pieces. Animation ends.
     * 
     * @param winner Piece that won the battle.
     * @param loser  Piece that lost the battle.
     */
    public void tie(Piece winner, Piece loser) {

        // Starts up animation,
        reset();
        begin(winner, loser);

        setImages();

        // Event that plays after the descent. Enlarge images.
        EventHandler<ActionEvent> afterDescent = new EventHandler<ActionEvent>() {

            private Timeline enlarge;

            @Override
            public void handle(ActionEvent t) {

                enlarge = new Timeline();
                enlarge.setCycleCount(1);
                enlarge.setAutoReverse(false);

                // Event that plays after the images enlarge. Fight.
                EventHandler<ActionEvent> afterEnlarge = new EventHandler<ActionEvent>() {

                    private Timeline fight;

                    @Override
                    public void handle(ActionEvent t) {

                        fight = new Timeline();
                        fight.setCycleCount(1);
                        fight.setAutoReverse(false);

                        // Event that plays after animation is done. Set visibility to false.
                        EventHandler<ActionEvent> closeAnimation = new EventHandler<ActionEvent>() {

                            @Override
                            public void handle(ActionEvent t) {

                                setVisible(false);

                            }

                        };

                        // Fight animation for winner image.
                        fight.getKeyFrames()
                                .addAll(new KeyFrame(Duration.millis(400), new KeyValue(winnerImage.xProperty(), 250)),
                                        new KeyFrame(Duration.millis(600), new KeyValue(winnerImage.xProperty(), 100)),
                                        new KeyFrame(Duration.millis(1500),
                                                new KeyValue(winnerImage.scaleXProperty(), 1),
                                                new KeyValue(winnerImage.scaleYProperty(), 1)));

                        // Fight animation for loser image.
                        fight.getKeyFrames().addAll(
                                new KeyFrame(Duration.millis(400), new KeyValue(loserImage.xProperty(), 250)),
                                new KeyFrame(Duration.millis(600), new KeyValue(loserImage.xProperty(), 400)),
                                new KeyFrame(Duration.millis(1500), closeAnimation,
                                        new KeyValue(loserImage.scaleXProperty(), 1),
                                        new KeyValue(loserImage.scaleYProperty(), 1)));

                        fight.play();

                    }

                };

                // Enlarge both images.
                enlarge.getKeyFrames().addAll(
                        new KeyFrame(Duration.millis(500), new KeyValue(winnerImage.scaleXProperty(), 1.5),
                                new KeyValue(winnerImage.scaleYProperty(), 1.5)),
                        new KeyFrame(Duration.millis(500), afterEnlarge, new KeyValue(loserImage.scaleXProperty(), 1.5),
                                new KeyValue(loserImage.scaleYProperty(), 1.5)));

                enlarge.play();

            }

        };

        // Both images come onto screen.
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(2000), afterDescent,
                new KeyValue(winnerImage.xProperty(), 100), new KeyValue(loserImage.xProperty(), 400)));

        // Add images to the Pane.
        getChildren().addAll(winnerImage, loserImage);

    }

    /**
     * Plays when the game is over. The color of the winner is faded on and a
     * message enlarges onto screen.
     * 
     * @param team Team that won the game.
     */
    public void winner(boolean team) {

        reset();

        Rectangle win;

        // Color fade.
        if (team == Piece.BLUE_TEAM) {
            win = new Rectangle(600, 600, Color.BLUE);
        } else {
            win = new Rectangle(600, 600, Color.RED);
        }

        fade = new FadeTransition(Duration.millis(1500), win);
        fade.setFromValue(0);
        fade.setToValue(1);

        // Winner label.
        Label flag = new Label((team == Piece.RED_TEAM ? "Red " : "Blue ") + " wins!");
        flag.setFont(new Font(50));
        flag.setLayoutX(200);
        flag.setLayoutY(250);

        timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);

        // Event to close the animation. Set visibility to false.
        EventHandler<ActionEvent> closeAnimation = new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {

                setVisible(false);

            }

        };

        // Enlarges the label.
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(4000), closeAnimation,
                new KeyValue(flag.scaleXProperty(), 2), new KeyValue(flag.scaleYProperty(), 2)));

        // Adds the background and label to the Pane.
        getChildren().addAll(win, flag);
    }

    /**
     * Plays the animation. Plays the fade and the timeline.
     */
    public void playAnimation() {
        fade.play();
        timeline.play();
    }

    /**
     * Plays the bomb explosion animation. Loser image comes onto screen and then
     * bomb gif plays on top. Animation ends.
     */
    private void bombExplode() {

        // Sets the ImageView of the explosion gif.
        explosion = new ImageView(
                new Image(File.separator + "assets" + File.separator + "explosion.gif", 131, 162, false, false));
        explosion.setX(240);
        explosion.setY(250);
        explosion.setVisible(false);

        // Sets the ImageView of the loser image.
        loserImage = new ImageView(new Image(loserImageStr, 100, 100, false, false));
        loserImage.setX(-200);
        loserImage.setY(300);

        // Event that plays after the loser images arrives on screen. Gif is visible.
        EventHandler<ActionEvent> afterArrival = new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                Timeline gif = new Timeline();
                gif.setCycleCount(1);
                gif.setAutoReverse(false);

                // Event that plays after the animation. Sets the visibility to false.
                EventHandler<ActionEvent> closeAnimation = new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent t) {

                        setVisible(false);

                    }

                };

                // Makes explosion visible.
                explosion.setVisible(true);

                gif.getKeyFrames().addAll(new KeyFrame(Duration.millis(1111), closeAnimation,
                        new KeyValue(loserImage.opacityProperty(), 0)));

                gif.play();

            }

        };

        timeline.getKeyFrames()
                .add(new KeyFrame(Duration.millis(2000), afterArrival, new KeyValue(loserImage.xProperty(), 250)));

        // Adds the images to the Pane.
        getChildren().addAll(loserImage, explosion);

    }

    /**
     * Sets all the globals to null and removes all the children from the Pane.
     * Ready for a new animation.
     */
    private void reset() {

        timeline = null;
        fade = null;
        winnerImageStr = null;
        loserImageStr = null;
        winnerImage = null;
        loserImage = null;

        getChildren().removeAll(getChildren());

    }

    /**
     * Creates the black fade, creates the timeline, and gets the image strings.
     * 
     * @param winner Piece that won the battle.
     * @param loser  Piece that lost the battle.
     */
    private void begin(Piece winner, Piece loser) {

        // Black fade.
        Rectangle black = new Rectangle(600, 600, Color.BLACK);
        fade = new FadeTransition(Duration.millis(1500), black);
        fade.setFromValue(0);
        fade.setToValue(0.8);
        getChildren().add(black);

        timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);

        if (winner != null) {
            winnerImageStr = File.separator + "assets" + File.separator + winner.getPieceType().name() + ".png";
        }

        loserImageStr = File.separator + "assets" + File.separator + loser.getPieceType().name() + ".png";

    }

    /**
     * Sets the images with their image strings.
     */
    private void setImages() {

        winnerImage = new ImageView(new Image(winnerImageStr, 100, 100, false, false));
        winnerImage.setX(-400);
        winnerImage.setY(250);

        loserImage = new ImageView(new Image(loserImageStr, 100, 100, false, false));
        loserImage.setX(900);
        loserImage.setY(250);

    }

}
