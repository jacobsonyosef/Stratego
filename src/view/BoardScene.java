package view;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import controller.StrategoController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.Loc;
import model.StrategoModel;
import network.StrategoMessage;
import network.StrategoPieceBattle;
import network.StrategoPieceUpdate;

/**
 * This class creates the entire board and is controlled by the controller for
 * the game.
 */
public class BoardScene extends Scene implements Observer {

    public static final double BORDER_SIZE = 1.2;
    public static final double TILE_SIZE = 60;

    private StrategoController controller;

    private BorderPane pane;
    private GridPane grid;
    private StackPane stack;
    private PieceAnimation animation;

    private HBox bottom;
    private PieceBin bin;
    private VBox bottomButtons;
    private PieceAutoPlace autoPlace;

    private boolean inverted;
    private BoardCell[][] cells;

    private BoardCell selected;
    private boolean fromBin;
    private Set<Loc> moves;
    private ImageView selection = new ImageView(
            new Image(File.separator + "assets" + File.separator + "selected.gif", TILE_SIZE, TILE_SIZE, false, true));

    // Timer
    private Label timerLabel;

    private boolean ourTurn;

    // ============== Constructors ===================================
    /**
     * Main constructor.
     *
     * @param team   False for red team, true for blue team.
     * @param server True if player is server, false if player is client.
     * @param host   Host name.
     * @param port   Port number.
     * @throws IOException If IO error.
     */
    public BoardScene(boolean team, boolean server, String host, int port) throws IOException {
        super(new StackPane());
        this.inverted = team;
        stack = (StackPane) getRoot();
        pane = new BorderPane();
        pane.setBackground(new Background(new BackgroundFill(Color.GREY, null, null)));
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        pane.setCenter(grid);

        controller = new StrategoController(team, server, host, port);
        setOurTurn(false);
        controller.getModel().addObserver(this);

        bin = new PieceBin(this, team);
        autoPlace = new PieceAutoPlace(bin, controller);
        bottomButtons = new VBox(autoPlace);
        bottom = new HBox(bin, bottomButtons);
        bottom.setSpacing(10);
        BorderPane.setMargin(bottom, new Insets(10));
        pane.setBottom(bottom);

        // TODO: FOR TESTING ONLY
        if (true) {
            Button testMoves = new Button("Auto Win");
            bottomButtons.getChildren().add(testMoves);
            testMoves.setOnAction(event -> {
                controller
                        .sendMessage(new StrategoMessage(StrategoMessage.MessageType.GAME_OVER, controller.getTeam()));
                onGameOver(controller.getTeam());
            });
        }
        initCells();

        animation = new PieceAnimation();
        stack.getChildren().addAll(pane, animation);
        animation.setVisible(false);
    }

    /**
     * Initialize the board cells with either grass or water and allow action
     * events.
     */
    private void initCells() {
        cells = new BoardCell[StrategoModel.ROWS][StrategoModel.COLS];
        for (int row = 0; row < StrategoModel.ROWS; row++) {
            for (int col = 0; col < StrategoModel.COLS; col++) {
                if (row == 0) {
                    grid.getColumnConstraints().add(new ColumnConstraints(BoardScene.TILE_SIZE));
                }
                if (col == 0) {
                    grid.getRowConstraints().add(new RowConstraints(BoardScene.TILE_SIZE));
                }
                BoardCell cell = new BoardCell(BORDER_SIZE, TILE_SIZE);

                String imageName = "/assets/grass.png";
                if (controller.isLake(new Loc(row, col))) {
                    imageName = "/assets/water.jfif";
                }
                cell.setImage(imageName);

                cells[row][col] = cell;
                grid.add(cell, col, row);

                cell.setOnMouseClicked(event -> {
                    if (!controller.isTurn())
                        return;
                    Loc to = getLocation(cell);
                    // If a valid move was clicked, make the move
                    if (moveSelected(to))
                        return;
                    selectPiece(to, cell);
                });

                cell.setOnDragDetected(event -> {
                    if (!controller.isTurn())
                        return;
                    Loc to = getLocation(cell);
                    selectPiece(to, cell);
                    startFullDrag();
                });

                cell.setOnMouseDragReleased(event -> {
                    if (!controller.isTurn())
                        return;
                    Loc to = getLocation(cell);
                    moveSelected(to);
                });
            }
        }
    }

