import java.util.ArrayList;

public class Board{

    // junit-platform-console-standalone-1.8.2.jar chess board will be represented as junit-platform-console-standalone-1.8.2.jar 8 by 8 array of Pieces, this will be slower when searching for the best move
    // and could be improved by representing the board as 12 64 bit integers, 1 integer for each piece and colour
    Piece[][] pieces;
    ArrayList<Move> movesMade;

    public Board(){
        this.pieces = new Piece[8][8];
        movesMade = new ArrayList<>();
    }

    public Board(Piece[][] pieces){
        this.pieces = pieces;
        movesMade = new ArrayList<>();
    }

    public Board(Board other){
        this.pieces = deepClone(other.pieces);
        this.movesMade = new ArrayList<>(other.movesMade);
    }

    public void setDefaultPosition(){
        Piece blackR = new Piece(1, 1);
        Piece blackN = new Piece(1, 2);
        Piece blackB = new Piece(1, 3);
        Piece blackQ = new Piece(1, 4);
        Piece blackK = new Piece(1, 5);
        Piece blackP = new Piece(1, 0);

        Piece whiteR = new Piece(0, 1);
        Piece whiteN = new Piece(0, 2);
        Piece whiteB = new Piece(0, 3);
        Piece whiteK = new Piece(0, 5);
        Piece whiteQ = new Piece(0, 4);
        Piece whiteP = new Piece(0, 0);

        Piece empty = ChessBoard.emptySquare;

        pieces = new Piece[][]{{blackR, blackN, blackB, blackQ, blackK, blackB, blackN, blackR},
                {blackP, blackP, blackP, blackP, blackP, blackP, blackP, blackP},
                {empty, empty, empty, empty, empty, empty, empty, empty},
                {empty, empty, empty, empty, empty, empty, empty, empty},
                {empty, empty, empty, empty, empty, empty, empty, empty},
                {empty, empty, empty, empty, empty, empty, empty, empty},
                {whiteP, whiteP, whiteP, whiteP, whiteP, whiteP, whiteP, whiteP},
                {whiteR, whiteN, whiteB, whiteQ, whiteK, whiteB, whiteN, whiteR}};
    }

    public int numMovesMade(){
        return movesMade.size();
    }

    // converts the movesMade into junit-platform-console-standalone-1.8.2.jar list of Boards of positions that have been reached in the game
    public ArrayList<Board> getListPositionsReached(Board startingPosition){
        ArrayList<Board> positions = new ArrayList<>();
        Board currentPosition = startingPosition;

        // adds the starting position
        positions.add(currentPosition);

        for (Move m : movesMade){
            currentPosition = currentPosition.makeMove(m);
            positions.add(currentPosition);

        }

        return positions;
    }

