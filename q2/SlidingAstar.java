/*
 * Author: LaMonte Nunn
 * Date: 15 FEB 2024
 * 
 * Overview:
 * This program solves a sliding puzzle using the A* search algorithm. The puzzle consists of a board
 * with nxn tiles, including numbers, red (R) tiles, and green (G) tiles. The goal is to sort the board in a
 * specific order: numbers in ascending order, followed by all R tiles, and then all G tiles. Tiles can be
 * swapped if they are neighbors (horizontally or vertically), but swaps are restricted based on tile type.
 * 
 * The program uses the A* algorithm with a heuristic based on taxi distances to find the
 * shortest path to the goal state. It includes components for tracking the path cost, heuristic estimation,
 * and total cost, as well as functionality for generating possible moves and evaluating board states.
 * 
 * Input:
 * - Initial board state: An nxn matrix with a mix of numbers, R tiles, and G tiles.
 * - Goal board state: Calculated by the program based on sorting criteria.
 * 
 * Output:
 * - Sequence of moves leading to the goal state, displayed on the console and written to a specified output file.
 * 
 * Constraints:
 * - Only neighboring tiles (horizontal or vertical) can be swapped.
 * - Tiles of the same type (number-number, R-R, G-G) cannot be swapped among themselves.
 * 
 * Features:
 * - A* search algorithm implementation.
 * - Heuristic function based on taxi distances for estimating the cost to reach the goal state.
 * - Open and closed lists for keeping track of explored and unexplored board states.
 * 
 */

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

//This program solves sliding puzzle using A* algorithm
public class SlidingAstar {
    // Board class (inner class)
    private class Board {
        private char[][] array; // board array
        private int gvalue; // path cost
        private int hvalue; // heuristic value
        private int fvalue; // gvalue plus hvalue
        private Board parent; // parent board

        // Constructor of board class
        private Board(char[][] array, int size) {
            this.array = new char[size][size]; // create board array

            for (int i = 0; i < size; i++) // copy given array
                for (int j = 0; j < size; j++)
                    this.array[i][j] = array[i][j];

            this.gvalue = 0; // path cost, heuristic value,
            this.hvalue = 0; // fvalue are all 0
            this.fvalue = 0;

            this.parent = null; // no parent
        }
    }

    // variables of a Sliding aStar
    private Board initial; // initial board
    private Board goal; // goal board
    private int size; // board size
    private PrintWriter writer;

