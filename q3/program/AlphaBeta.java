package program;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

//This program plays tic-tac game using min-max, depth limit,
//board evaluation, and alpha-beta pruning
public class AlphaBeta
{
    private final char EMPTY = ' ';                //empty slot
    private final char COMPUTER = 'X';             //computer
    private final char PLAYER = '0';               //player
    private final int MIN = 0;                     //min level
    private final int MAX = 1;                     //max level
    private final int MAX_DEPTH = 6;                   //depth limit

    //Board class (inner class)
    private class Board
    {
        private char[][] array;                    //board array

        //Constructor of Board class
        private Board(int size)
        {
            array = new char[size][size];          //create array
                                             
            for (int i = 0; i < size; i++)         //fill array with empty slots   
                for (int j = 0; j < size; j++)
                    array[i][j] = EMPTY;
        }
    }

    private Board board;                           //game board
    private int size;                              //size of board
    private PrintWriter writer;
    
    //Constructor of AlphaBeta class
    public AlphaBeta(int size, String outputFile)
    {
        try {
            this.writer = new PrintWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.board = new Board(size);              //create game board 
        this.size = size;                          //set board size
    }

    //Method plays game
    public void play()
    {
    
        try {
        while (true)                                   //computer and player take turns
        {
            board = playerMove(board);             //player makes a move


            // Calculate and display scores after player move
            int playerScore = calculateScore(PLAYER);
            int computerScore = calculateScore(COMPUTER);
            writer.println("Player Score: " + playerScore + ", Computer Score: " + computerScore);
            System.out.println("Player Score: " + playerScore + ", Computer Score: " + computerScore);


            if (playerWin(board))                  //if player wins then game is over
            {
                
                writer.println("Player wins");
                System.out.println("Player wins");

                break;
            }

            if (draw(board))                       //if draw then game is over
            {
                writer.println("Draw");
                System.out.println("Draw");
                break;
            }

            board = computerMove(board);           //computer makes a move
            


            // Calculate and display scores after computer move
            playerScore = calculateScore(PLAYER);
            computerScore = calculateScore(COMPUTER);
            writer.println("Player Score: " + playerScore + ", Computer Score: " + computerScore);
            System.out.println("Player Score: " + playerScore + ", Computer Score: " + computerScore);

            if (computerWin(board))                //if computer wins then game is over
            {                      
                
                writer.println("Computer wins");
                System.out.println("Computer wins");
                break;
            }

            if (draw(board))                       //if draw then game is over
            {
                writer.println("Draw");
                System.out.println("Draw");
                break;
            }
            }
        }
        finally {
            if (writer != null) {
                writer.flush(); // Ensure all data is written out
                writer.close(); // Close the writer to release resources
            }
    }
}

    //Method lets the player make a move
    private Board playerMove(Board board)
    {

        writer.println();
        System.out.println();
        writer.println("Player move: ");
        System.out.print("Player move: ");         //prompt player
     
            Scanner scanner = new Scanner(System.in);
            int i = scanner.nextInt();
            int j = scanner.nextInt();

            board.array[i][j] = PLAYER;                //place player symbol
        
        displayBoard(board);                       //diplay board

        return board;                              //return updated board
    }

    //Method determines computer's move
    private Board computerMove(Board board)
    {                                              //generate children of board
        LinkedList<Board> children = generate(board, COMPUTER);

        int maxIndex = -1;
        int maxValue = Integer.MIN_VALUE;
                                                   //find the child with
        for (int i = 0; i < children.size(); i++)  //largest minmax value
        {
            int currentValue = minmax(children.get(i), MIN, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (currentValue > maxValue)
            {
                maxIndex = i;
                maxValue = currentValue;
            }
        }

        Board result = children.get(maxIndex);     //choose the child as next move
                                                   
        for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            if (board.array[i][j] != result.array[i][j])
            {
                // computer move coordinates
                writer.println("Computer move: " + i + " " + j);
                System.out.println("Computer move: " + i + " " + j); 
                break; 
            }
        }
    }


        //compare board to current board
        displayBoard(result);                      //print next move

        return result;                             //retun updated board
    }

