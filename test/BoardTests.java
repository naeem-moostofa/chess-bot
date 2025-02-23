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

    @Test
    public void testCheck(){
        Board board = new Board();
        board.setDefaultPosition();

        // moving the light square bishop to the f7 square the put the king in check
        Move pawnMove = new Move(6,4, 5,4,0);
        board.makeMove(pawnMove);
        Move queenMove = new Move()

    }
}