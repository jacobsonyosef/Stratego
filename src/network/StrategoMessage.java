package network;

import java.io.Serializable;

/**
 * This class helps determine the type of message being sent.
 * 
 */
public class StrategoMessage implements Serializable {

    private MessageType type;
    private boolean team;

    /**
     * Default constructor.
     * 
     * @param type message type.
     */
    public StrategoMessage(MessageType type) {
        this.type = type;
    }

    /**
     * Second constructor.
     * 
     * @param type message type.
     * @param team the team for the message.
     */
    public StrategoMessage(MessageType type, boolean team) {
        this(type);
        this.team = team;
    }

    /**
     * Returns true if message is of type BEGIN_PLACEMENT.
     * 
     * @return true if message is of type BEGIN_PLACEMENT.
     */
    public boolean isBeginning() {
        return type == MessageType.BEGIN_PLACEMENT;
    }

    /**
     * Returns true if message is of type BEGIN_GAME.
     * 
     * @return true if message is of type BEGIN_GAME.
     */
    public boolean isGameStart() {
        return type == MessageType.BEGIN_GAME;
    }

    /**
     * Returns true if message is of type PIECE_UPDATE.
     * 
     * @return true if message is of type PIECE_UPDATE.
     */
    public boolean isPieceUpdate() {
        return type == MessageType.PIECE_UPDATE;
    }

    /**
     * Returns true if message is of type END_OF_TURN.
     * 
     * @return true if message is of type END_OF_TURN.
     */
    public boolean isEndOfTurn() {
        return type == MessageType.END_OF_TURN;
    }

    /**
     * Returns true if message is of type GAME_OVER.
     * 
     * @return true if message is of type GAME_OVER.
     */
    public boolean isGameOver() {
        return type == MessageType.GAME_OVER;
    }

    /**
     * Returns the message type.
     * 
     * @return the message type.
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Returns false if team red, true if team blue.
     * 
     * @return false if team red, true if team blue.
     */
    public boolean getTeam() {
        return team;
    }

    /**
     * Sets the team.
     * 
     * @param team false if team red, true if team blue
     */
    public void setTeam(boolean team) {
        this.team = team;
    }

    /**
     * Message type enum.
     * 
     * @author jessechen
     *
     */
    public enum MessageType {
        BEGIN_PLACEMENT, BEGIN_GAME, PIECE_UPDATE, END_OF_TURN, GAME_OVER
    }

}
