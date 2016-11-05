import java.util.ArrayList;
import java.util.Scanner;

public class Demo 
{
	public static void main(String[] args)
	{
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Enter a team to get stats for: ");
		String teamNumberString = scan.nextLine();
		
		while (!isInteger(teamNumberString))
		{
			System.out.println("Please enter a positive integer.");
		}
		
		int teamNumber = Integer.parseInt(teamNumberString);
		
		try
		{
			Parser parser = new Parser(teamNumber, true);
			
			System.out.println("Team total: " + parser.getTeamScore());
			System.out.println("Total users: " + parser.getNumUsers());
			
			System.out.println("Press enter to continue...");
			scan.nextLine();
			
			System.out.println("All users and scores: ");
			
			ArrayList<Pair<String, Long>> users = parser.getAllData();
			
			
			for (int i = 0; i < users.size(); i++)
			{
				System.out.println(String.format("%50s " + users.get(i).getSecond(), users.get(i).getFirst()));
			}
		} catch (Exception e) { e.printStackTrace(); }
		
		scan.close();
	}
	
	private static boolean isInteger(String toTest)
	{
		try
		{
			Integer.parseInt(toTest);
			return true;
		} catch (Exception e)
		{
			return false;
		}
	}
}
