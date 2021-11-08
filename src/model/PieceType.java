package model;
/**
 * Holds basic information about pieces such as their rank and number of
 * pieces that each player starts with.
 */
public enum PieceType {

    FLAG(-1, 1),
    BOMB(0, 6),
    SPY(1, 1),
    SCOUT(2, 8),
    MINER(3, 5),
    SERGEANT(4, 4),
    LIEUTENANT(5, 4),
    CAPTAIN(6, 4),
    MAJOR(7, 3),
    COLONEL(8, 2),
    GENERAL(9, 1),
    MARSHALL(10, 1);
    
    private int rank;
    private int quantity;
    
    /**
     * Set the rank and quantity of each piece.
     *
     * @param rank Piece rank.
     * @param quantity Number that each player starts with.
     */
    PieceType(int rank, int quantity) {
        this.rank = rank;
        this.quantity = quantity;
    }
    
    /**
     * Get the rank of this piece type.
     *
     * @return Piece rank.
     */
    public int getRank() {
        return rank;
    }
    
    /**
     * Get the quantity of this piece type that players begin
     * the game with.
     *
     * @return Piece quantity.
     */
    public int getQuantity() {
        return quantity;
    }
}
