//Tester program for tic-tac with min-max, depth limit, 
//board evaluation, and alph-beta pruning
import java.util.Scanner;
public class AlphaBetaTester
{
   //main program for tester
   public static void main(String[] args)
   {


      // ask for user input for sixe of the game

      Scanner in = new Scanner(System.in);

      System.out.print("Enter the board size: ");
      int size = in.nextInt();

      
      System.out.print("Enter output file: ");
      String outputFile = in.nextLine();

      
       //play tic-tac game
       
       AlphaBeta a = new AlphaBeta(size,outputFile);
	   a.play();

      in.close();
   }
}