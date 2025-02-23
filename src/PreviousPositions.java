import java.util.ArrayList;

public class PreviousPositions {
    // keeps track of positions reached and their frequency
    ArrayList<Board> positions;
    ArrayList<Integer> frequency;

    public PreviousPositions(){
        positions = new ArrayList<>();
        frequency = new ArrayList<>();
    }

    public PreviousPositions(Board b, Board startingPosition){
        positions = new ArrayList<>();
        frequency = new ArrayList<>();

        ArrayList<Board> boardsToAdd = b.getListPositionsReached(startingPosition);

        // adds positions that have been reached up to this point
        for (Board add : boardsToAdd){
            frequencyAddBoard(add);
        }
    }

    // adds the board to the positions list if it does not already contain it, otherwise adds 1 to the frequency for that
    // board
    public Integer frequencyAddBoard(Board board){

        int index = getBoardIndex(board);

        if (index == -1){
            addBoard(board);

            return positions.size() - 1;
        } else {
            increaseFrequency(index);

            return index;
        }
    }

    public int getBoardIndex(Board board){
        for (int i = 0; i < positions.size(); i++) {
            if (positions.get(i).equals(board)){
                return i;
            }
        }
        return -1;
    }



    public void addBoard(Board board){
        positions.add(board);
        frequency.add(1);
    }

    public int getFrequency(int index){
        return frequency.get(index);
    }

    public void increaseFrequency(int index){
        int currentFrequency = frequency.get(index);
        frequency.set(index, currentFrequency + 1);
    }

    // decreases the frequency by 1 for each index in indexes. if it becomes 0 that frequency and board is removed.
    public void decrementPositions(ArrayList<Integer> indexes){
        // we need to iterate backwards since they were added in a forwards direction
        for (int i = indexes.size() - 1; i >= 0; i--) {

            int index= indexes.get(i);

            int freq = frequency.get(index);

            if (freq == 1){
                positions.remove(index);
                frequency.remove(index);
            } else {
                frequency.set(index, freq - 1);
            }
        }
    }

}
