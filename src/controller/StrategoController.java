package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import javafx.application.Platform;
import model.Loc;
import model.PieceType;
import model.StrategoModel;
import network.StrategoMessage;
import view.Piece;

/**
 * This class is the controller of the Stratego game.
 * 
 */
public class StrategoController implements Runnable {

    public final static int PLACEMENT_PHASE_TIME = 120; // seconds
    private StrategoModel model;

    // Object parameters
    private boolean team;
    private boolean server;
    private String hostName;
    private int portNumber;

    // Socket stuff
    private ServerSocket serverSocket = null;
    private Socket socket = null;

    private boolean placement = true; // if we are in the piece placement phase

    // Streams
    private ObjectOutputStream output;
    private ObjectInputStream input;

    // ============== Constructors ===================================
    /**
     * Opponent constructor.
     * 
     * @param model The model currently used for the current player.
     * @param team  The team of opponent.
     */
    public StrategoController(StrategoModel model, boolean team) {
        this.model = model;
        this.team = team;
    }

    /**
     * Main constructor.
     *
     * @param team     True for blue, false for red.
     * @param server   True for server, false for client.
     * @param hostName Name of host.
     * @param port     Port number.
     */
    public StrategoController(boolean team, boolean server, String hostName, int port) {
        this.model = new StrategoModel();
        this.team = team;
        this.server = server;
        this.hostName = hostName;
        this.portNumber = port;

        new Thread(this).start();
    }

    // ============== Methods ===================================

    /**
     * Determines which Piece wins the battle.
     *
     * This method assumes that the battle is a legal move. For example the
     * attacking piece will never be the flag.
     *
     * @param p1 The attacking piece.
     * @param p2 The piece being attacked.
     * @return The Piece that wins the battle, or null if tie.
     */
    private static Piece getBattleWinner(Piece p1, Piece p2) {
        // Bomb only loses against miner
        if (p2.getPieceType() == PieceType.BOMB) {
            if (p1.getPieceType() == PieceType.MINER)
                return p1;
            return p2;
        }
        // Spy wins against marshall
        if (p1.getPieceType() == PieceType.SPY && p2.getPieceType() == PieceType.MARSHALL)
            return p1;
        // Compare ranks
        if (p1.getRank() > p2.getRank()) {
            return p1;
        } else if (p1.getRank() < p2.getRank()) {
            return p2;
        } else {
            return null;
        }
    }

    /**
     * Returns the current model used by current constructor.
     * 
     * @return The current model used by current constructor
     */
    public StrategoModel getModel() {
        return model;
    }

    /**
     * Returns the team of current constructor.
     * 
     * @return the team of current constructor
     */
    public boolean getTeam() {
        return team;
    }

    /**
     * Returns true if placement phase, false if not.
     * 
     * @return True if placement phase, false if not.
     */
    public boolean isPlacement() {
        return placement;
    }

    /**
     * Sets placement phase status.
     * 
     * @param placement True if placement phase, false if not.
     */
    public void setPlacement(boolean placement) {
        this.placement = placement;
    }

    /**
     * Return whether it is currently the players turn to move.
     *
     * @return True if it is the player's turn, false otherwise.
     */
    public boolean isTurn() {
        // TODO add this when doing networking
        return true;
    }

    /**
     * Checks if current location is a lake.
     * 
     * @param loc The current location, represented by a Loc object.
     * @return True if current location is a lake, false otherwise.
     */
    public boolean isLake(Loc loc) {
        int row = loc.getRow();
        int col = loc.getCol();
        if (row >= 4 && row <= 5) {
            if (col >= 2 && col <= 3)
                return true;
            return col >= 6 && col <= 7;
        }
        return false;
    }

    /**
     * Checks if the current location has a piece or not.
     * 
     * @param loc The current location, represented by a Loc object.
     * @return True if the current location has a piece, false if not.
     */
    public boolean hasPiece(Loc loc) {
        return model.getPiece(loc) != null;
    }

