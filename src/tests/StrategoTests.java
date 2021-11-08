package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import controller.StrategoController;
import model.Loc;
import model.PieceType;
import model.StrategoModel;
import view.Piece;

public class StrategoTests {
    @Test
    void testSetPieceOutOfBounds() {
        StrategoModel m = new StrategoModel();
        Piece p = new Piece(PieceType.CAPTAIN);
        Loc l = new Loc(10, 0);
        assertEquals(null, m.setPiece(p, l));
    }

    @Test
    void testGetPieceOutOfBounds() {
        StrategoModel m = new StrategoModel();
        Piece p = new Piece(PieceType.CAPTAIN);
        Loc l = new Loc(10, 0);
        assertEquals(null, m.getPiece(l));
    }

    @Test
    void testSetPieceToCurrentLocation() {
        StrategoModel m = new StrategoModel();
        Piece p1 = new Piece(PieceType.CAPTAIN, false);
        Loc l1 = new Loc(0, 0);
        m.setPiece(p1, l1);
        assertEquals(null, m.setPiece(p1, l1));
    }

    @Test
    void testIsTurn() {
        StrategoController sc = new StrategoController(true, true, "localhost", 4000);
        assertTrue(sc.isTurn());
    }

    @Test
    void testGetQuantity() {
        Piece p = new Piece(PieceType.SCOUT, true);
        assertEquals(8, p.getPieceType().getQuantity());
    }

    @Test
    void testInBounds() {
        StrategoModel m = new StrategoModel();

        for (int i = 0; i < StrategoModel.ROWS; i++) {
            for (int j = 0; j < StrategoModel.COLS; j++) {
                assertTrue(m.inBounds(new Loc(i, j)));
            }
        }
        assertFalse(m.inBounds(null));
        assertFalse(m.inBounds(new Loc(-1, 0)));
        assertFalse(m.inBounds(new Loc(0, -1)));
        assertFalse(m.inBounds(new Loc(10, 0)));
        assertFalse(m.inBounds(new Loc(0, 10)));
    }

    @Test
    void testGetTeam1() {
        StrategoController sc = new StrategoController(true, true, "localhost", 4000);
        assertTrue(sc.getTeam());
    }

    @Test
    void testGetTeam2() {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        assertFalse(sc.getTeam());
    }

    @Test
    void testIsPlacement() {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        assertTrue(sc.isPlacement());
        sc.setPlacement(false);
        assertFalse(sc.isPlacement());
    }

    @Test
    void testModel() {
        StrategoModel m = new StrategoModel();
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        assertFalse(m.equals(sc.getModel()));
    }

