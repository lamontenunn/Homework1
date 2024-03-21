import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SlidingAstarTester {

    public static int evaluationFun;
    public static int heuristicFun;

    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        
        // Ask for input file name
        System.out.print("Enter input file: ");
        String inputFileName = console.nextLine();
        File file = new File(inputFileName);
        
        try {
            Scanner in = new Scanner(file);
            int size = in.nextInt();

            System.out.println("Puzzle size: " + size);

            char[][] initial = new char[size][size];
            char[][] goal = new char[size][size];

            // initial board
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int num = in.nextInt();
                    initial[i][j] = num == 0 ? '0' : (char) ('0' + num);
                }
            }

            // goal board
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int num = in.nextInt();
                    goal[i][j] = num == 0 ? '0' : (char) ('0' + num);
                }
            }

            evaluationFun = in.nextInt();
            heuristicFun = in.nextInt();

            SlidingAstar s = new SlidingAstar(initial, goal, size, evaluationFun, heuristicFun);

            // Ask for output file name after reading the input file
            System.out.print("Enter output file: ");
            if (in.hasNextLine()) { // To consume any newline left in the buffer
                in.nextLine();
            }
            String outputFile = console.nextLine(); // Use console to read output file name

            s.solve(outputFile);

            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } finally {
            console.close(); // Close the console scanner
        }
    }
}
