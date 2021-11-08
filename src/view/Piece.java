package view;

import java.io.File;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import model.Loc;
import model.PieceType;

/**
 * Represents a piece on the game board. Holds the piece's image, type, team,
 * location on the board, whether or not the piece has been revealed by the
 * enemy player, and the team of the active player. These values can be set and
 * retrieved by the provided methods.
 * 
 */
public class Piece extends ImageView {

    public static final boolean RED_TEAM = false;
    public static final boolean BLUE_TEAM = true;

    private Loc loc;
    private boolean revealed;

    private PieceType type;
    private boolean team;
    private boolean viewTeam;
    private boolean loadGfx;

    public Piece(PieceType type, boolean team, boolean viewTeam) {
        this.type = type;
        this.team = team;
        this.viewTeam = viewTeam;
        this.loadGfx = true;
        loadGraphic();
    }

    /**
     * For testing only. Constructor. Sets global variables.
     * 
     * @param type Type of Piece object.
     */
    public Piece(PieceType type) {
        this.type = type;
        this.team = false;
        this.viewTeam = team;
        this.loadGfx = false;
    }

    /**
     * For testing only. Constructor. Sets global variables.
     * 
     * @param type Type of piece object.
     * @param team False for red, true for blue.
     */
    public Piece(PieceType type, boolean team) {
        this.type = type;
        this.team = team;
        this.viewTeam = team;
        this.loadGfx = false;
    }

    /**
     * Load the graphic that represents this piece.
     *
     * This class extends a Node for the graphic of the piece, which can be changed
     * to something else if the pieces use a different graphic (e.g. ImageView).
     */
    private void loadGraphic() {
        double size = BoardScene.TILE_SIZE - BoardScene.BORDER_SIZE * 2;
        String source = File.separator + "assets" + File.separator + type.name() + ".png";
        if (!revealed && team != viewTeam)
            source = File.separator + "assets" + File.separator + "unknown.png";
        this.setImage(new Image(source, size, size, true, true));
    }

    /**
     * 
     * @return Piece's rank.
     */
    public int getRank() {
        return getPieceType().getRank();
    }

    /**
     * 
     * @return Team's color.
     */
    public Paint getColor() {
        if (team == RED_TEAM)
            return Color.DARKRED;
        return Color.DARKBLUE;
    }

    /**
     * 
     * @return Piece's type.
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * 
     * @return Team.
     */
    public boolean getTeam() {
        return team;
    }

    /**
     * 
     * @return Location.
     */
    public Loc getLocation() {
        return loc;
    }

    /**
     * Sets the global variable Location with the given parameter.
     * 
     * @param loc Location.
     */
    public void setLocation(Loc loc) {
        this.loc = loc;
    }

    /**
     * Sets the global variable to the parameter. Load graphic if the global
     * variable is true.
     * 
     * @param revealed True if revealed
     */
    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
        if (loadGfx) {
            loadGraphic();
        }
    }

    /**
     * Used for debugging.
     */
    @Override
    public String toString() {
        String color = "";
        if (team) {
            color = "BLUE";
        } else {
            color = "RED";
        }

        return type.name() + " " + color;
    }

}
