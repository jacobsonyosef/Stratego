package model;

import java.io.Serializable;

import controller.Direction;

/**
 * The Loc class holds the information of a coordinate position.
 * 
 *
 */
public class Loc implements Serializable {

    private final int row;
    private final int col;

    /**
     * Default constructor.
     * 
     * @param row The row coordinate of the location.
     * @param col The col coordinate of the location.
     */
    public Loc(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns the row of the location.
     * 
     * @return the row of the location
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column of the location.
     * 
     * @return the column of the location
     */
    public int getCol() {
        return col;
    }

    /**
     * Creates a new Loc object in a specified direction and at a specified
     * distance.
     * 
     * @param dir  The direction to the new location.
     * @param dist The distance to the new location.
     * @return a new Loc object in a specified direction and at a specified distance
     */
    public Loc getRelative(Direction dir, int dist) {
        return new Loc(row + dir.getDeltaRow() * dist, col + dir.getDeltaCol() * dist);
    }

    /**
     * Compares two locations.
     *
     * @param o The object to compare to.
     * @return True if the two Loc objects have the same (row,col) coordinates.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Loc pos = (Loc) o;
        if (row != pos.row)
            return false;
        return col == pos.col;
    }

    /**
     * Creates a hash code.
     * 
     * @return a hash code for the set.
     */
    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + col;
        return result;
    }

    /**
     * Outputs the row and col in a string. For example, for row=1, and col=3, the
     * output is: 1, 3
     */
    @Override
    public String toString() {
        return row + ", " + col;
    }

}
