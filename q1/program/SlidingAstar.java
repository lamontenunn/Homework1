
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;


 
//This program solves sliding puzzle using A* algorithm
public class SlidingAstar
{
    //Board class (inner class)
    private class Board
    {
        private char[][] array;                 //board array
        private int gvalue;                     //path cost
        private int hvalue;                     //heuristic value
        private int fvalue;                     //gvalue plus hvalue
        private Board parent;                   //parent board

        //Constructor of board class
        private Board(char[][] array, int size)
        {
            this.array = new char[size][size];  //create board array

            for (int i = 0; i < size; i++)      //copy given array
                for (int j = 0; j < size; j++)
                    this.array[i][j] = array[i][j];

            this.gvalue = 0;                    //path cost, heuristic value,
            this.hvalue = 0;                    //fvalue are all 0
            this.fvalue = 0;

            this.parent = null;                 //no parent
        }
    }

    private Board initial;                         //initial board
    private Board goal;                            //goal board
    private int size;                              //board size
    private int evaluationFun;
    private int heuristicFun;
    private PrintWriter writer; // For writing output to file
    private long startTime; // To measure runtime
    private int numberOfSwaps = 0; // To count swaps
    private int numberOfBoardsSearched = 0; // To track boards searched


    //Constructor of SlidingAstar class
    public SlidingAstar(char[][] initial, char[][] goal, int size, int evaluationFun, int heuristicFun) {
        this.size = size;
        this.evaluationFun = evaluationFun;
        this.heuristicFun = heuristicFun;
        this.initial = new Board(initial, size);
        this.goal = new Board(goal, size);


        this.initial.hvalue = calculateHeuristic(initial);
        this.goal.hvalue = calculateHeuristic(goal);
    }


    private int calculateHeuristic(char[][] board) {
        switch (heuristicFun) {
            case 1:
                return heuristic_M(new Board(board, this.size));
            case 2:
                return heuristic_D(new Board(board, this.size));
            default:
                throw new IllegalArgumentException("Invalid heuristic function option: " + heuristicFun);
        }
    }


    //Method solves sliding puzzle
    public void solve(String outputPath) {
        try {
            writer = new PrintWriter(outputPath);
        } catch (FileNotFoundException e) {
            System.out.println("Output file not found: " + outputPath);
            return;
        }

        startTime = System.currentTimeMillis(); // Start timing

        LinkedList<Board> openList = new LinkedList<>();
        LinkedList<Board> closedList = new LinkedList<>();

        openList.addFirst(initial);

        while (!openList.isEmpty()) {
            numberOfBoardsSearched++; // Increment boards searched

            int best = selectBest(openList);
            Board board = openList.remove(best);

            closedList.addLast(board);

            if (goal(board)) {
                long endTime = System.currentTimeMillis(); // End timing
                displayPath(board);
                System.out.println("Runtime: " + (endTime - startTime) + " ms");
                writer.println("Runtime: " + (endTime - startTime) + " ms");
                System.out.println("Number of swaps: " + numberOfSwaps);
                writer.println("Number of swaps: " + numberOfSwaps);
                System.out.println("Number of boards searched: " + numberOfBoardsSearched);
                writer.println("Number of boards searched: " + numberOfBoardsSearched);
                writer.close(); 
                return;
            } else {
                LinkedList<Board> children = generate(board);

                for (Board child : children) {
                    if (!exists(child, closedList)) {
                        if (!exists(child, openList)) {
                            openList.addLast(child);
                        } else {
                            int index = find(child, openList);
                            if (child.fvalue < openList.get(index).fvalue) {
                                openList.remove(index);
                                openList.addLast(child); //replace old copy
                              }                            //with new copy
                          }                               
                     }     
                 }                                  
             }                                       
         }

         System.out.println("no solution");            //no solution if there are
         writer.close();
    }                                                  //no boards in open list
    

    //Method creates children of a board
    private LinkedList<Board> generate(Board board)
    {
        int i = 0, j = 0;
        boolean found = false;

        for (i = 0; i < size; i++)              //find location of empty slot
        {                                       //of board
            for (j = 0; j < size; j++)
                if (board.array[i][j] == '0')
                {   
                    found = true;
                    break;
                }
            
            if (found)
               break;
        }

        boolean north, south, east, west;       //decide whether empty slot
        north = i == 0 ? false : true;          //has N, S, E, W neighbors
        south = i == size-1 ? false : true;
        east = j == size-1 ? false : true; 
        west = j == 0 ? false : true;

        LinkedList<Board> children = new LinkedList<Board>();//list of children

        if (north) children.addLast(createChild(board, i, j, 'N')); //add N, S, E, W
        if (south) children.addLast(createChild(board, i, j, 'S')); //children if
        if (east) children.addLast(createChild(board, i, j, 'E'));  //they exist
        if (west) children.addLast(createChild(board, i, j, 'W'));  
                                                                    
        return children;                        //return children      
    }

