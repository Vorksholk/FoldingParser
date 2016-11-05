import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser 
{
	private static final int MAXIMUM_RETRIES = 100;
	
	/* Not using a HashMap from username to score so that the users are ordered by their score */
	private static ArrayList<Pair<String, Long>> users = new ArrayList<>();
	private static long teamScore = 0L;
	
	public Parser(int teamNum) throws IllegalArgumentException, SocketException
	{
		this(teamNum, false);
	}
	
	public Parser(int teamNum, boolean retry) throws IllegalArgumentException, SocketException
	{
		if (teamNum <= 0)
		{
			throw new IllegalArgumentException("Team number cannot be negative.");
		}
		
		String fileName = "work" + teamNum + ".html";
		boolean saved = saveUrl(fileName, "http://fah-web2.stanford.edu/teamstats/team" + teamNum + ".html");
		
		if (!saved)
		{
			if (retry)
			{
				int tries = 0;
				while (!saved)
				{
					if (tries > MAXIMUM_RETRIES)
					{
						throw new SocketException("After " + MAXIMUM_RETRIES + " retries, the FAH Stats page could still not be grabbed.");
					}
					
					saved = saveUrl(fileName, "http://fah-web2.stanford.edu/teamstats/team" + teamNum + ".html");
					tries++;
				}
			}
			else
			{
				throw new SocketException("The FAH Stats page could still not be grabbed.");
			}
		}
		
		try 
		{
			Scanner scan = new Scanner(new File(fileName));
			String input = "";
			int chunk = 0;
			while (chunk == 0)
			{
				input = scan.nextLine();
				
				if (input.contains("404 Not Found"))
				{
					scan.close();
					throw new IllegalArgumentException("The team provided does not have a stats page; is it in the top 1000 teams?");
				}
				
				if ((input.contains("" + teamNum) && input.contains("TR class=odd")))
				{
			          input = input.substring(input.indexOf("<TD>") + 4);
			          input = input.substring(input.indexOf("<TD>") + 4);
			          input = input.substring(input.indexOf("<TD>") + 4);
			          input = input.substring(0, input.indexOf("</TD>"));
			          
			          teamScore = Long.parseLong(input);
			          
			          chunk = 1;
				}
			}
			
			while (chunk == 1)
			{
		        input = scan.nextLine();
		        if ((input.contains("rank")) && (input.contains("credit"))) 
		        {
		          chunk = 2;
		        }
			}
			
			ArrayList<String> categories = new ArrayList<>();
			
		    input = input.substring(input.indexOf("<TD>"));
		    input = input.substring(input.indexOf("<TD>"));
		    categories.add(input.substring(4, input.indexOf("</TD>")));
		    
		    while (input.indexOf("<TD>") > -1)
		    {
		        input = input.substring(input.indexOf("<TD>"), input.length());
		        input = input.substring(4);
		        categories.add(input.substring(0, input.indexOf("</TD>")));
		    }
		    
		    boolean atEnd = false;
		    int nameLoc = categoryIndex("name", categories);
		    int numLoc = categoryIndex("credit", categories);
		    
		    while (!atEnd)
		    {
		        input = scan.nextLine();
		        if (!input.contains("TR class")) 
		        {
		            atEnd = true;
		        } 
		        
		        else 
		        {
		            try
		            {
		                String username = getComponent(nameLoc, input).replaceAll("\\\\", "").replaceAll("/", "").replaceAll("//", "");
		                long score = Long.valueOf(Long.parseLong(getComponent(numLoc, input)));
		                
		                users.add(new Pair<String, Long>(username, score));
		            }
		            catch (Exception e)
		            {
		                 e.printStackTrace();
		            }
		        }
		      }
		      scan.close();
			
			
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	
	public long getTeamScore()
	{
		return teamScore;
	}
	
	public ArrayList<String> getUsernames()
	{
		ArrayList<String> usernames = new ArrayList<String>();
		
		for (int i = 0; i < users.size(); i++)
		{
			usernames.add(users.get(i).getFirst());
		}
		
		return usernames;
	}
	
	public ArrayList<Long> getScores()
	{
		ArrayList<Long> scores = new ArrayList<Long>();
		
		for (int i = 0; i < users.size(); i++)
		{
			scores.add(users.get(i).getSecond());
		}
		
		return scores;
	}
	
	public int getNumUsers()
	{
		return users.size();
	}
	
	public long getScoreOfUser(String username)
	{
		for (Pair<String, Long> user : users)
		{
			if (user.getFirst().equals(username))
			{
				return user.getSecond();
			}
		}
		
		return -1;
	}
	
	public ArrayList<Pair<String, Long>> getAllData()
	{
		// Manual deep copy
		ArrayList<Pair<String, Long>> copy = new ArrayList<>();
		
		for (Pair<String, Long> user : users)
		{
			copy.add(user.clone());
		}
		
		return copy;
	}
	
	private static int categoryIndex(String toFind, ArrayList<String> categories)
	{
		for (int i = 0; i < categories.size(); i++) 
		{
			if (toFind.equals(categories.get(i)))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	private static String getComponent(int index, String input)
	{
		input = input.substring(input.indexOf("<TD>"));
		input = input.substring(input.indexOf("<TD>"));
		
		for (int i = 0; i < index; i++)
		{
			input = input.substring(input.indexOf("<TD>"), input.length());
			input = input.substring(4);
		}
		
		return input.substring(0, input.indexOf("</TD>"));
	}

    private static boolean saveUrl(String filename, String urlString)
    {
        try
        {
            BufferedInputStream in = null;
            FileOutputStream fout = null;
            try
            {
                in = new BufferedInputStream(new URL(urlString).openStream());
                fout = new FileOutputStream(filename);

                byte data[] = new byte[1024];
                int count;
                while ((count = in.read(data, 0, 1024)) != -1)
                {
                    fout.write(data, 0, count);
                }
            }
            finally
            {
                if (in != null)
                    in.close();
                if (fout != null)
                    fout.close();
            }
        } catch (Exception e)
        {
        	return false;
        }
        
        return true;
    }
}