    /**
     * Get the set of moves that can be made by the piece at the specified location.
     *
     * @param loc     Location to get moves for, or null for placing a piece.
     * @param fromBin Whether the piece being moved is coming from the bin or is
     *                already on the board.
     * @return Set of moves for the piece.
     */
    public Set<Loc> getMoves(Loc loc, boolean fromBin) {
        Set<Loc> moves = new HashSet<>();
        if (placement) {
            int startRow = 0;
            if (team == Piece.RED_TEAM)
                startRow = 6;
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < StrategoModel.COLS; c++) {
                    Loc move = new Loc(r + startRow, c);
                    if (fromBin) {
                        if (!hasPiece(move))
                            moves.add(move);
                    } else
                        moves.add(move);
                }
            }
            return moves;
        } else {
            Piece piece = model.getPiece(loc);
            if (!model.inBounds(loc) || piece == null)
                return moves;
            PieceType type = piece.getPieceType();
            // Flag and bomb cannot be moved
            if (type == PieceType.FLAG || type == PieceType.BOMB) {
                return moves;
            }
            int limit = 1;
            // Scout can move any distance
            if (type == PieceType.SCOUT)
                limit = StrategoModel.ROWS;
            // Add moves
            for (Direction dir : Direction.values()) {
                Loc current = loc;
                for (int i = 0; i < limit; i++) {
                    current = current.getRelative(dir, 1);
                    if (!model.inBounds(current) || isLake(current))
                        break;
                    if (hasPiece(current)) {
                        boolean team = model.getPiece(current).getTeam();
                        if (team != this.team)
                            moves.add(current);
                        break;
                    }
                    moves.add(current);
                }
            }
            return moves;
        }
    }

    /**
     * Moves piece from a location to another and battles if the new location has an
     * enemy piece.
     * 
     * @param from The current location, represented by a Loc object.
     * @param to   The new location, represented by a Loc object.
     */
    public void movePiece(Loc from, Loc to) {
        Piece p1 = model.getPiece(from);
        if (hasPiece(to)) { // if target location already contains a piece
            Piece p2 = model.getPiece(to);
            if (placement) {
                model.removePiece(p1);
                model.removePiece(p2);
                model.setPiece(p1, to);
                model.setPiece(p2, from);
            } else {
                battle(p1, p2);
            }
        } else {
            model.setPiece(p1, to);
        }
        if (!placement) {
            model.processUpdate(new StrategoMessage(StrategoMessage.MessageType.END_OF_TURN, !team));
        }
    }

    /**
     * Places piece into a location.
     * 
     * @param piece The Piece to place.
     * @param loc   The location, represented by a Loc object.
     */
    public void placePiece(Piece piece, Loc loc) {
        model.setPiece(piece, loc);
    }

    /**
     * Battles two pieces.
     * 
     * Two pieces fight, either the attacking piece wins and takes the position of
     * the other piece, or attacker is defeated and removed.
     * 
     * @param p1 The attacking piece.
     * @param p2 The piece being attacked.
     * @see StrategoController#getBattleWinner(Piece, Piece)
     */
    public void battle(Piece p1, Piece p2) {
        Piece winner = getBattleWinner(p1, p2);
        // Reveal pieces
        Piece tempWin = p1;
        if (winner == p2)
            tempWin = p2;
        Piece tempLose = p2;
        if (winner == p2)
            tempLose = p1;
        model.reveal(tempWin, tempLose, winner == null);
        if (winner == p1) {
            model.setPiece(p1, p2.getLocation());
        } else if (winner == p2) {
            model.removePiece(p1);
        } else { // tie
            model.removePiece(p1);
            model.removePiece(p2);
        }
    }

    /**
     * Checks if game over.
     * 
     * The game is over if a player's flag is stolen or if the player has no movable
     * pieces.
     * 
     * @return True if game over, false if not.
     */
    public boolean isGameOver() {
        return model.isGameOver(!team);
    }

    // ============== Networking ==================================

    /**
     * Send move to other player.
     *
     * @param message The message containing movement action.
     */
    public void sendMessage(StrategoMessage message) {
        if (!socket.isClosed()) {
            if (message != null) {
                try {
                    output.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    /**
     * Receive move from other player.
     * 
     */
    public void run() {
        try {
            // Initialize sockets
            if (server) {
                serverSocket = new ServerSocket(portNumber);
                System.out.println("Server started! Awaiting client to connect...");
                socket = serverSocket.accept();
                System.out.println("Socket accepted!");
            } else {
                socket = new Socket(hostName, portNumber);
                System.out.println("Client started!");
            }

            // Initialize streams
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            System.out.println("Streams initialized!");

            // Give the client a moment to load
            Thread.sleep(1000);
            Platform.runLater(() -> {
                model.processUpdate(new StrategoMessage(StrategoMessage.MessageType.BEGIN_PLACEMENT));
            });

            while (!socket.isClosed()) { // check that socket is open
                StrategoMessage inputMsg = (StrategoMessage) input.readObject();
                if (inputMsg != null) { // process input msg if it's not null (and it shouldn't be null)
                    Platform.runLater(() -> model.processUpdate(inputMsg));
                }

            }
        } catch (ClassNotFoundException | IOException | InterruptedException e) {
            // e.printStackTrace();
            closeAllSockets();
        }
    }

    /**
     * Closes Socket and ServerSocket.
     */
    public void closeAllSockets() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) { // Do nothing, i.e. ignore
        }

        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
        }
    }

}
