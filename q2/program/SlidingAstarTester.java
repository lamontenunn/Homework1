import java.io.*;
import java.util.*;

public class SlidingAstarTester {
    // main method for testing
    public static void main(String[] args) {
        try {

            Scanner name = new Scanner(System.in);
            System.out.print("Enter input file name: ");
            String fileName = name.nextLine();

            System.out.print("Enter output file name: ");
            String outputFileName = name.nextLine();

            File file = new File(fileName);
            Scanner in = new Scanner(file);
            int size = in.nextInt();

            char[][] initial = new char[size][size];
            char[][] goal = new char[size][size];

            in.nextLine();
            in.nextLine();
            // initial board
            for (int i = 0; i < size; i++) {
                String line = in.nextLine(); // Read the entire line
                String[] tokens = line.split("\\s+"); // Split by spaces
                for (int j = 0; j < size; j++) {
                    initial[i][j] = tokens[j].charAt(0); // Assign characters directly
                }
            }

            // find goal board setup
            ArrayList<Integer> nums = new ArrayList<>();
            int reds = 0;
            int greens = 0;
            // goal is to have a sorted list of numbers followed by 'R' and 'G'
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (Character.isDigit(initial[i][j])) {
                        nums.add(initial[i][j] - '0'); // Convert char to integer
                    } else if (initial[i][j] == 'R') {
                        reds++;
                    } else if (initial[i][j] == 'G') {
                        greens++;
                    }
                }
            }

            // Sort the list of numbers
            Collections.sort(nums);

            // fill the goal bord from array and count of reds and greens
            int counter = 0; // set counter
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (counter < nums.size()) {
                        goal[i][j] = (char) ('0' + nums.get(counter)); // Convert integer to char
                        counter++;
                    } else {
                        // Assign remaining 'R' and 'G'
                        if (reds > 0) {
                            goal[i][j] = 'R';
                            reds--;
                        } else if (greens > 0) {
                            goal[i][j] = 'G';
                            greens--;
                        }
                    }
                }
            }

            // create slidingAstar object given initial board, goal board and size
            SlidingAstar s = new SlidingAstar(initial, goal, size, outputFileName);
            s.solve();

            name.close();
            in.close();

            // catch exception
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }

    }
}
