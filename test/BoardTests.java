import org.junit.Test;
import static org.junit.Assert.*;

public  class BoardTests{
    @Test
    public void testMovePawn(){
        Board board = new Board();
        board.setDefaultPosition();
        Move oneSquare = new Move(6, 2, 5,2, 0);
        Move twoSquare = new Move(6, 2, 4,2, 0);
        Move threeSquare = new Move(6, 2, 3,2, 0);

        assertTrue(board.validMove(oneSquare));
        assertTrue(board.validMove(twoSquare));
        assertFalse(board.validMove(threeSquare));
    }

    @Test public void testBishopMove(){
        Board board = new Board();
        board.setDefaultPosition();

        Move whitePawn = new Move(6,4,4,4,0);
        Move blackPawn = new Move(1,4,3,4,0);

        assertTrue(board.validMove(whitePawn));
        assertTrue(board.validMove(blackPawn));

        board = board.makeMove(whitePawn);
        board = board.makeMove(blackPawn);

        Move validWhiteBishop = new Move(7,5,6,4,0);
        assertTrue(board.validMove(validWhiteBishop));
        board = board.makeMove(validWhiteBishop);

        Move validBlackBishop = new Move(0,5,3,2,0);
        assertTrue(board.validMove(validBlackBishop));
        board = board.makeMove(validBlackBishop);

        Move invalidMove = new Move(6,4,5,4,0);
        assertFalse(board.validMove(invalidMove));

        invalidMove = new Move(3,2,5,2,0);
        assertFalse(board.validMove(invalidMove));
    }

    @Test
    public void testKnightMove(){
        Board board = new Board();
        board.setDefaultPosition();

        Move validMove = new Move(7,1,5,0,0);
        assertTrue(board.validMove(validMove));

        validMove = new Move(7,1,5,2,0);
        assertTrue(board.validMove(validMove));

        Move invalidMove = new Move(0,1,2,1,0);
        assertFalse(board.validMove(invalidMove));

        invalidMove = new Move(7,6,4,5,0);
        assertFalse(board.validMove(invalidMove));
    }

    @Test
    public void testRookMove(){
        Board board = new Board();
        board.setDefaultPosition();

        // moving pieces so that the rook has squares to move
        Move move = new Move(7,1,5,0,0);
        assertTrue(board.validMove(move));
        board = board.makeMove(move);

        move = new Move(6,3,4,3,0);
        assertTrue(board.validMove(move));
        board = board.makeMove(move);

        move = new Move(6,1,5,1,0);
        assertTrue(board.validMove(move));
        board = board.makeMove(move);

        move = new Move(7,2,5,4,0);
        assertTrue(board.validMove(move));
        board = board.makeMove(move);

        Move invalidMove = new Move(7,0,6,1,0);
        assertFalse(board.validMove(invalidMove));

        // checks moving horizontally
        Move validMove = new Move(7,0,7,2,0);
        assertTrue(board.validMove(validMove));
        board = board.makeMove(validMove);

        validMove = new Move(7,2,7,1,0);
        assertTrue(board.validMove(validMove));
        board = board.makeMove(validMove);

        // checks moving vertically
        validMove = new Move(7,1,6,1,0);
        assertTrue(board.validMove(validMove));
    }

    // the queen's moves are a combination of the rook and bishops

    @Test
    public void testKingMove(){
        Board board = new Board();
        board.setDefaultPosition();
        // moving pieces to give the king squares
        Move move = new Move(6,4,4,4,0);
        assertTrue(board.validMove(move));
        board = board.makeMove(move);

        Move validMove = new Move(7,4,6,4,0);
        assertTrue(board.validMove(validMove));
        board = board.makeMove(validMove);

        validMove = new Move(6,4,5,5,0);
        assertTrue(board.validMove(validMove));
        board = board.makeMove(validMove);

        Move invalidMove = new Move(5,5,7,4,0);
        assertFalse(board.validMove(invalidMove));
    }

    @Test
    public void testCapture(){
        Board board = new Board();
        board.setDefaultPosition();

        Move move = new Move(6,4,5,4,0);
        assertTrue(board.validMove(move));
        board = board.makeMove(move);

        move = new Move(7,5,2,0,0);
        assertTrue(board.validMove(move));
        board = board.makeMove(move);

        move = new Move(2,0,1,1,0);
        assertTrue(board.validMove(move));
        board = board.makeMove(move);

        Piece piece = board.getPiece(1,1);
        assertEquals(piece.getColour(), 0);
        assertEquals(piece.getPiece(), 3);
        assertEquals(piece.getNumMoves(), 2);
    }

    @Test
    public void testMovingThroughPieces(){
        Board board = new Board();
        board.setDefaultPosition();

        Move bishopMove = new Move(7,2,5,0,0);
        Move queenMove = new Move(7,3,0,3,0);

        assertFalse(board.validMove(bishopMove));
        assertFalse(board.validMove(queenMove));
    }

    @Test
    public void testCheck(){
        Board board = new Board();
        board.setDefaultPosition();

        // moving the light square bishop to the f7 square the put the king in check
        Move pawnMove = new Move(6,4, 5,4,0);
        assertTrue(board.validMove(pawnMove));
        board = board.makeMove(pawnMove);

        Move bishopMove = new Move(7,5,4,2,0);
        assertTrue(board.validMove(bishopMove));
        board = board.makeMove(bishopMove);

        Move checkMove = new Move(4,2,1,5,0);
        assertTrue(board.validMove(checkMove));
        board = board.makeMove(checkMove);

        assertTrue(board.inCheck(1));
    }
}