    @Test
    void testIsLake() {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        assertTrue(sc.isLake(new Loc(4, 2)));
        assertTrue(sc.isLake(new Loc(5, 2)));
        assertTrue(sc.isLake(new Loc(4, 3)));
        assertTrue(sc.isLake(new Loc(5, 3)));
        assertTrue(sc.isLake(new Loc(4, 6)));
        assertTrue(sc.isLake(new Loc(5, 6)));
        assertTrue(sc.isLake(new Loc(4, 7)));
        assertTrue(sc.isLake(new Loc(5, 7)));
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < StrategoModel.COLS; j++) {
                assertFalse(sc.isLake(new Loc(i, j)));
            }
        }
        for (int i = 6; i < StrategoModel.ROWS; i++) {
            for (int j = 0; j < StrategoModel.COLS; j++) {
                assertFalse(sc.isLake(new Loc(i, j)));
            }
        }
        int[] col = { 0, 1, 4, 5, 8, 9 };
        for (int i = 4; i < 6; i++) {
            for (int j = 0; j < col.length; j++) {
                assertFalse(sc.isLake(new Loc(i, col[j])));
            }
        }
    }

    @Test
    void testHasPieceEmptyBoard() {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        sc.getModel();
        for (int i = 0; i < StrategoModel.ROWS; i++) {
            for (int j = 0; j < StrategoModel.COLS; j++) {
                assertFalse(sc.hasPiece(new Loc(i, j)));
            }
        }
    }

    @Test
    void testHasPieceWithMove() {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        sc.getModel();
        Loc l1 = new Loc(0, 0);
        Loc l2 = new Loc(0, 1);
        sc.placePiece(new Piece(PieceType.SCOUT), l1);
        assertTrue(sc.hasPiece(l1));
        sc.movePiece(l1, l2);
        assertFalse(sc.hasPiece(l1));
        assertTrue(sc.hasPiece(l2));
    }

    @Test
    // use movePiece to trigger a battle
    // moving piece loses
    void testMoveBattleMoverLoses() throws IOException {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        sc.setPlacement(false);

        Loc l1 = new Loc(0, 0);
        Loc l2 = new Loc(0, 1);

        Piece scout = new Piece(PieceType.SCOUT);
        Piece captain = new Piece(PieceType.CAPTAIN);

        sc.placePiece(scout, l1);
        sc.placePiece(captain, l2);

        assertEquals(scout.getLocation(), l1); // check locations
        Loc oldCapLoc = captain.getLocation();
        assertEquals(oldCapLoc, l2);

        sc.movePiece(l1, l2); // battle the scout and captain
        assertEquals(oldCapLoc, captain.getLocation()); // captain wins battle so stays here
        assertEquals(null, scout.getLocation()); // scouts loses battle so becomes null
    }

    @Test
    // use movePiece to trigger a battle
    // moving piece wins
    void testMoveBattleMoverWins() throws IOException {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        sc.setPlacement(false);

        Loc l1 = new Loc(0, 0);
        Loc l2 = new Loc(0, 1);

        Piece scout = new Piece(PieceType.SCOUT);
        Piece captain = new Piece(PieceType.CAPTAIN);

        sc.placePiece(scout, l2);
        sc.placePiece(captain, l1);

        assertEquals(captain.getLocation(), l1); // check locations
        Loc oldScoutLoc = scout.getLocation();
        assertEquals(oldScoutLoc, l2);

        sc.movePiece(l1, l2); // battle the scout and captain
        assertEquals(oldScoutLoc, captain.getLocation()); // captain wins battle so moves to scout old loc
        assertEquals(null, scout.getLocation()); // scouts loses battle so becomes null
    }

    @Test
    // use movePiece to trigger a battle
    // tie
    void testMoveBattleTie() throws IOException {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        sc.setPlacement(false);

        Loc l1 = new Loc(0, 0);
        Loc l2 = new Loc(0, 1);

        Piece p1 = new Piece(PieceType.CAPTAIN);
        Piece p2 = new Piece(PieceType.CAPTAIN);

        sc.placePiece(p1, l1);
        sc.placePiece(p2, l2);

        assertEquals(p1.getLocation(), l1); // check locations
        Loc oldP2Loc = p2.getLocation();
        assertEquals(oldP2Loc, l2);

        sc.movePiece(l1, l2); // battle the scout and captain
        assertEquals(null, p1.getLocation()); // tie so both null
        assertEquals(null, p2.getLocation());
    }

    @Test
    // use movePiece to trigger a battle
    // non-Miner attacks bomb
    void testMoveBattleLoseToBomb() throws IOException {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        sc.setPlacement(false);

        Loc l1 = new Loc(0, 0);
        Loc l2 = new Loc(0, 1);

        Piece p1 = new Piece(PieceType.CAPTAIN);
        Piece p2 = new Piece(PieceType.BOMB);

        sc.placePiece(p1, l1);
        sc.placePiece(p2, l2);

        sc.movePiece(l1, l2); // captain attacks bomb
        assertEquals(null, p1.getLocation()); // captain dies
        assertEquals(l2, p2.getLocation()); // bomb wins
    }

    @Test
    // use movePiece to trigger a battle
    // miner attacks bomb
    void testMoveBattleMinerBeatsBomb() throws IOException {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        sc.setPlacement(false);

        Loc l1 = new Loc(0, 0);
        Loc l2 = new Loc(0, 1);

        Piece p1 = new Piece(PieceType.MINER);
        Piece p2 = new Piece(PieceType.BOMB);

        sc.placePiece(p1, l1);
        sc.placePiece(p2, l2);

        sc.movePiece(l1, l2); // captain attacks bomb
        assertEquals(l2, p1.getLocation()); // miner wins
        assertEquals(null, p2.getLocation()); // bomb dies
    }

    @Test
    // use movePiece to trigger a battle
    // spy attacks marshall
    void testMoveBattleSpyBeatsMarshal() throws IOException {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        sc.setPlacement(false);

        Loc l1 = new Loc(0, 0);
        Loc l2 = new Loc(0, 1);

        Piece p1 = new Piece(PieceType.SPY);
        Piece p2 = new Piece(PieceType.MARSHALL);

        sc.placePiece(p1, l1);
        sc.placePiece(p2, l2);

        sc.movePiece(l1, l2); // captain attacks bomb
        assertEquals(l2, p1.getLocation()); // Spy wins
        assertEquals(null, p2.getLocation()); // Marshall dies
    }

    @Test
    void testSwapPieceDuringPlacement() {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        Piece scout = new Piece(PieceType.SCOUT);
        Piece captain = new Piece(PieceType.CAPTAIN);

        Loc l1 = new Loc(0, 0);
        Loc l2 = new Loc(0, 1);

        sc.placePiece(scout, l2);
        sc.placePiece(captain, l1);

        assertEquals(captain.getLocation(), l1); // check old locations
        assertEquals(scout.getLocation(), l2);

        sc.movePiece(l1, l2); // swap pieces using movePiece method

        assertEquals(captain.getLocation(), l2); // check new locations
        assertEquals(scout.getLocation(), l1);
    }

    @Test
    // isGameOver only look at other team's pieces, so our pieces don't matter
    void testIsGameOverEmptyBoard() {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        sc.setPlacement(false);
        assertTrue(sc.isGameOver());
    }

    @Test
    // isGameOver only look at other team's pieces, so our pieces don't matter
    void testIsGameOverEnemyNoFlag() {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        sc.setPlacement(false);

        Piece p1 = new Piece(PieceType.SCOUT, true);
        sc.placePiece(p1, new Loc(9, 9));
        assertTrue(sc.isGameOver()); // other team no flag
    }

    @Test
    // isGameOver only look at other team's pieces, so our pieces don't matter
    void testIsGameOverEnemyHasFlagButNoMoveablePiece() {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        sc.setPlacement(false);

        Piece p1 = new Piece(PieceType.FLAG, true);
        Piece p2 = new Piece(PieceType.BOMB, true);
        sc.placePiece(p1, new Loc(9, 8));
        sc.placePiece(p2, new Loc(9, 9));
        assertTrue(sc.isGameOver()); // other team has flag but no moveable pieces
    }

    @Test
    // isGameOver only look at other team's pieces, so our pieces don't matter
    void testIsGameOverEnemyHasFlagAndMoveablePiece() {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        sc.setPlacement(false);

        Piece p1 = new Piece(PieceType.FLAG, true);
        Piece p2 = new Piece(PieceType.SCOUT, true);
        sc.placePiece(p1, new Loc(9, 8));
        sc.placePiece(p2, new Loc(9, 9));
        assertFalse(sc.isGameOver()); // other team has flag but no moveable pieces
    }

    @Test
    // only zero pieces
    void testGetMovesPlacementPhaseZeroPieces() {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        Piece p = new Piece(PieceType.SCOUT, false);
        Loc l = new Loc(9, 9);
        Set<Loc> set = new HashSet<Loc>();
        for (int i = 6; i < StrategoModel.ROWS; i++) {
            for (int j = 0; j < StrategoModel.COLS; j++) {
                set.add(new Loc(i, j));
            }
        }
        assertEquals(set, sc.getMoves(null, true));
    }

    @Test
    // only one piece
    void testGetMovesPlacementPhaseOnePiece() {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        Piece p = new Piece(PieceType.SCOUT, false);
        Loc l = new Loc(9, 9);
        sc.placePiece(p, l);
        Set<Loc> set = new HashSet<Loc>();
        for (int i = 6; i < StrategoModel.ROWS; i++) {
            for (int j = 0; j < StrategoModel.COLS; j++) {
                set.add(new Loc(i, j));
            }
        }
        assertEquals(set, sc.getMoves(l, false));
    }

    @Test
    // Scout can move any distance until enemy
    void testGetMovesBattlePhaseScout() {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        sc.setPlacement(false);

        Piece p1 = new Piece(PieceType.SCOUT, false);
        Piece p2 = new Piece(PieceType.CAPTAIN, true);

        Loc l1 = new Loc(9, 0);
        Loc l2 = new Loc(7, 0);

        sc.placePiece(p1, l1);
        sc.placePiece(p2, l2);
        // assertEquals(new HashSet<Loc>(), sc.getMoves(l, false));
        Set<Loc> set = sc.getMoves(l1, false);
        set.add(new Loc(7, 0));
        set.add(new Loc(8, 0));
        for (int j = 1; j < StrategoModel.COLS; j++) {
            set.add(new Loc(9, j));
        }
        assertEquals(set, sc.getMoves(l1, false));

    }

    @Test
    // captain can move one unit distance
    void testGetMovesBattlePhaseCaptain() {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        sc.setPlacement(false);
        Piece p = new Piece(PieceType.CAPTAIN, false);
        Loc l = new Loc(6, 2);
        sc.placePiece(p, l);
        // assertEquals(new HashSet<Loc>(), sc.getMoves(l, false));
        Set<Loc> set = sc.getMoves(l, false);
        set.add(new Loc(6, 1));
        set.add(new Loc(6, 3));
        set.add(new Loc(7, 2));
        assertEquals(set, sc.getMoves(l, false));
    }

    @Test
    // bomb can't move
    void testGetMovesBattlePhaseBomb() {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        sc.setPlacement(false);
        Piece p = new Piece(PieceType.BOMB, false);
        Loc l = new Loc(7, 7);
        sc.placePiece(p, l);
        assertEquals(new HashSet<Loc>(), sc.getMoves(l, false));
    }

    @Test
    // flag can't move
    void testGetMovesBattlePhaseFlag() {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        sc.setPlacement(false);
        Piece p = new Piece(PieceType.FLAG, false);
        Loc l = new Loc(7, 7);
        sc.placePiece(p, l);
        assertEquals(new HashSet<Loc>(), sc.getMoves(l, false));
    }

    @Test
    // null piece
    void testGetMovesBattlePhaseNull() {
        StrategoController sc = new StrategoController(false, true, "localhost", 4000);
        sc.setPlacement(false);
        Loc l = new Loc(7, 7);
        assertEquals(new HashSet<Loc>(), sc.getMoves(l, false));
    }

    @Test
    void testGetModel() {
        StrategoModel m = new StrategoModel();
        StrategoController sc = new StrategoController(m, true);
        assertEquals(m, sc.getModel());
    }

    @Test
    void testLocToString() {
        assertEquals("0, 0", new Loc(0, 0).toString());
    }

    @Test
    void testLocIsEquals() {
        Loc l1 = new Loc(0, 0);
        Loc l2 = new Loc(1, 1);
        assertFalse(l1.equals(l2));
        assertFalse(l1.equals(null));
    }

}
