package searchingRelated;
import IRUtilities.*;
import java.io.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class StopStem
{
	private Porter porter;
	private java.util.HashSet<String> stopWords;
	public boolean isStopWord(String str)
	{
		return stopWords.contains(str);	
	}
	public StopStem(String str)
	{
		super();
		porter = new Porter();
		stopWords = new java.util.HashSet<String>();
		
		File file = new File(str);
		
		try(Scanner sc = new Scanner(file)){
			while(sc.hasNextLine())
				stopWords.add(sc.nextLine());
		}catch(FileNotFoundException e) {
			e.printStackTrace();
			System.out.println(file.getAbsolutePath() + " not find!");
		}finally {
			
		}
	}
	public String stem(String str)
	{
		return porter.stripAffixes(str);
	}
	public static void main(String[] arg)
	{
		StopStem stopStem = new StopStem("stopwords.txt");
		String input="";
		try{
			do
			{
				System.out.print("Please enter a single English word: ");
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				input = in.readLine();
				if(input.length()>0)
				{	
					if (stopStem.isStopWord(input))
						System.out.println("It should be stopped");
					else
			   			System.out.println("The stem of it is \"" + stopStem.stem(input)+"\"");
				}
			}
			while(input.length()>0);
		}
		catch(IOException ioe)
		{
			System.err.println(ioe.toString());
		}
	}
}