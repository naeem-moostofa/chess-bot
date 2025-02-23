import java.util.ArrayList;

public class Main {
    public static void main(String[] args){

        ChessBoard displayBoard = new ChessBoard();
        displayBoard.setDefaultPosition();
        Board board = displayBoard.board;
        Board startingBoard = new Board();
        startingBoard.setDefaultPosition();

        int playerColour = 0;
        int currentColour  = 0;

        displayBoard.playerColour = playerColour;

        // depths can be adjusted to increase time spent on each move and accuracy
        int fullDepth = 4;
        int maxDepth = 6;

        displayBoard.display();

        PreviousPositions positions;

        long startTime;
        long endTime;

        while (true){
            Moves moves = board.getAllMoves(currentColour);
            ArrayList<Board> playedMoves = board.makeAllMoves(moves);

            playedMoves = ChessBot.removeNull(playedMoves);

            // the game is over if the current player has no moves
            if (playedMoves.isEmpty()){

                if (board.inCheck(currentColour)){
                    System.out.println("Game over, " + colourToString(nextColour(currentColour)) + " wins!");
                } else {
                    System.out.println("Game over, stalemate.");
                }

                break;
            }

            positions = new PreviousPositions(board, startingBoard);

            int index = positions.getBoardIndex(board);
            int frequency = positions.getFrequency(index);

            // the game is also over if the same position has been reached three times
            if (frequency == 3){
                System.out.println("Game over, three move repetition.");
                break;
            }


            if (playerColour == currentColour){
                displayBoard.playerMove = true;

                boolean waiting = true;
                // waits until the player has moved
                while(waiting){
                    waiting = displayBoard.playerMove;
                }

                board = displayBoard.board;

            } else {
                System.out.println("Computer's turn");
                startTime = System.currentTimeMillis();

                // gets the bot's move
                Move bestMove = ChessBot.getBestMove(board, currentColour, fullDepth, maxDepth, startingBoard);
                endTime = System.currentTimeMillis();

                System.out.println("Move Made!");
                System.out.println(bestMove);
                System.out.println("Time: " + (endTime - startTime));

                board = board.makeMove(bestMove);

                displayBoard.board = board;

                // updates the board on the UI after the bot has moved
                displayBoard.updateSquares();
            }

            currentColour = nextColour(currentColour);
        }

        // prints out all moves made in the game
        for (int i = 0; i < board.numMovesMade(); i++) {
            System.out.println(board.getMoveMade(i));
        }
    }

    // will return 1 if 0 is given and 0 if 1 is given
    public static int nextColour(int currentColour){
        return Math.abs(currentColour - 1);
    }

    public static String colourToString(int colour){
        if (colour == 0){
            return "White";
        } else {
            return "Black";
        }
    }
}
