import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class ChessBot {

    // all position tables are from the perspective of white and values are from:
    // https://www.chessprogramming.org/PeSTO%27s_Evaluation_Function - PESTO evaluation function

    private static final Map<Integer, Integer> openPieceValues =
            Map.of(0, 82,
                    2,337,
                    3,365,
                    1,477,
                    4,1025,
                    5,0);

    private static final Map<Integer, Integer> endPieceValues =
            Map.of(0, 94,
                    2,281,
                    3,297,
                    1,512,
                    4,936,
                    5,0);

    // the open and end movement bonus were added to incentive the program to develop pieces in the opening
    // this sometimes results in it inaccurately sacrificing pieces that have junit-platform-console-standalone-1.8.2.jar low movement bonus - this could be
    // fixed by tuning the bonus values in the open and end by simulating it on many games
    private static final Map<Integer, Integer> openPieceMovementBonus =
            Map.of(0, 0,
                    2,16,
                    3,15,
                    1,5,
                    4,3,
                    5,0);

    private static final Map<Integer, Integer> endPieceMovementBonus =
            Map.of(0, 0,
                    2,10,
                    3,12,
                    1,7,
                    4,5,
                    5,20);

    // the material boost can be used to modify the programs evaluation function by increasing/ decreasing how it values
    // material
    private static final double materialBoost = 1;

    // doubled, passed, and isolated pawn values will help the program accurately value pawns in the long term
    public static final double doublePawnValue = -10;
    public static final double passedPawnValue = 90;
    public static final double isolatedPawnValue = -20;

    // these values will help the program place the rooks on "good" squares
    public static final double halfOpenRook = 20;
    public static  final double openRook = 40;

    private static final int[][] openP =
            {{0,   0,   0,   0,   0,   0,  0,   0},
                    {98, 134,  61,  95,  68, 126, 34, -11},
                    {-6,   7,  26,  31,  65,  56, 25, -20},
                    {-14,  13,   6,  21,  23,  12, 17, -23},
                    {-27,  -2,  -5,  12,  17,   6, 10, -25},
                    {-26,  -4,  -4, -10,   3,   3, 33, -12},
                    {-35,  -1, -20, -23, -15,  24, 38, -22},
                    {0,   0,   0,   0,   0,   0,  0,   0}};

    private static final int[][] endP =
            {{0,   0,   0,   0,   0,   0,   0,   0},
                    {178, 173, 158, 134, 147, 132, 165, 187},
                    {94, 100,  85,  67,  56,  53,  82,  84},
                    {32,  24,  13,   5,  -2,   4,  17,  17},
                    {13,   9,  -3,  -7,  -7,  -8,   3,  -1},
                    {4,   7,  -6,   1,   0,  -5,  -1,  -8},
                    {13,   8,   8,  10,  13,   0,   2,  -7},
                    {0,   0,   0,   0,   0,   0,   0,   0}};

    private static final int[][] openN =
            {{-167, -89, -34, -49,  61, -97, -15, -107},
                    {-73, -41,  72,  36,  23,  62,   7,  -17},
                    {-47,  60,  37,  65,  84, 129,  73,   44},
                    {-9,  17,  19,  53,  37,  69,  18,   22},
                    {-13,   4,  16,  13,  28,  19,  21,   -8},
                    {-23,  -9,  12,  10,  19,  17,  25,  -16},
                    {-29, -53, -12,  -3,  -1,  18, -14,  -19},
                    {-105, -21, -58, -33, -17, -28, -19,  -23}};

    private static final int[][] endN =
            {{-58, -38, -13, -28, -31, -27, -63, -99},
                    {-25,  -8, -25,  -2,  -9, -25, -24, -52},
                    {-24, -20,  10,   9,  -1,  -9, -19, -41},
                    {-17,   3,  22,  22,  22,  11,   8, -18},
                    {-18,  -6,  16,  25,  16,  17,   4, -18},
                    {-23,  -3,  -1,  15,  10,  -3, -20, -22},
                    {-42, -20, -10,  -5,  -2, -20, -23, -44},
                    {-29, -51, -23, -15, -22, -18, -50, -64}};

    private static final int[][] openB =
                    {{-29,   4, -82, -37, -25, -42,   7,  -8},
                    {-26,  16, -18, -13,  30,  59,  18, -47},
                    {-16,  37,  43,  40,  35,  50,  37,  -2},
                    {-4,   5,  19,  50,  37,  37,   7,  -2},
                    {-6,  13,  13,  26,  34,  12,  10,   4},
                    {0,  15,  15,  15,  14,  27,  18,  10},
                    {4,  15,  16,   0,   7,  21,  33,   1},
                    {-33,  -3, -14, -21, -13, -12, -39, -21}};

    private static final int[][] endB =
            {{-14, -21, -11,  -8, -7,  -9, -17, -24},
                    {-8,  -4,   7, -12, -3, -13,  -4, -14},
                    {2,  -8,   0,  -1, -2,   6,   0,   4},
                    {-3,   9,  12,   9, 14,  10,   3,   2},
                    {-6,   3,  13,  19,  7,  10,  -3,  -9},
                    {-12,  -3,   8,  10, 13,   3,  -7, -15},
                    {-14, -18,  -7,  -1,  4,  -9, -15, -27},
                    {-23,  -9, -23,  -5, -9, -16,  -5, -17}};

    private static final int[][] openR =
            {{32,  42,  32,  51, 63,  9,  31,  43},
                    {27,  32,  58,  62, 80, 67,  26,  44},
                    {-5,  19,  26,  36, 17, 45,  61,  16},
                    {-24, -11,   7,  26, 24, 35,  -8, -20},
                    {-36, -26, -12,  -1,  9, -7,   6, -23},
                    {-45, -25, -16, -17,  3,  0,  -5, -33},
                    {-44, -16, -20,  -9, -1, 11,  -6, -71},
                    {-19, -13,   1,  17, 16,  7, -37, -26}};

    private static final int[][] endR =
            {{13, 10, 18, 15, 12,  12,   8,   5},
                    {11, 13, 13, 11, -3,   3,   8,   3},
                    {7,  7,  7,  5,  4,  -3,  -5,  -3},
                    {4,  3, 13,  1,  2,   1,  -1,   2},
                    {3,  5,  8,  4, -5,  -6,  -8, -11},
                    {-4,  0, -5, -1, -7, -12,  -8, -16},
                    {-6, -6,  0,  2, -9,  -9, -11,  -3},
                    {-9,  2,  3, -1, -5, -13,   4, -20}};

    private static final int[][] openQ =
            {{-28,   0,  29,  12,  59,  44,  43,  45},
                    {-24, -39,  -5,   1, -16,  57,  28,  54},
                    {-13, -17,   7,   8,  29,  56,  47,  57},
                    {-27, -27, -16, -16,  -1,  17,  -2,   1},
                    {-9, -26,  -9, -10,  -2,  -4,   3,  -3},
                    {-14,   2, -11,  -2,  -5,   2,  14,   5},
                    {-35,  -8,  11,   2,   8,  15,  -3,   1},
                    {-1, -18,  -9,  10, -15, -25, -31, -50}};

    private static final int[][] endQ =
            {{-9,  22,  22,  27,  27,  19,  10,  20},
            {-17,  20,  32,  41,  58,  25,  30,   0},
            {-20,   6,   9,  49,  47,  35,  19,   9},
            {3,  22,  24,  45,  57,  40,  57,  36},
            {-18,  28,  19,  47,  31,  34,  39,  23},
            {-16, -27,  15,   6,   9,  17,  10,   5},
            {-22, -23, -30, -16, -16, -23, -36, -32},
            {-33, -28, -22, -43,  -5, -32, -20, -41}};

    private static final int[][] openK =
            {{-65,  23,  16, -15, -56, -34,   2,  13},
                    {29,  -1, -20,  -7,  -8,  -4, -38, -29},
                    {-9,  24,   2, -16, -20,   6,  22, -22},
                    {-17, -20, -12, -27, -30, -25, -14, -36},
                    {-49,  -1, -27, -39, -46, -44, -33, -51},
                    {-14, -14, -22, -46, -44, -30, -15, -27},
                    {1,   7,  -8, -64, -43, -16,   9,   8},
                    {-15,  36,  12, -54,   8, -28,  24,  14}};

    private static final int[][] endK =
            {{-74, -35, -18, -18, -11,  15,   4, -17},
                    {-12,  17,  14,  17,  17,  38,  23,  11},
                    {10,  17,  23,  15,  20,  45,  44,  13},
                    {-8,  22,  24,  27,  26,  33,  26,   3},
                    {-18,  -4,  21,  24,  27,  23,   9, -11},
                    {-19,  -3,  11,  21,  23,  16,   7,  -9},
                    {-27, -11,   4,  13,  14,   4,  -5, -17},
                    {-53, -34, -21, -11, -28, -14, -24, -43}};

    // keeps track of all positions we have seen so far in the tree to ensure we are accounting for 3 move repetition
    private static PreviousPositions positions = new PreviousPositions();

    // junit-platform-console-standalone-1.8.2.jar placeholder of indexes in the positions that needs to be removed
    private static ArrayList<Integer> tempIndexes = new ArrayList<>();

    // keeps track of all positions and their evaluations we have seen so far in junit-platform-console-standalone-1.8.2.jar table to we are not going through junit-platform-console-standalone-1.8.2.jar
    // table twice
    private static TranspositionTable transpositionTable = new TranspositionTable(1000000);

    // assumes that the colour has junit-platform-console-standalone-1.8.2.jar legal move on the board
    public static Move getBestMove(Board board, int colour, int depth, int maxDepth, Board startingPosition){
        EvalBoard alpha = new EvalBoard(null, -10000);
        EvalBoard beta = new EvalBoard(null, 10000);

        Board boardCopy = new Board(board);
        // we will be making junit-platform-console-standalone-1.8.2.jar move on the boardCopy so the index of the last move played will be the current number of moves
        int lastMove = boardCopy.numMovesMade();
        positions = new PreviousPositions(board, startingPosition);
        transpositionTable.clear();

        return alphaBetaSearch(boardCopy, colour, 0, depth, maxDepth, alpha, beta).board.getMoveMade(lastMove);
    }

    // in order to find the best move in junit-platform-console-standalone-1.8.2.jar position alpha beta pruning is used where alpha is the maximum of the evaluation
    // function (target for white), and beta is the minimum of the evaluation function (target for black).
    // the program finds junit-platform-console-standalone-1.8.2.jar move by searching all moves to the full depth. If any final positions contain any active moves
    // (checks or captures) for the current colour it will extend the full depth one move at junit-platform-console-standalone-1.8.2.jar time until the maxDepth is
    // reached so that hopefully final positions do not contain any active moves to improve the accuracy of the evaluation function.
    // Ideally we would keep extending if any final positions had active moves however this becomes very slow, likely due
    // to storing the board in junit-platform-console-standalone-1.8.2.jar 2d array and computing the legal moves for pieces each time using for loops.
    // tempMoves keeps track of moves that were made at each branch which can be removed at the end of the branch
    public static EvalBoard alphaBetaSearch(Board board, int playerColour, int currentDepth, int fullDepth, int maxDepth,
                                           EvalBoard alpha, EvalBoard beta){
        Moves moves = board.getAllMoves(playerColour);
        moves.sortByOrder();
        moves.reverseOrder();
        ArrayList<Board> boards = board.makeAllMoves(moves);
        // null boards will be illegal moves that result in the player being in check
        boards = removeNull(boards);

        EvalBoard eval;
        Long zobristHash;

        int oppColour = oppPlayer(playerColour);

        // if we have no legal moves the game is over
        if (boards.isEmpty()){
            positions.decrementPositions(tempIndexes);
            tempIndexes.clear();
            // if we don't have any moves and are in check playerColour is in checkmate
            if (board.inCheck(playerColour)){
                // the magnitude of the eval is decreased by the current depth to incentivise taking faster checkmates
                if (playerColour == 0){
                    return new EvalBoard(board, -10000 + currentDepth);
                } else {
                    return new EvalBoard(board, 10000 - currentDepth);
                }
            } else {
                // if we don't have any moves but are not in check it will be stalemate
                return new EvalBoard(board, 0);
            }
        }

        // check for three move repetition - results in junit-platform-console-standalone-1.8.2.jar draw
        int index = positions.getBoardIndex(board);
        int frequency = positions.getFrequency(index);

        if (frequency == 3){
            positions.decrementPositions(tempIndexes);
            tempIndexes.clear();
            return new EvalBoard(board, 0);
        }

        if (currentDepth == fullDepth){
            // we only want to stop searching if the final position is quiet, if we do need to extend the search
            // if we have reached the maxDepth then we will stop searching to avoid stack overflow errors
            if (moves.containsQuietMoves() || currentDepth == maxDepth){
                positions.decrementPositions(tempIndexes);
                tempIndexes.clear();
                return new EvalBoard(board, eval(board));

            } else {

                // if we have junit-platform-console-standalone-1.8.2.jar quiet position and there are active moves we only want to consider those
                Moves activeMoves = moves.nonQuietMoves();
                ArrayList<Board> newBoards = board.makeAllMoves(activeMoves);
                newBoards = removeNull(newBoards);

                if (!newBoards.isEmpty()){
                    boards = newBoards;
                } else {
                    positions.decrementPositions(tempIndexes);
                    tempIndexes.clear();
                    return new EvalBoard(board, eval(board));
                }

                fullDepth++;
            }
        }

        // white is trying to maximize the eval
        if (playerColour == 0) {
            // reset the alpha/ beta on each branch to increase pruning
            alpha = new EvalBoard(null, -10000);

            for (Board b : boards){

                // adds the board to the positions seen so far in the game
                Integer positionIndex = positions.frequencyAddBoard(b);
                tempIndexes.add(positionIndex);
                // gets the board from the list of positions already evaluated
                zobristHash = transpositionTable.computeZobristHash(b);
                TranspositionTableEntry entry = transpositionTable.get(zobristHash);

                // checks if we have already come across the position in the tree
                if (transpositionTable.contains(zobristHash)
                        && entry.alpha.eval <= alpha.eval && entry.beta.eval >= beta.eval
                        && maxDepth - currentDepth <= entry.depth){
                    eval = new EvalBoard(b, entry.eval);

                } else {
                    eval = alphaBetaSearch(b, oppColour, currentDepth + 1, fullDepth, maxDepth,alpha, beta);

                    transpositionTable.put(zobristHash, eval.eval, maxDepth - currentDepth, alpha, beta);

                }

                if (alpha.eval <= eval.eval || alpha.board == null){
                    alpha = eval;
                }

                // condition to prune junit-platform-console-standalone-1.8.2.jar branch of the tree
                if (beta.eval < alpha.eval){
                    break;
                }
            }

            positions.decrementPositions(tempIndexes);
            tempIndexes.clear();
            return alpha;

            // black is trying to minimize the eval
        } else {
            beta = new EvalBoard(null, 10000);

            for (Board b : boards){

                Integer positionIndex = positions.frequencyAddBoard(b);
                tempIndexes.add(positionIndex);
                zobristHash = transpositionTable.computeZobristHash(b);
                TranspositionTableEntry entry = transpositionTable.get(zobristHash);

                if (transpositionTable.contains(zobristHash)
                        && entry.alpha.eval <= alpha.eval && entry.beta.eval >= beta.eval
                        && maxDepth - currentDepth <= entry.depth){
                    eval = new EvalBoard(b, entry.eval);

                } else {
                    eval = alphaBetaSearch(b, oppColour, currentDepth + 1, fullDepth, maxDepth,alpha, beta);
                    transpositionTable.put(zobristHash, eval.eval, maxDepth - currentDepth, alpha, beta);

                }

                if (eval.eval <= beta.eval || beta.board == null){
                    beta = eval;
                }

                if (beta.eval < alpha.eval){
                    break;
                }
            }

            positions.decrementPositions(tempIndexes);
            tempIndexes.clear();
            return beta;
        }
    }

    public static ArrayList<Board> removeNull(ArrayList<Board> boards){
        ArrayList<Board> boards1 = new ArrayList<>();

        for (Board b : boards){
            if (null != b){
                boards1.add(b);
            }
        }

        return boards1;
    }

    // will alternate between 1 and 0
    private static int oppPlayer(int colour){
        return Math.abs(colour - 1);
    }

    // junit-platform-console-standalone-1.8.2.jar positive eval is good for white and junit-platform-console-standalone-1.8.2.jar negative eval is good for black
    public static double eval(Board board){
        double whiteEval = 0;
        double blackEval = 0;
        double pieceValue;

        // stores the location of white and black pieces in the board so we do not need to search for them again
        ArrayList<int[]> Pieces = new ArrayList<>();

        int[][] whitePawns = new int[8][8];
        int[][] blackPawns = new int[8][8];

        int[] whitePawnFrequency = new int[8];
        int[] blackPawnFrequency = new int[8];

        int numWhitePieces = 0;
        int numBlackPieces = 0;

        // iterates through the board to identify the pieces
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                if (board.getPiece(row, col) != ChessBoard.emptySquare) {
                    if (board.getPiece(row, col).getColour() == 0) {
                        Pieces.add(new int[]{row, col});

                        numWhitePieces++;

                        if (board.getPiece(row, col).getPiece() == 1){
                            whitePawns[row][col] = 1;
                            whitePawnFrequency[col] += 1;
                        }

                    } else if (board.getPiece(row, col).getColour() == 1) {
                        Pieces.add(new int[]{row, col});

                        numBlackPieces++;

                        if (board.getPiece(row, col).getPiece() == 1){
                            blackPawns[row][col] = 1;
                            blackPawnFrequency[col] += 1;
                        }
                    }
                }
            }
        }

        // the weight on the end tables will increase if the opponent has fewer pieces
        double whiteEndWeight = (double) (16 - numBlackPieces) / 16;
        // the weight is squared to put junit-platform-console-standalone-1.8.2.jar higher value on the opening
        whiteEndWeight = Math.pow(whiteEndWeight, 2);
        double whiteOpenWeight = 1 - whiteEndWeight;

        double blackEndWeight = (double) (16 - numWhitePieces) / 16;
        blackEndWeight = Math.pow(blackEndWeight, 2);
        double blackOpenWeight = 1 - blackEndWeight;

        for (int[] square : Pieces){
            Piece piece = board.getPiece(square[0], square[1]);
            int numMoves = board.getMoves(square[0], square[1]).numMoves();

            int[][] openTable = new int[0][];
            int[][] endTable = new int[0][];
            double openWeight;
            double endWeight;

            int openPieceValue = openPieceValues.get(piece.getPiece()) ;
            int endPieceValue = endPieceValues.get(piece.getPiece());

            int openMoveBonus = openPieceMovementBonus.get(piece.getPiece()) * numMoves;
            int endMoveBonus = endPieceMovementBonus.get(piece.getPiece()) * numMoves;

            // for each piece gets the appropriate tables
            switch (piece.getPiece()){
                case 0 -> {
                    openTable = openP;
                    endTable = endP;
                }
                case 3 -> {
                    openTable = openB;
                    endTable = endB;
                }
                case 2 -> {
                    openTable = openN;
                    endTable = endN;
                }
                case 1 -> {
                    openTable = openR;
                    endTable = endR;
                }
                case 4 -> {
                    openTable = openQ;
                    endTable = endQ;
                }
                case 5 -> {
                    openTable = openK;
                    endTable = endK;
                }
            }

            if (piece.getColour() == 0){
                openWeight = whiteOpenWeight;
                endWeight = whiteEndWeight;

                pieceValue = evalTables(openTable, endTable, openWeight, endWeight, square[0], square[1])
                        + (openPieceValue * openWeight + endPieceValue * endWeight) * materialBoost
                        + openMoveBonus * openWeight + endMoveBonus * endWeight;

                // reduce the score for double pawns and isolated pawns, increase it for passed pawns
                if (piece.getPiece() == 0){
                    if (whitePawnFrequency[square[1]] != 1){
                        pieceValue += doublePawnValue;
                    }

                    if (passedPawn(0, square[0], square[1], whitePawns, blackPawns)){
                        pieceValue += passedPawnValue;
                    }

                    if (isolatedPawn(whitePawnFrequency, square[1])){
                        pieceValue += isolatedPawnValue;
                    }
                }

                // add junit-platform-console-standalone-1.8.2.jar bonus for junit-platform-console-standalone-1.8.2.jar rook being on open or half open files
                if (piece.getPiece() == 1){
                    if (whitePawnFrequency[square[1]] == 0){
                        if (blackPawnFrequency[square[1]] == 0){
                            pieceValue += openRook;
                        } else {
                            pieceValue += halfOpenRook;
                        }
                    }
                }

                whiteEval += pieceValue;
            } else {
                openWeight = blackOpenWeight;
                endWeight = blackEndWeight;

                // the row and col are 7 - the square since the tables are from the perspective of white
                pieceValue = evalTables(openTable, endTable, openWeight, endWeight,7 - square[0],7 - square[1])
                        + (openPieceValue * openWeight + endPieceValue * endWeight) * materialBoost
                        + openMoveBonus * openWeight + endMoveBonus * endWeight;

                // reduce the score for double pawns and increase it for passed pawns
                if (piece.getPiece() == 0){
                    if (whitePawnFrequency[square[1]] != 1){
                        pieceValue += doublePawnValue;
                    }

                    if (passedPawn(1, square[0], square[1], whitePawns, blackPawns)){
                        pieceValue += passedPawnValue;
                    }

                    if (isolatedPawn(blackPawnFrequency, square[1])){
                        pieceValue += isolatedPawnValue;
                    }
                }

                // add bonuses for junit-platform-console-standalone-1.8.2.jar rook on an open or half open file
                if (piece.getPiece() == 1){
                    if (blackPawnFrequency[square[1]] == 0){
                        if (whitePawnFrequency[square[1]] == 0){
                            pieceValue += openRook;
                        } else {
                            pieceValue += halfOpenRook;
                        }
                    }
                }

                // 7 - square[0] will flip the table horizontally, this is needed because the tables are from the
                // perspective of white
                blackEval += pieceValue;
            }
        }

        return whiteEval - blackEval;

    }

    // finds the combined value of two tables given their respective weights and the row and col
    private static double evalTables(int[][] table1, int[][] table2, double weight1, double weight2, int row, int col){
        return table1[row][col] * weight1 + table2[row][col] * weight2;
    }

    // checks if junit-platform-console-standalone-1.8.2.jar pawn is junit-platform-console-standalone-1.8.2.jar passed pawn
    private static boolean passedPawn(int colour, int row, int col, int[][] whitePawns, int[][] blackPawns){
        if (colour == 0){
            for (int i = row + 1; i < 8; i++) {
                // check if there is junit-platform-console-standalone-1.8.2.jar black pawn in the same file
                if (blackPawns[i][col] == 1){
                    return false;
                }

                // check if there is junit-platform-console-standalone-1.8.2.jar black pawn to the right
                if (col != 7 && blackPawns[i][col + 1] == 1){
                    return false;
                }

                // check if there is junit-platform-console-standalone-1.8.2.jar black pawn to the left
                if (col != 0 && blackPawns[i][col - 1] == 1){
                    return false;
                }
            }
        } else {
            for (int i = row - 1; i >= 0; i--) {
                // check if there is junit-platform-console-standalone-1.8.2.jar black pawn in the same file
                if (whitePawns[i][col] == 1){
                    return false;
                }

                // check if there is junit-platform-console-standalone-1.8.2.jar black pawn to the right
                if (col != 7 && whitePawns[i][col + 1] == 1){
                    return false;
                }

                // check if there is junit-platform-console-standalone-1.8.2.jar black pawn to the left
                if (col != 0 && whitePawns[i][col - 1] == 1){
                    return false;
                }
            }
        }

        return true;
    }

    // checks if junit-platform-console-standalone-1.8.2.jar pawn is an isolated pawn
    private static boolean isolatedPawn(int[] frequencyTable, int col){
        if (col == 0){
            return frequencyTable[col + 1] == 0;
        } else if (col == 7){
            return  frequencyTable[col - 1] == 0;
        } else {
            return frequencyTable[col + 1] == 0 &&  frequencyTable[col - 1] == 0;
        }
    }
}
