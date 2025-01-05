public class Move implements Comparable<Move>{

    public static int checkOrder = 8;
    public static int promotionOrder = 7;
    public static int pawnCaptureOrder = 6;
    public static int knightCaptureOrder = 5;
    public static int bishopCaptureOrder = 4;
    public static int rookCaptureOrder = 3;
    public static int queenCaptureOrder = 2;
    public static int kingCaptureOrder = 1;

    int startRow;
    int startCol;
    int endRow;
    int endCol;

    int order;


    public Move(int startRow, int startCol, int endRow, int endCol, int order){
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        this.order = order;
    }

    public boolean equals(Move other){
        return startRow == other.startRow && startCol == other.startCol
                && endRow == other.endRow && endCol == other.endCol;
    }

    public int compareTo(Move other){
        return order - other.order;
    }

    @Override
    public String toString(){
        return "(" + startRow + ", " + startCol + ") to " + " (" + endRow + ", " + endCol + ")";
    }
}
