import java.util.LinkedList;
import java.util.Scanner;

//This program plays tic-tac game using min-max algorithm
public class TicTac {
    private final char EMPTY = ' '; // empty slot
    private final char COMPUTER = 'X'; // computer
    private final char PLAYER = 'O'; // player
    private final int MIN = 0; // min level
    private final int MAX = 1; // max level

    // Board class (inner class)
    private class Board {
        private char[][] array; // board array

        // Constructor of Board class
        private Board(int size) {
            array = new char[size][size]; // create array

            for (int i = 0; i < size; i++) // fill array with empty slots
                for (int j = 0; j < size; j++)
                    array[i][j] = EMPTY;
        }
    }

    private Board board; // game board
    private int size; // size of board
    private int d; // depth
    private int alpha = Integer.MIN_VALUE;
    private int beta = Integer.MAX_VALUE;

    // Constructor of TicTac class
    public TicTac(int size, int d) {
        this.board = new Board(size); // create game board
        this.size = size; // set board size
        this.d = d;
    }

    // Method plays game
    public void play() {
        System.out.println("Human makes the first move");
        while (true) // computer and player take turns
        {
            board = playerMove(board); // player makes a move

            if (playerWin(board)) // if player wins then game is over
            {
                System.out.println("Player wins");
                break;
            }

            if (draw(board)) // if draw then game is over
            {
                System.out.println("Draw");
                break;
            }

            board = computerMove(board); // computer makes a move

            if (computerWin(board)) // if computer wins then game is over
            {
                System.out.println("Computer wins");
                break;
            }

            if (draw(board)) // if draw then game is over
            {
                System.out.println("Draw");
                break;
            }
        }
    }

    // Method lets the player make a move
    private Board playerMove(Board board) {
        System.out.print("Player move: "); // prompt player

        Scanner scanner = new Scanner(System.in); // read player's move
        int i = scanner.nextInt();
        int j = scanner.nextInt();

        board.array[i][j] = PLAYER; // place player symbol

        displayBoard(board); // diplay board

        System.out.println("Player placed a O at " + i + " " + j + "\n");

        return board; // return updated board
    }

    // Method determines computer's move
    private Board computerMove(Board board) { // generate children of board
        LinkedList<Board> children = generate(board, COMPUTER);

        int maxIndex = -1;
        int maxValue = Integer.MIN_VALUE;
        // find the child with
        for (int i = 0; i < children.size(); i++) // largest minmax value
        {
            int currentValue = minmax(children.get(i), d, alpha, beta, MIN);
            if (currentValue > maxValue) {
                maxIndex = i;
                maxValue = currentValue;
            }
        }

        Board result = children.get(maxIndex); // choose the child as next move

        System.out.println("Computer move:");

        displayBoard(result); // print next move

        return result; // return updated board
    }

    // Method computes minmax value of a board
    private int minmax(Board board, int level, int depth, int alpha, int beta) {
        if (depth >= d)
            return 0; // TODO: CALCUALTE UTILITY VALUE
        else if (computerWin(board)) // utility is 1 if computer wins
            return 1;
        else if (playerWin(board)) // utility is -1 if player wins
            return -1;
        else if (draw(board)) // utility is 0 if draw
            return 0;
        else {
            
            if (level == MAX) // if board is at max level
            {
                LinkedList<Board> children = generate(board, COMPUTER);
                // generate children of board
                int maxValue = Integer.MIN_VALUE;
                // find maximum of minmax value of children
                for (int i = 0; i < children.size(); i++) {
                    int currentValue = minmax(children.get(i), depth-1, alpha, beta, MIN);

                    if (currentValue > maxValue)
                        maxValue = currentValue;
                }

                return maxValue; // return maximum minmax value
            } else // if board is at min level
            {

                LinkedList<Board> children = generate(board, PLAYER);
                // generate children of board
                int minValue = Integer.MAX_VALUE;
                // find minimum of minmax values of children
                for (int i = 0; i < children.size(); i++) {
                    int currentValue = minmax(children.get(i), depth-1, alpha, beta, MAX);

                    if (currentValue < minValue)
                        minValue = currentValue;
                }

                return minValue; // return minimum minmax value
            }
        }
    }

    // Method generates children of board using a symbol
    private LinkedList<Board> generate(Board board, char symbol) {
        LinkedList<Board> children = new LinkedList<Board>();
        // empty list of children
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) // go thru board
                if (board.array[i][j] == EMPTY) { // if slot is empty
                    Board child = copy(board); // put the symbol and
                    child.array[i][j] = symbol; // create child board
                    children.addLast(child);
                }

        return children; // return list of children
    }

    /*p is the number of two
    consecutive pieces and q is the number of three consecutive pieces. In other words, a player gets
    2 points for two consecutive pieces and 3 points for three consecutive pieces. Overlaps are counted */

    // TODO: calculateUtilityValue
    private int calculateUtilityValue(Board board, char symbol) {

        int i = 0, j = 0, k = 0;

        for(i=0;i<board.array.length;i++) {

        }

    }

    // Method checks whether computer wins
    private boolean computerWin(Board board) {
        return check(board, COMPUTER); // check computer wins
    } // somewhere in board

    // Method checks whether player wins
    private boolean playerWin(Board board) {
        return check(board, PLAYER); // check player wins
    } // somewhere in board

    // Method checks whether board is draw
    private boolean draw(Board board) {
        // check board is full and neither computer nor player win
        return full(board) && !computerWin(board) && !playerWin(board);
    }

    // Method checks whether row, column, or diagonal is occupied
    // by a symbol
    private boolean check(Board board, char symbol) {
        for (int i = 0; i < size; i++) // check each row
            if (checkRow(board, i, symbol))
                return true;

        for (int i = 0; i < size; i++) // check each column
            if (checkColumn(board, i, symbol))
                return true;

        if (checkLeftDiagonal(board, symbol)) // check left diagonal
            return true;

        if (checkRightDiagonal(board, symbol)) // check right diagonal
            return true;

        return false;
    }

    // Method checks whether a row is occupied by a symbol
    private boolean checkRow(Board board, int i, char symbol) {
        for (int j = 0; j < size; j++)
            if (board.array[i][j] != symbol)
                return false;

        return true;
    }

    // Method checks whether a column is occupied by a symbol
    private boolean checkColumn(Board board, int i, char symbol) {
        for (int j = 0; j < size; j++)
            if (board.array[j][i] != symbol)
                return false;

        return true;
    }

    // Method checks whether left diagonal is occupied a symbol
    private boolean checkLeftDiagonal(Board board, char symbol) {
        for (int i = 0; i < size; i++)
            if (board.array[i][i] != symbol)
                return false;

        return true;
    }

    // Method checks whether right diagonal is occupied by a symbol
    private boolean checkRightDiagonal(Board board, char symbol) {
        for (int i = 0; i < size; i++)
            if (board.array[i][size - 1 - i] != symbol)
                return false;

        return true;
    }

    // Method checks whether a board is full
    private boolean full(Board board) {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (board.array[i][j] == EMPTY)
                    return false;

        return true;
    }

    // Method makes copy of a board
    private Board copy(Board board) {
        Board result = new Board(size);

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                result.array[i][j] = board.array[i][j];

        return result;
    }

    // Method displays a board
    private void displayBoard(Board board) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++)
                System.out.print(board.array[i][j]);
            System.out.println();
        }
    }
}
