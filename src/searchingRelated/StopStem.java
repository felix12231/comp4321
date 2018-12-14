package searchingRelated;
import IRUtilities.*;

import java.io.*;
import java.util.Scanner;

import jdbm.RecordManagerFactory;

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
		if(!file.canRead()){
			System.out.println("!file.canRead()");
			//file = new File("/comp4321-testing1.0/WEB-INF/lib/stopwords.txt");
			//file = new File("/home/orbo/Desktop/COMP4321-Project/apache-tomcat-7.0.86/wtpwebapps/comp4321-testing1.0/WEB-INF/classes/searchingRelated/stopwords.txt");
			//file = new File(System.getProperty("catalina.home")+"/webapps/comp4321-testing1.0/WEB-INF/lib/stopwords.txt");
			file = new File("/home/orbo/Desktop/COMP4321-Project/apache-tomcat-7.0.86/webapps/comp4321-testing1.0/WEB-INF/lib/stopwords.txt");
			System.out.println(file.getAbsolutePath());
		}
		
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
		StopStem stopStem = new StopStem("ddstopwords.txt");
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