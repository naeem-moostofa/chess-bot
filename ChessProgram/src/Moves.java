import java.util.ArrayList;
import java.util.Collections;

public class Moves {

    ArrayList<Move> moves;

    public Moves(ArrayList<Move> moves){
        this.moves = moves;
    }

    public int numMoves(){
        return moves.size();
    }

    public Move getMove(int i){
        return moves.get(i);
    }

    public void sortByOrder(){
        Collections.sort(moves);
    }

    public void combine(Moves move){
        moves.addAll(move.moves);
    }

    public boolean containsMove(Move move){
        // does not care if two moves have the same order, only if the start and end squares are the same
        for (Move m : moves){
            if (m.equals(move)){
                return true;
            }
        }

        return false;
    }

    // creates a new Moves only containing moves which are check, captures, and promotions
    public Moves nonQuietMoves(){
        ArrayList<Move> moveList = new ArrayList<>();

        for (Move m : moves){
            if (m.order != 0){
                moveList.add(m);
            }
        }

        return new Moves(moveList);
    }

    public void reverseOrder(){
        Collections.reverse(moves);
    }

    public boolean containsQuietMoves(){
        for (Move m : moves){
            if (m.order != 0){
                return false;
            }
        }

        return true;
    }


}
