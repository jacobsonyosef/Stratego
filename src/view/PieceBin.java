package view;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import model.PieceType;

/**
 * Displays the piece bin. Holds all the characters and how many each player is
 * supposed to have of each.
 * 
 */
public class PieceBin extends GridPane {

    private BoardCell[][] pieces;
    private int[][] amounts;

    private int rows;
    private int cols;

    private BoardScene scene;
    private boolean team;

    /**
     * Constructor. Sets the global variables, creates the BoardCells for the
     * pieces, and creates the amounts each piece should hold.
     * 
     * @param scene The scene object to add PieceBin to
     * @param team  False for team red, true for team blue
     */
    public PieceBin(BoardScene scene, boolean team) {
        super();
        this.setAlignment(Pos.CENTER);
        this.scene = scene;
        this.team = team;

        PieceType[] types = PieceType.values();
        rows = 2;
        cols = types.length / 2;
        pieces = new BoardCell[rows][cols];
        amounts = new int[rows][cols];
        resetPieces();
    }

    /**
     * Sets up the pieces inside a bin.
     */
    public void resetPieces() {
        PieceType[] types = PieceType.values();
        getChildren().clear();
        int pieceNo = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                BoardCell cell = new BoardCell(BoardScene.BORDER_SIZE, BoardScene.TILE_SIZE);
                cell.setPiece(new Piece(types[pieceNo], team, team));
                pieces[i][j] = cell;
                amounts[i][j] = types[pieceNo].getQuantity();
                pieceNo++;
                this.add(cell, j, i);

                updateHighlight(i, j);
                cell.setOnMouseClicked(event -> {
                    scene.selectPiece(null, cell);
                });

                cell.setOnDragDetected(event -> {
                    scene.selectPiece(null, cell);
                    scene.startFullDrag();
                });
            }
        }
    }

    /**
     * Goes through the pieces and sees which pieces still have amounts left.
     * Returns the list of pieces.
     * 
     * @return A list of the remaining pieces.
     */
    public List<Piece> remainingPieces() {
        List<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                PieceType type = this.pieces[i][j].getPiece().getPieceType();
                int amount = amounts[i][j];
                for (int c = 0; c < amount; c++) {
                    pieces.add(new Piece(type, team, team));
                }
            }
        }
        return pieces;
    }

    /**
     * Disables the pieces. Sets the amounts to zero for each piece.
     */
    public void disableAll() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                amounts[i][j] = 0;
                updateHighlight(i, j);
            }
        }
    }

    /**
     * Highlights the piece when selected.
     * 
     * @param row Row of piece.
     * @param col Column of piece.
     */
    private void updateHighlight(int row, int col) {
        int amount = amounts[row][col];
        if (amount > 0) {
            Label count = new Label("x" + amount);
            count.setTextFill(Color.WHITE);
            count.setFont(new Font(20));
            pieces[row][col].setHighlight(count);
            pieces[row][col].setDisable(false);
        } else {
            Rectangle darken = new Rectangle(BoardScene.TILE_SIZE, BoardScene.TILE_SIZE, Color.gray(0, 0.5));
            pieces[row][col].setHighlight(darken);
            pieces[row][col].setDisable(true);
        }
    }

    /**
     * Gets the row and column from the passed BoardCell. Calls updateHightlight.
     * 
     * @param cell Selected BoardCell.
     */
    public void onDeselect(BoardCell cell) {
        int row = GridPane.getRowIndex(cell);
        int col = GridPane.getColumnIndex(cell);
        updateHighlight(row, col);
    }

    /**
     * Selects piece. Removes one amount if there are any. If there aren't return
     * null. Gets row and column of BoardCell and updates highlight. Returns the
     * Piece.
     * 
     * @param cell Current BoardCell selected.
     * @param team Team.
     * @return Null if no more amount, Piece if there are amounts left.
     */
    public Piece takePiece(BoardCell cell, boolean team) {
        int row = GridPane.getRowIndex(cell);
        int col = GridPane.getColumnIndex(cell);
        if (amounts[row][col] > 0) {
            amounts[row][col]--;
            updateHighlight(row, col);
            return new Piece(cell.getPiece().getPieceType(), team, team);
        }
        return null;
    }
}