    /**
     * Moves selected piece to a chosen location.
     * 
     * @param to The chosen location to the move the selected piece to.
     * @return True if selected a cell with a piece and valid destination location.
     */
    private boolean moveSelected(Loc to) {
        if (selected != null && moves.contains(to)) {
            if (fromBin) {
                Piece piece = bin.takePiece(selected, controller.getTeam());
                controller.placePiece(piece, to);
            } else {
                Loc from = getLocation(selected);
                controller.movePiece(from, to);
            }
            selectPiece(null, null);
            return true;
        }
        selectPiece(null, null);
        return false;
    }

    /**
     * Selects the piece at the specified location and cell.
     * 
     * @param loc  Specified location.
     * @param cell Specified cell.
     */
    public void selectPiece(Loc loc, BoardCell cell) {
        if (cell != null) {
            if (selected != null)
                selectPiece(null, null);
            // Select piece
            fromBin = loc == null;
            if (!fromBin && !controller.hasPiece(loc))
                return;
            // Only allowed to select pieces on your own team
            if (cell.getPiece().getTeam() != controller.getTeam())
                return;
            selected = cell;
            cell.setHighlight(selection);
            moves = controller.getMoves(loc, fromBin);
            for (Loc move : moves) {
                if (move.equals(loc))
                    continue;
                getCell(move).setHighlight(new Circle((TILE_SIZE / 4), Color.gray(0, 0.5)));
            }
        } else {
            // Unselect piece
            if (moves != null) {
                for (Loc move : moves) {
                    getCell(move).setHighlight(null);
                }
            }
            if (selected != null) {
                selected.setHighlight(null);
                if (fromBin)
                    bin.onDeselect(selected);
            }
            selected = null;
            moves = null;
        }
    }

    /**
     * Sets our turn and disables our pane if true.
     * 
     * @param ourTurn True if this turn is our turn.
     */
    private void setOurTurn(boolean ourTurn) {
        this.ourTurn = ourTurn;
        pane.setDisable(!ourTurn);
    }

    /**
     * Flip a location to the opposite side of the board. This is used to convert
     * locations so that Player 2 has a flipped view of the board.
     *
     * @param loc Location on the board
     * @return Inverted location
     */
    private Loc invertLocation(Loc loc) {
        return new Loc(StrategoModel.ROWS - 1 - loc.getRow(), StrategoModel.COLS - 1 - loc.getCol());
    }

    /**
     * Get a cell given a location. This method takes into account if the view is
     * inverted.
     *
     * @param loc Location in the model.
     * @return Corresponding board cell.
     */
    private BoardCell getCell(Loc loc) {
        if (inverted)
            loc = invertLocation(loc);
        return cells[loc.getRow()][loc.getCol()];
    }

    /**
     * Get the location in the model that corresponds to the given board cell. This
     * method accounts for the view being inverted.
     *
     * @param cell BoardCell on the grid.
     * @return Corresponding model location.
     */
    private Loc getLocation(BoardCell cell) {
        int row = GridPane.getRowIndex(cell);
        int col = GridPane.getColumnIndex(cell);
        Loc loc = new Loc(row, col);
        if (inverted)
            loc = invertLocation(loc);
        return loc;
    }

    /**
     * Performs game over animation when game over.
     * 
     * @param winnerTeam False if red team wins, true if blue team wins.
     */
    private void onGameOver(boolean winnerTeam) {
        animation.winner(winnerTeam);
        animation.setVisible(true);
        animation.playAnimation();
        System.out.println("Game over");
        setOurTurn(false);
    }

    /**
     * Closes all sockets.
     */
    public void onClosed() {
        if (controller != null) {
            controller.closeAllSockets();
        }
    }