    //Method computes minmax value of a board
    private int minmax(Board board, int level, int depth, int alpha, int beta)
    {
        if (computerWin(board) || playerWin(board) || draw(board) || depth >= MAX_DEPTH)
            return evaluate(board);                //if board is terminal or depth limit is reached
            



        else                                       //evaluate board
        {
            if (level == MAX)                      //if board is at max level     
            {
                 LinkedList<Board> children = generate(board, COMPUTER);
                                                   //generate children of board
                 int maxValue = Integer.MIN_VALUE;

                 for (int i = 0; i < children.size(); i++)
                 {                                 //find minmax values of children
                     int currentValue = minmax(children.get(i), MIN, depth+1, alpha, beta);
                                                   
                     if (currentValue > maxValue)  //find maximum of minmax values
                         maxValue = currentValue;
                                                   
                     if (maxValue >= beta)         //if maximum exceeds beta stop
                         return maxValue;
                                                   
                     if (maxValue > alpha)         //if maximum exceeds alpha update alpha
                         alpha = maxValue;
                 }

                 return maxValue;                  //return maximum value   
            }
            else                                   //if board is at min level
            {                     
                 LinkedList<Board> children = generate(board, PLAYER);
                                                   //generate children of board
                 int minValue = Integer.MAX_VALUE;

                 for (int i = 0; i < children.size(); i++)
                 {                                 //find minmax values of children
                     int currentValue = minmax(children.get(i), MAX, depth+1, alpha, beta);
                                     
                     if (currentValue < minValue)  //find minimum of minmax values
                         minValue = currentValue;
                                     
                     if (minValue <= alpha)        //if minimum is less than alpha stop
                         return minValue;
                                     
                     if (minValue < beta)          //if minimum is less than beta update beta
                         beta = minValue;
                 }

                 return minValue;                  //return minimum value 
            }
        }
    }

    //Method generates children of board using a symbol
    private LinkedList<Board> generate(Board board, char symbol)
    {
        LinkedList<Board> children = new LinkedList<Board>();
                                                   //empty list of children
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)         //go thru board
                if (board.array[i][j] == EMPTY)
                {                                  //if slot is empty
                    Board child = copy(board);     //put the symbol and
                    child.array[i][j] = symbol;    //create child board
                    children.addLast(child);
                }

        return children;                           //return list of children
    }

    //Method checks whether computer wins
    private boolean computerWin(Board board)
    {
        return check(board, COMPUTER);             //check computer wins
    }                                              //somewhere in board

    //Method checks whether player wins
    private boolean playerWin(Board board)
    {
        return check(board, PLAYER);               //check player wins
    }                                              //somewhere in board

    //Method checks whether board is draw
    private boolean draw(Board board)
    {                  
        //check board is full and neither computer nor player win                            
        return full(board) && !computerWin(board) && !playerWin(board);
    }                   

    //Method checks whether row, column, or diagonal is occupied
    //by a symbol
    private boolean check(Board board, char symbol)
    {
        for (int i = 0; i < size; i++)             //check each row
            if (checkRow(board, i, symbol))
               return true;

        for (int i = 0; i < size; i++)             //check each column
            if (checkColumn(board, i, symbol))
                return true;

        if (checkLeftDiagonal(board, symbol))      //check left diagonal
            return true;

        if (checkRightDiagonal(board, symbol))     //check right diagonal
            return true;

        return false;                          
    }

    //Method checks whether a row is occupied by a symbol
    private boolean checkRow(Board board, int i, char symbol)
    {
        for (int j = 0; j < size; j++)
            if (board.array[i][j] != symbol)
                return false;

        return true;
    }

    //Method checks whether a column is occupied by a symbol
    private boolean checkColumn(Board board, int i, char symbol)
    {
        for (int j = 0; j < size; j++)
            if (board.array[j][i] != symbol)
                return false;

        return true;
    }

    //Method checks whether left diagonal is occupied a symbol
    private boolean checkLeftDiagonal(Board board, char symbol)
    {
        for (int i = 0; i < size; i++)
            if (board.array[i][i] != symbol)
               return false;

        return true;
    }

    //Method checks whether right diagonal is occupied by a symbol
    private boolean checkRightDiagonal(Board board, char symbol)
    {
        for (int i = 0; i < size; i++)
            if (board.array[i][size-1-i] != symbol)
               return false;

        return true;
    }

    //Method checks whether a board is full
    private boolean full(Board board)
    {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (board.array[i][j] == EMPTY)
                   return false;

        return true;
    }

    //Method makes copy of a board
    private Board copy(Board board)
    { 
        Board result = new Board(size);      

        for (int i = 0; i < size; i++)       
            for (int j = 0; j < size; j++)
                result.array[i][j] = board.array[i][j];

        return result;                       
    }

