//Tester program for tic-tac with min-max
import java.util.Scanner;
public class TicTacTester
{
   //main program for tester
   public static void main(String[] args)
   {

      int boardSize;
      

      Scanner in = new Scanner(System.in);
      System.out.println("Enter board size");
      boardSize = in.nextInt();
      System.err.print("Enter Depth: ");
      int d = in.nextInt();









       TicTac t = new TicTac(boardSize, d);
	   t.play();
   }
}