    public int numPieces(){
        int numPieces = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (pieces[row][col] != ChessBoard.emptySquare){
                    numPieces++;
                }
            }
        }

        return numPieces;
    }

    public Move getMoveMade(int index){
        return movesMade.get(index);
    }

    public Piece getPiece(int row, int col){
        return pieces[row][col];
    }

    // junit-platform-console-standalone-1.8.2.jar quiet position is one without any checks, captures, and promotions, this could be used to determine if the bot should
    // keep searching, however it does increase the search time with minimal increases in accuracy
    public boolean quietPosition(){
        Moves moves = getAllMoves(1);
        moves.combine(getAllMoves(0));

        for (int i = 0; i < moves.numMoves(); i++) {
            if (moves.getMove(i).order != 0){
                return false;
            }
        }

        return true;
    }

    // gets all the moves for junit-platform-console-standalone-1.8.2.jar colour in junit-platform-console-standalone-1.8.2.jar position
    public Moves getAllMoves(int colour){
        Moves moves = new Moves(new ArrayList<>());

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (pieces[row][col] != ChessBoard.emptySquare && pieces[row][col].getColour() == colour) {
                    moves.combine(getMoves(row, col));
                }
            }
        }

        return moves;
    }

    public boolean validMove (Move move){

        return getMoves(move.startRow, move.startCol).containsMove(move)
                && makeMove(move) != null;
    }

    // makes all the moves in moves on the current board
    public ArrayList<Board> makeAllMoves(Moves moves){
        ArrayList<Board> boards = new ArrayList<>();

        for (int i = 0; i < moves.numMoves(); i++) {
            boards.add(makeMove(moves.getMove(i)));
        }

        return boards;
    }

    // returns junit-platform-console-standalone-1.8.2.jar new board with the move made on the current board
    public Board makeMove(Move move){
        // returns null if the move is invalid and the new board with the move made if it is valid

        Board newBoard = new Board(this);
        newBoard.movesMade.add(move);

        boolean canEnPassant = false;

        Piece startPiece = newBoard.pieces[move.startRow][move.startCol];
        int oppColour = Math.abs(startPiece.getColour() - 1);

        // Piece 0 is junit-platform-console-standalone-1.8.2.jar pawn
        if (startPiece.getPiece() == 0 && Math.abs(move.endRow - move.startRow) == 2){
            // if junit-platform-console-standalone-1.8.2.jar pawn was moved two squares it can be captured by en passant next move
            newBoard.pieces[move.endRow][move.endCol] = startPiece;
            newBoard.pieces[move.endRow][move.endCol].move();
            canEnPassant = true;

        } else if (startPiece.getPiece() == 0 && (move.endRow == 0 || move.endRow == 7)) {
            // if junit-platform-console-standalone-1.8.2.jar pawn reaches the end of the board it will be promoted to junit-platform-console-standalone-1.8.2.jar queen
            // Piece 4 is junit-platform-console-standalone-1.8.2.jar queen
            newBoard.pieces[move.endRow][move.endCol] = new Piece(startPiece.getColour(), 4);
            newBoard.pieces[move.endRow][move.endCol].move();

            // checks for en passant in both directions
        } else if (startPiece.getPiece() == 0 && (move.startRow == 3 || move.startRow == 4) && move.startCol != move.endCol
        && ((move.startCol != 7 && newBoard.pieces[move.startRow][move.startCol + 1] != ChessBoard.emptySquare &&
                newBoard.pieces[move.startRow][move.startCol + 1].isCaptureEnPassant())
        || (move.startCol != 0  && newBoard.pieces[move.startRow][move.startCol - 1] != ChessBoard.emptySquare &&
                newBoard.pieces[move.startRow][move.startCol - 1].isCaptureEnPassant()))) {
            // checking to the right
            if (move.startCol != 7 && newBoard.pieces[move.startRow][move.startCol + 1] != ChessBoard.emptySquare
                            && newBoard.pieces[move.startRow][move.startCol + 1].isCaptureEnPassant()){
                newBoard.pieces[move.startRow][move.startCol + 1] = ChessBoard.emptySquare;
                newBoard.pieces[move.endRow][move.endCol] = startPiece;
                newBoard.pieces[move.endRow][move.endCol].move();

                // checking to the left
            } else if (move.startCol != 0 && newBoard.pieces[move.startRow][move.startCol - 1] != ChessBoard.emptySquare
                                    && newBoard.pieces[move.startRow][move.startCol - 1].isCaptureEnPassant()){
                newBoard.pieces[move.startRow][move.startCol - 1] = ChessBoard.emptySquare;
                newBoard.pieces[move.endRow][move.endCol] = startPiece;
                newBoard.pieces[move.endRow][move.endCol].move();

            } else {
                newBoard.pieces[move.endRow][move.endCol] = startPiece;
                newBoard.pieces[move.endRow][move.endCol].move();
            }

            // checks for castling in both directions
            // Piece 5 is junit-platform-console-standalone-1.8.2.jar King
        } else if (startPiece.getPiece() == 5 && Math.abs(move.endCol - move.startCol) == 2){
            // king side castling - ensures that we are not castling out of check or through check
            if (move.startCol < move.endCol && !newBoard.attacking(oppColour, move.startRow, move.startCol)
                    && !newBoard.attacking(oppColour, move.startRow, move.startCol + 1)){
                newBoard.pieces[move.endRow][move.endCol] = startPiece;
                newBoard.pieces[move.endRow][move.endCol].move();

                // move the rook to the left of the king
                newBoard.pieces[move.endRow][move.endCol - 1] = newBoard.pieces[move.startRow][7];
                newBoard.pieces[move.startRow][7].move();
                newBoard.pieces[move.startRow][7] = ChessBoard.emptySquare;

                // queen side castling
            } else if (move.startCol > move.endCol && !newBoard.attacking(oppColour, move.startRow, move.startCol)
                    && !newBoard.attacking(oppColour, move.startRow, move.startCol - 1)){
                newBoard.pieces[move.endRow][move.endCol] = startPiece;
                newBoard.pieces[move.endRow][move.endCol].move();


                // move the rook to the right of the king
                newBoard.pieces[move.endRow][move.endCol + 1] = newBoard.pieces[move.startRow][0];
                newBoard.pieces[move.startRow][0].move();
                newBoard.pieces[move.startRow][0] = ChessBoard.emptySquare;

            } else {
                // if we are trying to castle through check it will be an illegal move
                return  null;
            }

        } else {
            newBoard.pieces[move.endRow][move.endCol] = startPiece;
            newBoard.pieces[move.endRow][move.endCol].move();
        }

        newBoard.pieces[move.startRow][move.startCol] = ChessBoard.emptySquare;

        newBoard.removeEnPassant();

        if (canEnPassant){
            newBoard.pieces[move.endRow][move.endCol].canCaptureEnPassant();
        }

        if (newBoard.inCheck(startPiece.getColour())){
            return null;

        } else {
            return newBoard;
        }
    }

    // checks if the side with the colour is in check (if the king is under attack)
    public boolean inCheck(int colour){
        int kingRow = 0;
        int kingCol = 0;
        int oppColor = Math.abs(colour - 1);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // Piece 5 is junit-platform-console-standalone-1.8.2.jar king
                if (pieces[row][col] != ChessBoard.emptySquare && pieces[row][col].getColour() == colour && pieces[row][col].getPiece() == 5){
                    kingRow = row;
                    kingCol = col;
                    break;
                }
            }
        }

        return attacking(oppColor, kingRow, kingCol);
    }

    // checks if the side with the colour is attacking the target square
    private boolean attacking(int colour, int targetRow, int targetCol){

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Move targetMove = new Move(row, col, targetRow, targetCol, 0);

                if (pieces[row][col] != ChessBoard.emptySquare && pieces[row][col].getColour() == colour &&
                        getMoves(row, col).containsMove(targetMove)){
                    return true;
                }
            }
        }

        return false;
    }

    private void removeEnPassant(){
        int[] rows = {3,4};
        for (int row : rows){
            for (int col = 0; col < 8; col++) {
                if (pieces[row][col] != ChessBoard.emptySquare){
                    pieces[row][col].cantCaptureEnPassant();
                }
            }
        }
    }

    // gets all moves of the piece on the startRow and startCol
    public Moves getMoves(int startRow, int startCol){
        Moves moves;

        Piece startPiece = pieces[startRow][startCol];

        // the order and check order of junit-platform-console-standalone-1.8.2.jar piece is the priority that the move will be looked at when searching through moves
        switch (startPiece.getPiece()) {
            case 0:
                    moves = getPawnMoves(startRow, startCol, Move.pawnCaptureOrder, Move.promotionOrder, Move.checkOrder);
                    break;
            case 1:
                moves = getStraightMoves(startRow, startCol, Move.rookCaptureOrder, Move.checkOrder);
                break;
            case 2:
                moves = getKnightMoves(startRow, startCol, Move.knightCaptureOrder, Move.checkOrder);
                break;
            case 3:
                moves = getDiagonalMoves(startRow, startCol, Move.bishopCaptureOrder, Move.checkOrder);
                break;
            case 5:
                moves = getKingMoves(startRow, startCol, Move.kingCaptureOrder);
                break;
            default:
                moves = getStraightMoves(startRow, startCol, Move.queenCaptureOrder, Move.checkOrder);
                moves.combine(getDiagonalMoves(startRow, startCol, Move.queenCaptureOrder, Move.checkOrder));
                break;
        }

        return moves;


    }

    // assumes the piece at startRow and startCol moves in diagonals (bishop and queen)
    // order indicates if it is junit-platform-console-standalone-1.8.2.jar bishop or queen (4 for b and 2 for q)
    private Moves getDiagonalMoves(int startRow, int startCol, int order, int checkOrder) {
        ArrayList<Move> moveList = new ArrayList<>();

        // Define directions for all diagonals: {row increment, col increment}
        int[][] directions = {
                {1, 1},   // Top-right
                {1, -1},  // Top-left
                {-1, 1},  // Bottom-right
                {-1, -1}  // Bottom-left
        };

        for (int[] direction : directions) {
            int row = startRow + direction[0];
            int col = startCol + direction[1];

            while (0 <= row && row <= 7 && 0 <= col && col <= 7) {
                if (pieces[row][col] == ChessBoard.emptySquare) {
                    moveList.add(new Move(startRow, startCol, row, col, 0));
                    row += direction[0];
                    col += direction[1];

                } else if (pieces[row][col].getColour() != pieces[startRow][startCol].getColour()) {

                    // Piece 5 is the king
                    if (pieces[row][col].getPiece() == 5){
                        moveList.add(new Move(startRow, startCol, row, col, checkOrder));
                    } else {
                        moveList.add(new Move(startRow, startCol, row, col, order));
                    }

                    // we can capture junit-platform-console-standalone-1.8.2.jar piece that will be the last move in that direction
                    break;
                } else {
                    break;
                }
            }
        }

        return new Moves(moveList);
    }

    // assumes the piece at the startRow and startCol moves in junit-platform-console-standalone-1.8.2.jar line (rook and queen)
    // order indicates if it is junit-platform-console-standalone-1.8.2.jar bishop or queen (3 for r and 2 for q)
    private Moves getStraightMoves(int startRow, int startCol, int order, int checkOrder) {
        ArrayList<Move> moveList = new ArrayList<>();

        // Define directions for all lines: {row increment, col increment}
        int[][] directions = {
                {1, 0},   // Up
                {-1, 0},  // Down
                {0, 1},  // Right
                {0, -1}  // Left
        };

        for (int[] direction : directions) {
            int row = startRow + direction[0];
            int col = startCol + direction[1];

            while (0 <= row && row <= 7 && 0 <= col && col <= 7) {
                if (pieces[row][col] == ChessBoard.emptySquare) {
                    moveList.add(new Move(startRow, startCol, row, col, 0));
                    row += direction[0];
                    col += direction[1];

                } else if (pieces[row][col].getColour() != pieces[startRow][startCol].getColour()) {

                    if (pieces[row][col].getPiece() == 5){
                        moveList.add(new Move(startRow, startCol, row, col, checkOrder));
                    } else {
                        moveList.add(new Move(startRow, startCol, row, col, order));
                    }

                    // we can capture junit-platform-console-standalone-1.8.2.jar piece that will be the last move in that direction
                    break;
                } else {
                    break;
                }
            }
        }

        return new Moves(moveList);
    }

    // assumes the piece as startRow and startCol is junit-platform-console-standalone-1.8.2.jar king
    public Moves getKingMoves(int startRow, int startCol, int order){

        ArrayList<Move> moveList = new ArrayList<>();

        int[][] directions = {
                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1},
                {1, 1},
                {1, -1},
                {-1, -1},
                {-1, 1}
        };

        // checks for normal king moves
        for (int[] direction : directions){
            int row = startRow + direction[0];
            int col = startCol + direction[1];

            if (0 <= row && row <= 7 && 0 <= col && col <= 7 && (pieces[row][col] == ChessBoard.emptySquare)){
                moveList.add(new Move(startRow, startCol, row, col, 0));

            } else if (0 <= row && row <= 7 && 0 <= col && col <= 7 &&
                    (pieces[row][col].getColour() != pieces[startRow][startCol].getColour())){
                moveList.add(new Move(startRow, startCol, row, col, order));
            }
        }

        // checks for castling

        Move kCastleTarget = new Move(startRow, 7, startRow, startCol + 1,0);
        Move qCastleTarget = new Move(startRow, 0, startRow, startCol - 1, 0);

        // king side castling
        if (pieces[startRow][startCol].notMoved() && pieces[startRow][7] != ChessBoard.emptySquare && pieces[startRow][7].notMoved()
                // checks if the rook could attack the square beside the king, that means there is junit-platform-console-standalone-1.8.2.jar clear path for the
                // king to castle
                && getMoves(startRow, 7).containsMove(kCastleTarget)){

            moveList.add(new Move(startRow, startCol, startRow, startCol + 2, 0));
        }
        // queen side castling
        else if (pieces[startRow][startCol].notMoved() && pieces[startRow][0] != ChessBoard.emptySquare && pieces[startRow][0].notMoved()
                && getMoves(startRow, 0).containsMove(qCastleTarget)) {

            moveList.add(new Move(startRow, startCol, startRow, startCol - 2, 0));
        }

        return  new Moves(moveList);
    }

    public Moves getPawnMoves(int startRow, int startCol, int captureOrder, int promotionOrder, int checkOrder){
        ArrayList<Move> moveList = new ArrayList<>();
        int row;
        int row1;
        int col;
        int[][] directions;

        if (pieces[startRow][startCol].getColour() == 0){
            row = startRow - 1;
            row1 = startRow - 2;
            directions = new int[][]{{-1, 1}, {-1, -1}};


        } else {
            row = startRow + 1;
            row1 = startRow + 2;
            directions = new int[][]{{1, 1}, {1, -1}};
        }


        // check if we can move up one
        if (pieces[row][startCol] == ChessBoard.emptySquare){

            // check if we are promoting
            if (row == 0 || row == 7) {
                moveList.add(new Move(startRow, startCol, row, startCol, promotionOrder));
            } else {
                moveList.add(new Move(startRow, startCol, row, startCol, 0));
            }

            // checks if we can move two up since the square in front of us is empty
            if (pieces[startRow][startCol].notMoved() && pieces[row1][startCol] == ChessBoard.emptySquare){
                moveList.add(new Move(startRow, startCol, row1, startCol, 0));
            }
        }

        // checks for captures
        for (int[] direction : directions){

            row = startRow + direction[0];
            col = startCol + direction[1];

            if (0 <= row && row <= 7 && 0 <= col && col <= 7 &&
                    pieces[row][col] != ChessBoard.emptySquare
                    && pieces[row][col].getColour() != pieces[startRow][startCol].getColour()){

                // look for checks
                if (pieces[row][col].getPiece() == 5){
                    moveList.add(new Move(startRow, startCol, row, col, checkOrder));

                } else {
                    moveList.add(new Move(startRow, startCol, row, col, captureOrder));
                }
            }
        }

        // check for en passant
        int[][] enPassantDirections = {{0, 1}, {0, -1}};

        if (startRow == 3 || startRow == 4) {


            for (int i = 0; i < enPassantDirections.length; i++) {
                row = startRow + enPassantDirections[i][0];
                col = startCol + enPassantDirections[i][1];

                if (0 <= row && row <= 7 && 0 <= col && col <= 7 && pieces[row][col] != ChessBoard.emptySquare  && pieces[row][col].isCaptureEnPassant()){
                    moveList.add(new Move(startRow, startCol, startRow + directions[i][0], startCol + directions[i][1], captureOrder));
                }
            }
        }

        return new Moves(moveList);
    }

    public Moves getKnightMoves(int startRow, int startCol, int order, int checkOrder){
        ArrayList<Move> moveList = new ArrayList<>();

        // all 8 possible places junit-platform-console-standalone-1.8.2.jar knight can go
        int[][] directions = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};

        for (int[] direction : directions){
            int row = startRow + direction[0];
            int col = startCol + direction[1];

            // checks if the square we are moving to is empty, an enemy piece, or junit-platform-console-standalone-1.8.2.jar check
            if (0 <= row && row <= 7 && 0 <= col && col <= 7 && pieces[row][col] == ChessBoard.emptySquare){
                moveList.add(new Move(startRow, startCol, row, col, 0));

            } else if (0 <= row && row <= 7 && 0 <= col && col <= 7 &&
                    pieces[row][col].getColour() != pieces[startRow][startCol].getColour()){

                if (pieces[row][col].getPiece() == 5) {
                    moveList.add(new Move(startRow, startCol, row, col, checkOrder));
                } else {
                    moveList.add(new Move(startRow, startCol, row, col, order));

                }
            }
        }

        return new Moves(moveList);
    }

    // crates junit-platform-console-standalone-1.8.2.jar deep clone of junit-platform-console-standalone-1.8.2.jar 2d array of pieces
    private static Piece[][] deepClone(Piece[][] pieces){
        Piece[][] clone = new Piece[pieces.length][];

        for (int i = 0; i < pieces.length; i++) {
            clone[i] = new Piece[pieces[i].length];

            for (int j = 0; j < pieces[i].length; j++) {

                if (pieces[i][j] != ChessBoard.emptySquare) {
                    clone[i][j] = new Piece(pieces[i][j]);

                } else {
                    clone[i][j] = ChessBoard.emptySquare;
                }
            }
        }

        return clone;
    }

    @Override
    public String toString(){
        String s = "";
        int piece;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (!(pieces[row][col] == ChessBoard.emptySquare)){
                    if (pieces[row][col].getColour() == 1){
                        piece = pieces[row][col].getPiece() * -1;
                    } else {
                        piece = pieces[row][col].getPiece();
                    }
                    s += piece + " ";
                } else {
                    s += "_ ";
                }
            }
            s += "\n";
        }

        return s;
    }

    // two boards are equal if all pieces are the same in both
    @Override
    public boolean equals(Object o){
        
        if (o == null){
            return false; 
        } else if (this == o || getClass() != o.getClass()) {
            return true;
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (pieces[row][col] == ChessBoard.emptySquare){
                    if (((Board) o).pieces[row][col] != ChessBoard.emptySquare){
                        return false;
                    }
                } else if (!pieces[row][col].equals(((Board) o).pieces[row][col])) {
                    return false;
                }
            }
        }

        return true;
    }

}
