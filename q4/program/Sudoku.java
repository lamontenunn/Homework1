public class Sudoku {
    private int[][] puzzleBoard; // Use int[][] for internal representation
    private final int boardSize;
    private final int subSquareSize;

    public Sudoku(int[][] board) {
        this.puzzleBoard = board;
        this.boardSize = board.length;
        this.subSquareSize = (int) Math.sqrt(boardSize);
    }

    boolean solve() {
        int[] cell = findMostConstrainedCell();
        int row = cell[0];
        int col = cell[1];

        // If no cell is found, the puzzle is solved
        if (row == -1 || col == -1) {
            return true;
        }

        // Try filling the cell with valid numbers according to the puzzle rules
        for (int num = 1; num <= boardSize; num++) {
            if (canPlaceNumber(row, col, num)) {
                int original = puzzleBoard[row][col];
                puzzleBoard[row][col] = num; // Directly assign the number
                if (!isPartialSolutionViable(row, col)) {
                    puzzleBoard[row][col] = original; // Undo the placement
                    continue; // Skip to the next number
                }

                if (solve()) {
                    return true;
                }
                puzzleBoard[row][col] = original; // backtrack
            }
        }

        return false; // backtracking
    }

    // Checks if the current cell is writable, meaning it's designated 'w', 'o', or
    // 'e'
    private boolean isWritableCell(int row, int col) {
        // Consider cells with 0 or special placeholders as writable
        return puzzleBoard[row][col] == -3 || puzzleBoard[row][col] == -2 || puzzleBoard[row][col] == -1;
    }

    // Determines if a number can be placed in a given cell without violating Sudoku
    // rules
    private boolean canPlaceNumber(int row, int col, int num) {
        // Check if the cell has a special constraint
        if (puzzleBoard[row][col] == -1 && num % 2 == 0) { // Cell must contain an odd number
            return false;
        }
        if (puzzleBoard[row][col] == -2 && num % 2 != 0) { // Cell must contain an even number
            return false;
        }

        // Check if the number is not already placed in the row and column
        for (int i = 0; i < boardSize; i++) {
            if (puzzleBoard[row][i] == num || puzzleBoard[i][col] == num) {
                return false;
            }
        }

        // Check the sub-square for duplicates
        int startRow = row - row % subSquareSize;
        int startCol = col - col % subSquareSize;
        for (int r = 0; r < subSquareSize; r++) {
            for (int c = 0; c < subSquareSize; c++) {
                if (puzzleBoard[startRow + r][startCol + c] == num) {
                    return false;
                }
            }
        }

        return true; // The number can be placed
    }

    public void display() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                // Adjust this to match how you've decided to internally mark cells
                switch (puzzleBoard[i][j]) {
                    case -1:
                        System.out.print("o ");
                        break;
                    case -2:
                        System.out.print("e ");
                        break;
                    case -3:
                        System.out.print("w ");
                        break;
                    case -4:
                        System.out.print("b ");
                        break;
                    default:
                        System.out.print(puzzleBoard[i][j] + " ");
                        break;
                }
            }
            System.out.println();
        }
    }

    private int[] findMostConstrainedCell() {
        int minOptions = boardSize + 1; // Start higher than the maximum possible options
        int[] cell = new int[] { -1, -1 }; // Store row and column of the most constrained cell

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (isWritableCell(row, col)) {
                    int options = countValidOptions(row, col);
                    if (options < minOptions) {
                        minOptions = options;
                        cell[0] = row;
                        cell[1] = col;
                    }
                }
            }
        }

        return cell; // Returns the position of the most constrained cell
    }

    private int countValidOptions(int row, int col) {
        int validOptions = 0;
        for (int num = 1; num <= boardSize; num++) {
            if (canPlaceNumber(row, col, num)) {
                validOptions++;
            }
        }
        return validOptions;
    }

    private boolean isPartialSolutionViable(int lastPlacedRow, int lastPlacedCol) {
        // Check if every number can still be placed in the row, column, and subgrid of
        // the last placed number
        return isRowViable(lastPlacedRow) && isColumnViable(lastPlacedCol)
                && isSubgridViable(lastPlacedRow, lastPlacedCol);
    }

    private boolean isRowViable(int row) {
        boolean[] seen = new boolean[boardSize];
        for (int col = 0; col < boardSize; col++) {
            if (Character.isDigit(puzzleBoard[row][col])) {
                int num = puzzleBoard[row][col] - '0';
                if (num > 0 && num <= boardSize) {
                    if (seen[num - 1])
                        return false; // Duplicate found in row
                    seen[num - 1] = true;
                }
            }
        }
        return true;
    }

    private boolean isColumnViable(int col) {
        boolean[] seen = new boolean[boardSize];
        for (int row = 0; row < boardSize; row++) {
            if (Character.isDigit(puzzleBoard[row][col])) {
                int num = puzzleBoard[row][col] - '0';
                if (num > 0 && num <= boardSize) {
                    if (seen[num - 1])
                        return false; // Duplicate found in column
                    seen[num - 1] = true;
                }
            }
        }
        return true;
    }

    private boolean isSubgridViable(int row, int col) {
        boolean[] seen = new boolean[boardSize];
        int startRow = row - row % subSquareSize;
        int startCol = col - col % subSquareSize;
        for (int r = 0; r < subSquareSize; r++) {
            for (int c = 0; c < subSquareSize; c++) {
                int val = puzzleBoard[startRow + r][startCol + c];
                if (Character.isDigit(val)) {
                    int num = val - '0';
                    if (num > 0 && num <= boardSize) {
                        if (seen[num - 1])
                            return false; // Duplicate found in subgrid
                        seen[num - 1] = true;
                    }
                }
            }
        }
        return true;
    }

}
