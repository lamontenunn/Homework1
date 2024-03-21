import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class SudokuTester {
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        System.out.print("Enter input file: ");
        String input = in.nextLine();

        System.out.print("Enter output file: ");
        String outp = in.nextLine();

        try {
            Scanner file = new Scanner(new File(input));

            int size = file.nextInt();
            file.nextLine();

            int[][] board = new int[size][size];

            int lineNumber = 0;

            file.nextLine();

            // this methods checks the input function and will display if the input is
            // incorrect
            while (file.hasNextLine() && lineNumber < size) {
                String line = file.nextLine().trim();
                String[] parts = line.split("\\s+");
                for (int j = 0; j < parts.length; j++) {
                    if (parts[j].matches("-?\\d+")) { // Check if the part is an integer
                        board[lineNumber][j] = Integer.parseInt(parts[j]);
                    } else {
                        // Convert placeholders ('o', 'e', 'w') to their integer encodings
                        board[lineNumber][j] = convertPlaceholderToInt(parts[j]);
                    }
                }
                lineNumber++; // Increment lineNumber after processing each line
            }

            file.close();

            Sudoku s = new Sudoku(board);

            if (s.solve()) {
                s.display();

                try (PrintWriter out = new PrintWriter(outp)) {
                    for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                            // Convert the internal representation back to the original format
                            switch (board[i][j]) {
                                case -1:
                                    out.print("o ");
                                    break;
                                case -2:
                                    out.print("e ");
                                    break;
                                case -3:
                                    out.print("w ");
                                    break;
                                case -4:
                                    out.print("b ");
                                    break;
                                default:
                                    out.print(board[i][j] + " ");
                                    break;
                            }
                        }
                        out.println(); // New line after each row
                    }
                }

            } else {
                System.out.println("No solution found.");
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found.");
        }
    }

    private static int convertPlaceholderToInt(String placeholder) {
        switch (placeholder) {
            case "o":
                return -1;
            case "e":
                return -2;
            case "w":
                return -3;
            case "b":
                return -4;
            default:
                return 0; // Default case for empty cells or any unspecified placeholder
        }
    }

}