    /**
     * Update the visuals on the grid after the controller has updated the model.
     *
     * @param o   Model instance
     * @param arg Piece information
     */
    @Override
    public void update(Observable o, Object arg) {
        StrategoMessage msg = (StrategoMessage) arg;
        boolean fromMe = ourTurn;
        if (msg.isBeginning()) {
            // Indicates that the client has connected and the placement
            // phase should start.
            setOurTurn(true);
            startPlacementPhase();
        } else if (msg.isGameStart()) {
            selectPiece(null, null);
            autoPlace.placeRemainingPieces();
            controller.setPlacement(false);
            setOurTurn(msg.getTeam() == controller.getTeam());
        } else if (msg.isPieceUpdate()) {
            StrategoPieceUpdate update = (StrategoPieceUpdate) msg;
            if (controller.isPlacement()) {
                fromMe = update.getTeam() == controller.getTeam();
            }
            if (arg instanceof StrategoPieceBattle) {
                StrategoPieceBattle battle = (StrategoPieceBattle) arg;

                if (battle.isTie()) {
                    animation.tie(getCell(battle.getWinner()).getPiece(), getCell(battle.getLoser()).getPiece());
                } else {
                    animation.battle(getCell(battle.getWinner()).getPiece(), getCell(battle.getLoser()).getPiece());
                }

                animation.setVisible(true);
                animation.playAnimation();

                getCell(battle.getWinner()).getPiece().setRevealed(true);
                getCell(battle.getLoser()).getPiece().setRevealed(true);
                if (fromMe)
                    controller.sendMessage(msg);
                return;
            }
            if (update.isPlacement()) {
                Piece piece = new Piece(update.getPieceType(), update.getTeam(), controller.getTeam());
                getCell(update.getTo()).setPiece(piece);
            } else if (update.isMove()) {
                BoardCell old = getCell(update.getFrom());
                Piece piece = old.getPiece();
                old.setPiece(null);
                getCell(update.getTo()).setPiece(piece);
            } else if (update.isRemove()) {
                getCell(update.getFrom()).setPiece(null);
            }
        } else if (msg.isEndOfTurn()) {
            if (fromMe && controller.isGameOver()) {
                controller
                        .sendMessage(new StrategoMessage(StrategoMessage.MessageType.GAME_OVER, controller.getTeam()));
                controller.closeAllSockets();
                onGameOver(controller.getTeam());
                return;
            }
            setOurTurn(msg.getTeam() == controller.getTeam());
        } else if (msg.isGameOver()) {
            onGameOver(msg.getTeam());
        }

        if (fromMe) { // send only changes that we made
            // System.out.println("\nOur turn! So send message.");
            controller.sendMessage(msg);
            // System.out.println("\t\tMessage sent.");
        }
    }

    // ============= Placement phase and Timer ======================

    /**
     * Starts placement phase after two players connected over network.
     */
    private void startPlacementPhase() {
        System.out.println("Placement phase started!");

        timerLabel = new Label("Time remaining (s): ");
        bottomButtons.getChildren().add(timerLabel);
        new GameTimer(StrategoController.PLACEMENT_PHASE_TIME);
    }

    /**
     * This class manages all things related to the timer used for the placement
     * phase. Time is measured in seconds.
     */
    private class GameTimer {

        Timer timer;
        int seconds;

        /**
         * Default constructor.
         * 
         * @param seconds The time length in seconds for placement phase.
         */
        GameTimer(int seconds) {
            this.seconds = seconds;
            timer = new Timer();
            timer.schedule(new DisplayCountdown(), 0, 1000); // timer.schedule(task, delay, period/interval)
        }

        /**
         * This class displays the timer onto the GUI.
         */
        private class DisplayCountdown extends TimerTask {
            int timeRemaining = seconds;

            /**
             * Performs the following tasks each second.
             */
            @Override
            public void run() {
                if (timeRemaining == 0) { // when placement phase ends
                    System.out.println("Placement phase ended.");
                    Platform.runLater(() -> {
                        autoPlace.placeRemainingPieces();
                        timerLabel.setText("Placement over. Entered battle phase.");
                        controller.sendMessage(
                                new StrategoMessage(StrategoMessage.MessageType.BEGIN_GAME, Piece.RED_TEAM));
                    });
                    this.cancel(); // stop timer
                } else {
                    System.out.println(timeRemaining + " seconds remaining in placement phase.");
                    timeRemaining -= 1;
                    Platform.runLater(() -> timerLabel.setText("Time remaining (s): " + (timeRemaining + 1)));
                }
            }
        }
    }
}