    //Method creates a child of a board by swapping empty slot in a 
    //given direction
    private Board createChild(Board board, int i, int j, char direction)
    {
        Board child = copy(board);                   //create copy of board

        if (direction == 'N')                        //swap empty slot to north
        {
            child.array[i][j] = child.array[i-1][j];
            child.array[i-1][j] = '0';
            numberOfSwaps++;
        }
        else if (direction == 'S')                   //swap empty slot to south
        {
            child.array[i][j] = child.array[i+1][j];
            child.array[i+1][j] = '0';
            numberOfSwaps++;
        }
        else if (direction == 'E')                   //swap empty slot to east
        {
            child.array[i][j] = child.array[i][j+1];
            child.array[i][j+1] = '0';
            numberOfSwaps++;
        }
        else                                         //swap empty slot to west
        {
            child.array[i][j] = child.array[i][j-1];
            child.array[i][j-1] = '0';
            numberOfSwaps++;
        }

        child.gvalue = board.gvalue + 1;
        child.hvalue = calculateHeuristic(child.array); // Calculate heuristic for the child

        // Calculate fvalue based on the selected evaluation function
        switch (evaluationFun) {
            case 1: // f = h
                child.fvalue = child.hvalue;
                break;
            case 2: // f = g
                child.fvalue = child.gvalue;
                break;
            case 3: // f = g + h
                child.fvalue = child.gvalue + child.hvalue;
                break;
            default:
                throw new IllegalArgumentException("Invalid evaluation function option: " + evaluationFun);
        }

        child.parent = board;
        return child;                   //return child
    }

    //Method computes heuristic value of board based on misplaced values
    private int heuristic_M(Board board)
    {
        int value = 0;                               //initial heuristic value

        for (int i = 0; i < size; i++)               //go thru board and
            for (int j = 0; j < size; j++)           //count misplaced values
                if (board.array[i][j] != goal.array[i][j])
                   value += 1;                       
  
        return value;                                //return heuristic value
    }

    //Method computes heuristic value of board
    //Heuristic value is the sum of taxi distances of misplaced values
    private int heuristic_D(Board board)
    {
        //initial heuristic value
        int value = 0;

        //go thru board
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                //if value mismatches in goal board    
                if (board.array[i][j] != goal.array[i][j]) 
                {                                
                    //locate value in goal board
                    int x = 0, y = 0;         
                    boolean found = false;
                    for (x = 0; x < size; x++)
                    {                       
                        for (y = 0; y < size; y++)
                            if (goal.array[x][y] == board.array[i][j])
                            {                 
                                found = true; 
                                break;
                            } 
                        if (found)
                           break;                        
                    }
            
                    //find city distance between two locations
                    value += (int)Math.abs(x-i) + (int)Math.abs(y-j);
                }
                     
        //return heuristic value               
        return value;
    }

    //Method locates the board with minimum fvalue in a list of boards
    private int selectBest(LinkedList<Board> list)
    {
        int minValue = list.get(0).fvalue;           //initialize minimum
        int minIndex = 0;                            //value and location

        for (int i = 0; i < list.size(); i++)
        {
            int value = list.get(i).fvalue;
            if (value < minValue)                    //updates minimums if
            {                                        //board with smaller
                minValue = value;                    //fvalue is found
                minIndex  = i;
            } 
        }

        return minIndex;                             //return minimum location
    }   

    //Method creates copy of a board
    private Board copy(Board board)
    {
         return new Board(board.array, size);
    }

    //Method decides whether a board is goal
    private boolean goal(Board board)
    {
        return identical(board, goal);           //compare board with goal
    }                                             

    //Method decides whether a board exists in a list
    private boolean exists(Board board, LinkedList<Board> list)
    {
        for (int i = 0; i < list.size(); i++)    //compare board with each
            if (identical(board, list.get(i)))   //element of list
               return true;

        return false;
    }

    //Method finds location of a board in a list
    private int find(Board board, LinkedList<Board> list)
    {
        for (int i = 0; i < list.size(); i++)    //compare board with each
            if (identical(board, list.get(i)))   //element of list
               return i;

        return -1;
    }
    
    //Method decides whether two boards are identical
    private boolean identical(Board p, Board q)
    {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (p.array[i][j] != q.array[i][j])
                    return false;      //if there is a mismatch then false

        return true;                   //otherwise true
    }

    //Method displays path from initial to current board
    private void displayPath(Board board) {
        LinkedList<Board> path = new LinkedList<>();
        while (board != null) {
            path.addFirst(board);
            board = board.parent;
        }

        for (Board b : path) {
            displayBoard(b); // Display each board in path
        }
    }

    //Method displays board
    private void displayBoard(Board board) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(board.array[i][j] + " ");
                writer.print(board.array[i][j] + " ");
            }
            System.out.println();
            writer.println();
        }
        System.out.println();
        writer.println();
    }
}
