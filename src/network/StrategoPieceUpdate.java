package network;

import model.Loc;
import model.PieceType;

/**
 * A message that a piece has been placed, removed, or moved.
 */
public class StrategoPieceUpdate extends StrategoMessage {

    private Loc from;
    private Loc to;
    private PieceType pieceType;

    private StrategoPieceUpdate() {
        super(MessageType.PIECE_UPDATE);
    }

    /**
     * Create a message that a piece was removed.
     *
     * @param from Where the piece was removed.
     */
    public StrategoPieceUpdate(Loc from) {
        this();
        this.from = from;
    }

    /**
     * Create a message that a piece has moved.
     *
     * @param from Old location
     * @param to   New location
     * @param team False for red, true for blue.
     */
    public StrategoPieceUpdate(Loc from, Loc to, boolean team) {
        this();
        this.from = from;
        this.to = to;
        setTeam(team);
    }

    /**
     * Create a message that a piece has been placed.
     *
     * @param to        Placed location.
     * @param pieceType Piece type that was placed.
     * @param team      Which team the piece belongs to.
     */
    public StrategoPieceUpdate(Loc to, PieceType pieceType, boolean team) {
        this();
        this.to = to;
        this.pieceType = pieceType;
        setTeam(team);
    }

    /**
     * Get whether this message represents a piece placement.
     *
     * @return True if this message is a placement, false otherwise.
     */
    public boolean isPlacement() {
        return from == null && to != null;
    }

    /**
     * Get whether this message represents a move.
     *
     * @return True if this message is a move, false otherwise.
     */
    public boolean isMove() {
        return from != null && to != null;
    }

    /**
     * Get whether this message represents a remove.
     *
     * @return True if this message is a remove, false otherwise.
     */
    public boolean isRemove() {
        return from != null && to == null;
    }

    /**
     * Get the location the piece moved from.
     *
     * @return Old location.
     */
    public Loc getFrom() {
        return from;
    }

    /**
     * Get the location the piece moved to.
     *
     * @return New location.
     */
    public Loc getTo() {
        return to;
    }

    /**
     * Get the type of piece that was placed. This is only applicable when this
     * message is for piece placement.
     *
     * @return New piece type.
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Returns message type.
     * 
     * @return message type.
     */
    public String getMessageType() {
        if (this.isMove())
            return "move";
        else if (this.isPlacement())
            return "placement";
        else if (this.isRemove())
            return "remove";
        else
            return "";
    }
}
