import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChessBoard {
    private JPanel[][] squares = new JPanel[8][8];
    private JPanel chessboard = new JPanel(new GridLayout(8, 8));
    private Point startSquare = null;
    private Point endSquare = null;
    private Point currentSquare = null;

    public Board board;

    // the playerMove is volatile since it will update once the player has made a move on the chess board and is used
    // in the main class to determine when it is the computers move
    public volatile boolean playerMove = false;
    public int playerColour = 0;

    public static Piece emptySquare = null;


    public ChessBoard(Board board){
        this.board = board;
    }

    public void display() {
        // title of the UI windwo
        JFrame frame = new JFrame("Chess Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                JPanel square = new JPanel();
                // creates alternating white and black squares
                square.setBackground((row + col) % 2 == 0 ? Color.WHITE : Color.GRAY);
                square.setPreferredSize(new Dimension(75, 75));
                square.setLayout(new BorderLayout()); // Ensure proper layout for centering

                if (!(board.getPiece(row,col) == emptySquare)){

                    JLabel pieceLabel = getImage(board.getPiece(row,col));
                    square.add(pieceLabel, BorderLayout.CENTER);

                    square.add(pieceLabel);
                }

                int currentRow = row;
                int currentCol = col;

                // Add drag-and-drop listener on each square
                square.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {

                        // if it is not the players move they will not be able to select squares
                        if (playerMove) {

                            currentSquare = new Point(currentRow, currentCol);

                            if (startSquare == null) {
                                startSquare = currentSquare;

                            // ensures that selecting the same square twice will unselect it
                            } else if (startSquare.equals(currentSquare)) {
                                startSquare = null;

                            } else {
                                endSquare = currentSquare;

                                Move move = new Move(startSquare.x, startSquare.y, endSquare.x, endSquare.y, 0);

                                // ensures that a move is valid and the player is moving a piece of its own colour before
                                // making the move
                                if (board.getPiece(startSquare.x, startSquare.y) != emptySquare &&
                                        board.getPiece(startSquare.x, startSquare.y).getColour() == playerColour
                                        && board.validMove(move)) {
                                    board = board.makeMove(move);
                                    playerMove = false;
                                }

                                updateSquares();

                                startSquare = null;
                                endSquare = null;
                            }

                            // Reset all squares back to their original color removing any old highlight
                            for (int i = 0; i < 8; i++) {
                                for (int j = 0; j < 8; j++) {
                                    squares[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.GRAY);
                                }
                            }

                            // If a square is currently selected change the background of that square to have a yellow
                            // highlight
                            if (startSquare != null) {
                                Color blendedColor = getBlendedColor();

                                squares[startSquare.x][startSquare.y].setBackground(blendedColor);
                            }

                            chessboard.repaint();
                        }
                    }
                });

                chessboard.add(square);
                squares[row][col] = square;
            }
        }

        frame.add(chessboard);
        frame.setVisible(true);
    }

    // updates the chessboard squares with the pieces in the board
    public void updateSquares(){

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                squares[row][col].removeAll();
                if (board.getPiece(row,col) != emptySquare){

                    JLabel pieceLabel = getImage(board.getPiece(row,col));
                    squares[row][col].add(pieceLabel, BorderLayout.CENTER);

                    squares[row][col].add(pieceLabel);
                }
            }
        }


        chessboard.revalidate();
        chessboard.repaint();
    }

    // returns a highlighted yellow colour of the square at the startSquare
    private Color getBlendedColor() {
        Color currentColor = squares[startSquare.x][startSquare.y].getBackground();
        int r = currentColor.getRed();
        int g = currentColor.getGreen();
        int b = currentColor.getBlue();

        Color yellow = new Color(255, 255, 0, 150); // Semi-transparent yellow
        Color blendedColor = new Color(
                (r + yellow.getRed()) / 2,
                (g + yellow.getGreen()) / 2,
                (b + yellow.getBlue()) / 2,
                currentColor.getAlpha());
        return blendedColor;
    }

    private static JLabel getImage(Piece piece){
        String imagePath = getImagePath(piece);

        ImageIcon imageIcon = new ImageIcon(imagePath);

        Image image = imageIcon.getImage();
        // ensures the image is the right size for the board
        Image scaledImage = image.getScaledInstance(75, 75, Image.SCALE_SMOOTH);

        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel pieceLabel = new JLabel(scaledIcon);

        // ensures that the image is centered on the square
        pieceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pieceLabel.setVerticalAlignment(SwingConstants.CENTER);

        return pieceLabel;
    }


    private static String getImagePath(Piece piece) {

        String path = "";

        if (piece.getColour() == 0) {
            switch (piece.getPiece()) {
                case 0 -> path = "src/pieceImages/white-pawn.png";
                case 1 -> path = "src/pieceImages/white-rook.png";
                case 2 -> path = "src/pieceImages/white-knight.png";
                case 3 -> path = "src/pieceImages/white-bishop.png";
                case 5 -> path = "src/pieceImages/white-king.png";
                case 4 -> path = "src/pieceImages/white-queen.png";
            }
        } else {
            switch (piece.getPiece()) {
                case 0 -> path = "src/pieceImages/black-pawn.png";
                case 1 -> path = "src/pieceImages/black-rook.png";
                case 2 -> path = "src/pieceImages/black-knight.png";
                case 3 -> path = "src/pieceImages/black-bishop.png";
                case 5 -> path = "src/pieceImages/black-king.png";
                case 4 -> path = "src/pieceImages/black-queen.png";
            }
        }

        return path;
    }
}
