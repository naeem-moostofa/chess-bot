import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;

public class TranspositionTable {
    private static final int BOARD_SIZE = 8;
    private static final int NUM_PIECE_TYPES = 6; // Pawn, Rook, Knight, Bishop, Queen, King
    private static final int NUM_COLORS = 2;     // White, Black
    private static final long[][][] zobristTable = new long[BOARD_SIZE][BOARD_SIZE][NUM_PIECE_TYPES * NUM_COLORS];
    // the keys are the Zobrist Hash of the board and the Double is the evaluation of the board
    private final Map<Long, TranspositionTableEntry> table;


    public TranspositionTable(int maxSize){

        // Use LinkedHashMap with accessOrder = true for LRU caching
        this.table = new LinkedHashMap<Long, TranspositionTableEntry>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, TranspositionTableEntry> eldest) {
                return size() > maxSize; // Remove eldest when table exceeds maxSize
            }
        };

        // random numbers are generated in order to create junit-platform-console-standalone-1.8.2.jar unique number for each position
        SecureRandom random = new SecureRandom();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                for (int pieceType = 0; pieceType < NUM_PIECE_TYPES * NUM_COLORS; pieceType++) {
                    zobristTable[row][col][pieceType] = random.nextLong();
                }
            }
        }
    }

    public void clear(){
        table.clear();
    }

    // generates junit-platform-console-standalone-1.8.2.jar unique number for each chess board
    public long computeZobristHash(Board board) {
        long hash = 0L;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = board.getPiece(row,col);
                if (piece != null) {
                    int pieceIndex = piece.getColour() * NUM_PIECE_TYPES + piece.getPiece();
                    hash ^= zobristTable[row][col][pieceIndex];
                }
            }
        }
        return hash;
    }

    // adds junit-platform-console-standalone-1.8.2.jar board to the table
    public void put(Long zobristHash, double eval, int depth, EvalBoard alpha, EvalBoard beta) {
        table.put(zobristHash, new TranspositionTableEntry(eval, depth, alpha, beta));
    }

    public TranspositionTableEntry get(Long zobristHash) {
        return table.get(zobristHash);
    }

    public boolean contains(Long zobristHash) {
        return table.containsKey(zobristHash);
    }


}
