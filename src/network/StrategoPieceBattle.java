package network;

import model.Loc;

/**
 * A specific type of piece update message, where battle takes place.
 *
 */
public class StrategoPieceBattle extends StrategoPieceUpdate {

    private boolean tie;

    /**
     * Default constructor
     * 
     * @param p1  Location of piece 1.
     * @param p2  Location of piece 2.
     * @param tie True if battle is a tie.
     */
    public StrategoPieceBattle(Loc p1, Loc p2, boolean tie) {
        super(p1, p2, false);
        this.tie = tie;
    }

    /**
     * Return the starting location of the winner.
     * 
     * @return the starting location of the winner.
     */
    public Loc getWinner() {
        return getFrom();
    }

    /**
     * Return the starting location of the loser.
     * 
     * @return the starting location of the loser.
     */
    public Loc getLoser() {
        return getTo();
    }

    /**
     * Returns true if message is a tie.
     * 
     * @return true if message is a tie.
     */
    public boolean isTie() {
        return tie;
    }

}
