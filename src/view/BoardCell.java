package view;

import java.io.File;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * This class holds the board and piece images.
 *
 */
public class BoardCell extends StackPane {

    private Rectangle border;
    private Rectangle color;
    private Paint baseColor;
    private Group piecePane;
    private Group highlight;

    private ImageView image;
    private double bgSize;

    /**
     * Default constructor.
     * 
     * @param border The border size of a cell.
     * @param size   The inner size of a cell.
     */
    public BoardCell(double border, double size) {
        this.border = new Rectangle(size, size);
        bgSize = size - border * 2;
        color = new Rectangle(bgSize, bgSize);
        color.setVisible(false);
        baseColor = color.getFill();
        piecePane = new Group();
        highlight = new Group();

        image = new ImageView(new Image(File.separator + "assets" + File.separator + "transparent.png", bgSize, bgSize,
                false, false));

        getChildren().addAll(this.border, image, color, piecePane, highlight);
    }

    /**
     * Updates the color of the cell.
     */
    private void updateColor() {
        Piece piece = getPiece();
        if (piece != null) {
            color.setFill(piece.getColor());
            color.setVisible(true);
        } else {
            color.setFill(baseColor);
            color.setVisible(false);
        }
    }

    /**
     * Sets the color of the base.
     * 
     * @param fill The base color.
     */
    public void setColor(Paint fill) {
        baseColor = fill;
        updateColor();
    }

    /**
     * Sets the image of the cell.
     * 
     * @param str The address to the image asset.
     */
    public void setImage(String str) {
        ImageView replace = new ImageView(new Image(str, bgSize, bgSize, false, false));
        getChildren().set(1, replace);
    }

    /**
     * Returns the Piece in the cell, which is on the top layer.
     * 
     * @return The Piece in the cell.
     */
    public Piece getPiece() {
        ObservableList<Node> children = piecePane.getChildren();
        if (children.size() < 1)
            return null;
        return (Piece) children.get(0);
    }

    /**
     * Sets the piece in the cell.
     * 
     * @param piece The piece to place in the cell.
     */
    public void setPiece(Piece piece) {
        if (piece == null)
            piecePane.getChildren().clear();
        else
            piecePane.getChildren().setAll(piece);
        updateColor();
    }

    /**
     * Highlights the cell when selected.
     * 
     * @param node The Node to add to top layer.
     */
    public void setHighlight(Node node) {
        if (node == null)
            highlight.getChildren().clear();
        else
            highlight.getChildren().setAll(node);
    }

}