    // Constructor of SlidingAstar class
    public SlidingAstar(char[][] initial, char[][] goal, int size, String outputFileName) {
        this.size = size; // set size of board
        this.initial = new Board(initial, size); // create initial board
        this.goal = new Board(goal, size); // create goal board

        try {
            this.writer = new PrintWriter(new FileWriter(outputFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Method solves sliding puzzle
    public void solve() {

        LinkedList<Board> openList = new LinkedList<Board>(); // open list
        LinkedList<Board> closedList = new LinkedList<Board>();// closed list

        openList.addFirst(initial); // add initial board to open list

        while (!openList.isEmpty()) {// while open list has more boards

            int best = selectBest(openList); // select best board

            Board board = openList.remove(best); // remove board

            closedList.addLast(board); // add board to closed list

            if (goal(board)) { // if board is goal

                displayPath(board); // display path to goal
                return; // stop search
            }

            else { // if board is not goal

                LinkedList<Board> children = generate(board);// create children

                for (int i = 0; i < children.size(); i++) { // for each child
                    Board child = children.get(i);

                    if (!exists(child, closedList)) // if child is not in closed list
                    {
                        if (!exists(child, openList))// if child is not in open list
                            openList.addLast(child); // add to open list
                        else { // if child is already in open list
                            int index = find(child, openList);
                            if (child.fvalue < openList.get(index).fvalue) { // if fvalue of new copy
                                openList.remove(index); // is less than old copy
                                openList.addLast(child); // replace old copy
                            } // with new copy
                        }
                    }
                }
            }
        }

        System.out.println("no solution"); // no solution if there are no more board in open list
    }

    // Method creates children of a board
    private LinkedList<Board> generate(Board board) {

        // creates children linkedlist
        LinkedList<Board> children = new LinkedList<>();

        // Iterate over the board to find all possible moves
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // Check all four directions for possible swaps
                char[] directions = { 'N', 'S', 'E', 'W' };
                for (char d : directions) {
                    if (isValidSwap(board, d, i, j)) {
                        Board child = createChild(board, i, j, d);
                        if (child != null) { // Ensure the child is valid
                            children.addLast(child);
                        }
                    }
                }
            }
        }

        return children;
    }

    // given direction
    private Board createChild(Board board, int i, int j, char direction) {
        // First, make a deep copy of the board to create a new child
        Board child = copy(board);

        // Determine the swap position based on the direction
        int swapI = i, swapJ = j;
        switch (direction) {
            case 'N':
                swapI = i - 1;
                break; // Move up
            case 'S':
                swapI = i + 1;
                break; // Move down
            case 'E':
                swapJ = j + 1;
                break; // Move right
            case 'W':
                swapJ = j - 1;
                break; // Move left
        }

        // Perform the swap if the move is within bounds
        if (swapI >= 0 && swapI < size && swapJ >= 0 && swapJ < size) {
            char temp = child.array[i][j];
            child.array[i][j] = child.array[swapI][swapJ];
            child.array[swapI][swapJ] = temp;

            // After the swap, update the gvalue and parent
            child.gvalue = board.gvalue + 1; // Increment the path cost by 1
            child.parent = board; // Set the current board as parent

            // Recalculate the heuristic value for the child
            child.hvalue = heuristic_D(child); // Call your heuristic function
            // Update the fvalue (total cost)
            child.fvalue = child.gvalue + child.hvalue;
        }

        return child;
    }

    // Heuristic value is the sum of taxi distances of misplaced values
    private int heuristic_D(Board board) {
        // initial heuristic value
        int value = 0;

        // go thru board
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                // if value mismatches in goal board
                if (board.array[i][j] != goal.array[i][j]) {
                    // locate value in goal board
                    int x = 0, y = 0;
                    boolean found = false;
                    for (x = 0; x < size; x++) {
                        for (y = 0; y < size; y++)
                            if (goal.array[x][y] == board.array[i][j]) {
                                found = true;
                                break;
                            }
                        if (found)
                            break;
                    }

                    // find city distance between two locations
                    value += (int) Math.abs(x - i) + (int) Math.abs(y - j);
                }

        // return heuristic value
        return value;
    }

    // Method locates the board with minimum fvalue in a list of boards
    private int selectBest(LinkedList<Board> list) {
        int minValue = list.get(0).fvalue; // initialize minimum
        int minIndex = 0; // value and location

        for (int i = 0; i < list.size(); i++) {
            int value = list.get(i).fvalue;
            if (value < minValue) // updates minimums if
            { // board with smaller f value
                minValue = value; // fvalue is found
                minIndex = i;
            }
        }

        return minIndex; // return minimum location
    }

    // Method creates copy of a board
    private Board copy(Board board) {
        return new Board(board.array, size);
    }

    // Method decides whether a board is goal
    private boolean goal(Board board) {
        return identical(board, goal); // compare board with goal
    }

    // Method decides whether a board exists in a list
    private boolean exists(Board board, LinkedList<Board> list) {
        for (int i = 0; i < list.size(); i++) // compare board with each
            if (identical(board, list.get(i))) // element of list
                return true;

        return false;
    }

    // Method finds location of a board in a list
    private int find(Board board, LinkedList<Board> list) {
        for (int i = 0; i < list.size(); i++) // compare board with each
            if (identical(board, list.get(i))) // element of list
                return i;

        return -1;
    }

    // Method decides whether two boards are identical
    private boolean identical(Board p, Board q) {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (p.array[i][j] != q.array[i][j])
                    return false; // if there is a mismatch then false

        return true; // otherwise true
    }

    // Method displays path from initial to current board
    private void displayPath(Board board) {
        LinkedList<Board> list = new LinkedList<Board>();

        Board pointer = board; // start at current board

        while (pointer != null) // go back towards initial board
        {
            list.addFirst(pointer); // add boards to beginning of list

            pointer = pointer.parent; // keep going back
        }
        // print boards in list
        for (int i = 0; i < list.size(); i++)
            displayBoard(list.get(i));
    }

    // Method displays baord
    private void displayBoard(Board board) {
        StringBuilder sb = new StringBuilder(); // Use StringBuilder to accumulate text
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                for (int j = 0; j < size; j++) {
                    sb.append("--");
                    if (j < size - 1) {
                        sb.append("-");
                    }
                }
                sb.append("\n");
            }

            for (int j = 0; j < size; j++) {
                sb.append(board.array[i][j]);
                if (j < size - 1) {
                    sb.append("|");
                }
            }
            sb.append("\n");
        }
        sb.append("\n");

        // Print to console
        System.out.print(sb.toString());
        // Write to file
        writer.print(sb.toString());
        writer.flush(); // Ensure data is written
    }

    private boolean isValidSwap(Board board, char direction, int i, int j) {
        int ni = i, nj = j; // new indices after moving in the specified direction

        // Update indices based on direction
        switch (direction) {
            case 'N':
                ni = i - 1;
                break; // Move up
            case 'S':
                ni = i + 1;
                break; // Move down
            case 'E':
                nj = j + 1;
                break; // Move right
            case 'W':
                nj = j - 1;
                break; // Move left
        }

        // Check bounderies
        if (ni < 0 || nj < 0 || ni >= size || nj >= size)
            return false;

        char current = board.array[i][j];
        char next = board.array[ni][nj];

        // Check for valid swap
        // Swap between number and 'R' or 'G'
        if (Character.isDigit(current) && (next == 'R' || next == 'G'))
            return true;
        if (Character.isDigit(next) && (current == 'R' || current == 'G'))
            return true;

        // Swap between 'R' and 'G'
        if ((current == 'R' && next == 'G') || (current == 'G' && next == 'R'))
            return true;

        // If none of the valid return false
        return false;
    }

}