//Method displays a board 
private void displayBoard(Board board)
{
    for (int i = 0; i < size; i++)
    {
        for (int j = 0; j < size; j++)
        {
            writer.print(board.array[i][j]);
            System.out.print(board.array[i][j]); // Print the cell value
            if (j < size - 1) {
                System.out.print(" | "); 
                writer.print("|");
            }
        }






        
        System.out.println(); // Move to the next line after printing a row
        writer.print("\n");
        if (i < size - 1) {

            for (int j = 0; j < size; j++) {
                if (j < size - 1) {
                    writer.print("---");
                    System.out.print("---"); 
                } else {
                    writer.print("--");
                    System.out.print("--"); 
                }
            }
            writer.print("\n");
            System.out.println();
             
        }
    }
}




    //Method evaluates a board
    private int evaluate(Board board)
    {
        if (computerWin(board))                    //utility is 4*size if computer wins
            return 4*size;
        else if (playerWin(board))                 //utility is -4*size if player wins
            return -4*size;
        else if (draw(board))                      //utility is 3*size if draw
            return 3*size;
        else                            
            return count(board, COMPUTER) - count(board, PLAYER);
    }                

                                                   
    //Method counts possible ways a symbol can win
    private int count(Board board, char symbol)
    {
        int answer = 0;

        for (int i = 0; i < size; i++)
            if (testRow(board, i, symbol))         //count winning rows   
                answer++;

        for (int i = 0; i < size; i++)
            if (testColumn(board, i, symbol))      //count winning columns
               answer++;

        if (testLeftDiagonal(board, symbol))       //count winning left diagonal
            answer++;

        if (testRightDiagonal(board, symbol))      //count winning right diagonal
            answer++;

        return answer;
    }

    //Method checks whether a row is occupied by a symbol or empty
    private boolean testRow(Board board, int i, char symbol)
    {
        for (int j = 0; j < size; j++)
            if (board.array[i][j] != symbol && board.array[i][j] != EMPTY)
                return false;

        return true;
    }

    //Method checks whether a column is occupied by a symbol or empty
    private boolean testColumn(Board board, int i, char symbol)
    {
        for (int j = 0; j < size; j++)
            if (board.array[j][i] != symbol && board.array[j][i] != EMPTY)
                return false;

        return true;
    }

    //Method checks whether left diagonal is occupied by a symbol or empty
    private boolean testLeftDiagonal(Board board, char symbol)
    {
        for (int i = 0; i < size; i++)
            if (board.array[i][i] != symbol && board.array[i][i] != EMPTY)
               return false;

        return true;
    }

    //Method checks whether right diagonal is occupied by a symbol or empty
    private boolean testRightDiagonal(Board board, char symbol)
    {
        for (int i = 0; i < size; i++)
            if (board.array[i][size-1-i] != symbol && board.array[i][size-1-i] != EMPTY)
               return false;

        return true;
    }





    private int calculateScore(char symbol) {
        int p = 0; // Number of two consecutive pieces
        int q = 0; // Number of three consecutive pieces
    
        // Loop through each row and column
        for (int i = 0; i < size; i++) {
            p += countConsecutive(i, -1, symbol, 2); // Count two consecutive in row
            p += countConsecutive(-1, i, symbol, 2); // Count two consecutive in column
            q += countConsecutive(i, -1, symbol, 3); // Count three consecutive in row
            q += countConsecutive(-1, i, symbol, 3); // Count three consecutive in column
        }
    
        // Count diagonals for two and three consecutive pieces
        p += countDiagonalConsecutive(symbol, 2, true); // Count two consecutive in left diagonal
        p += countDiagonalConsecutive(symbol, 2, false); // Count two consecutive in right diagonal
        q += countDiagonalConsecutive(symbol, 3, true); // Count three consecutive in left diagonal
        q += countDiagonalConsecutive(symbol, 3, false); // Count three consecutive in right diagonal
    
        // Calculate the score based on 2p + 3q
        return 2 * p + 3 * q;
    }
    












    private int countConsecutive(int row, int col, char symbol, int length) {
        int count = 0;
        int consecutive = 0;
        // Horizontal check (if row is not -1)
        if (row != -1) {
            for (int j = 0; j < size; j++) {
                if (board.array[row][j] == symbol) {
                    consecutive++;
                    if (consecutive == length) {
                        count++;
                        if (length == 3) { 
                            j++;
                        }
                    }
                } else {
                    consecutive = 0;
                }
            }
        }
        // Reset for vertical check
        consecutive = 0;
        // Vertical check (if col is not -1)
        if (col != -1) {
            for (int i = 0; i < size; i++) {
                if (board.array[i][col] == symbol) {
                    consecutive++;
                    if (consecutive == length) {
                        count++;
                        if (length == 4) { 
                            i++;
                        }
                    }
                } else {
                    consecutive = 0;
                }
            }
        }
        return count;
    }

    // Count consecutive pieces diagonally
    private int countDiagonalConsecutive(char symbol, int length, boolean leftDiagonal) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            int consecutive = 0;
            for (int j = 0; j < size; j++) {
                int x = i + (leftDiagonal ? j : -j);
                int y = j;
                if (x >= 0 && x < size && y >= 0 && y < size) {
                    if (board.array[x][y] == symbol) {
                        consecutive++;
                        if (consecutive == length) {
                            count++;
                            break; // Move to next diagonal after finding a sequence
                        }
                    } else {
                        consecutive = 0;
                    }
                }
            }
        }
        return count;
    }



    
}

