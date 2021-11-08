package view;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import controller.StrategoController;
import javafx.scene.control.Button;
import model.Loc;

/**
 * Button for autoplacing the pieces randomly.
 */
public class PieceAutoPlace extends Button {

    private PieceBin bin;
    private StrategoController controller;

    public PieceAutoPlace(PieceBin bin, StrategoController controller) {
        super("Randomly place pieces");
        this.bin = bin;
        this.controller = controller;
        this.setOnAction((event) -> randomizePieces());
    }

    /**
     * Places pieces onto the board.
     * 
     * @param controller Controller to place pieces onto.
     * @param reset      Whether to reset the piece bin before shuffling. If true,
     *                   will randomly place a full set of pieces on the board. If
     *                   false, the pieces that have already been placed on the
     *                   board will not be disturbed.
     */
    private void placePieces(StrategoController controller, boolean reset) {
        List<Piece> pieceList = getPiecesShuffled(reset);
        Set<Loc> moves = controller.getMoves(null, false);
        for (Loc move : moves) {
            if (!reset && controller.hasPiece(move))
                continue;
            controller.placePiece(pieceList.remove(0), move);
        }
        bin.disableAll();
    }

    /**
     * Get a shuffled list of pieces from the piece bin. Optionally reset the pieces
     * in the piece bin before shuffling.
     *
     * @param reset If true, returns a full set of pieces. If false, returns a
     *              shuffled list of the pieces that have yet to be placed.
     * @return Shuffled list of pieces.
     */
    private List<Piece> getPiecesShuffled(boolean reset) {
        if (reset)
            bin.resetPieces();
        List<Piece> list = bin.remainingPieces();
        Collections.shuffle(list);
        return list;
    }

    /**
     * Randomize all of the pieces on the board, disregarding any pieces already
     * placed.
     */
    public void randomizePieces() {
        placePieces(controller, true);
    }

    /**
     * Place all the pieces that haven't yet been placed. Pieces already on the
     * board will not be disturbed.
     */
    public void placeRemainingPieces() {
        placePieces(controller, false);
    }

}
