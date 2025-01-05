import java.util.Objects;

public class Piece {
    // 0 is white and 1 is black
    private int colour;

    // 0 is a pawn
    // 1 is a rook
    // 2 is a knight
    // 3 is a bishop
    // 4 is a queen
    // 5 is a king
    private int piece;

    private int numMoves;

    private boolean captureEnPassant;

    public Piece(int colour, int piece){
        this.colour = colour;
        this.piece = piece;
        this.numMoves = 0;
        this.captureEnPassant = false;
    }

    public Piece (Piece other){
        this.colour = other.colour;
        this.piece = other.piece;
        this.captureEnPassant = other.captureEnPassant;
        this.numMoves = other.numMoves;
    }

    public void move(){
        numMoves++;
    }

    public int getColour() {
        return colour;
    }

    public int getPiece() {
        return piece;
    }

    public int getNumMoves() {
        return numMoves;
    }

    public boolean isCaptureEnPassant(){
        return captureEnPassant;
    }

    public void canCaptureEnPassant(){
        captureEnPassant = true;
    }

    public void cantCaptureEnPassant(){
        captureEnPassant = false;
    }

    public boolean notMoved(){
        return numMoves == 0;
    }

    @Override
    public String toString(){
        return "c: " + colour + ", p: " + piece + ", " + captureEnPassant;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Piece){
            return ((Piece) o).piece == (piece) && ((Piece) o).colour == colour;
        }

        return false;
    }
}
