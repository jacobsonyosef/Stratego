package controller;

/**
 * Direction enum.
 * 
 * @author jessechen
 */

public enum Direction {

    UP(-1, 0), RIGHT(0, 1), DOWN(1, 0), LEFT(0, -1);

    private int deltaRow;
    private int deltaCol;

    /**
     * Default constructor.
     * 
     * @param deltaRow Difference in row axis.
     * @param deltaCol Difference in column axis.
     */
    Direction(int deltaRow, int deltaCol) {
        this.deltaRow = deltaRow;
        this.deltaCol = deltaCol;
    }

    /**
     * Returns the change in units in row axis.
     * 
     * @return the change in units in row axis
     */
    public int getDeltaRow() {
        return deltaRow;
    }

    /**
     * Returns the change in units in column axis.
     * 
     * @return the change in units in column axis
     */
    public int getDeltaCol() {
        return deltaCol;
    }

}
