/* --
COMP4321 Lab2 Exercise
Student Name:
Student ID:
Section:
Email:
*/
import java.util.Vector;
import org.htmlparser.beans.StringBean;
import org.htmlparser.util.ParserException;

import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.htmlparser.beans.LinkBean;

import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.URLConnection;
import java.text.SimpleDateFormat;



public class Crawler
{
	private String url;
	private Vector<String> stringVec;
	
	Crawler(String _url)
	{
		url = _url;
	}
	
	public Vector<String> extractWords() throws ParserException

	{
		// extract words in url and return them
		// use StringTokenizer to tokenize the result from StringBean
		// ADD YOUR CODES HERE
		Vector<String> stringForLength = new Vector<String>();
		StringBean sb;

        sb = new StringBean ();
        sb.setLinks (true);
        sb.setURL (url);
        String contentForLength = sb.getStrings();
        StringTokenizer st = new StringTokenizer(contentForLength);
        while(st.hasMoreTokens()) {
        	stringForLength.add(st.nextToken());
        }
        this.stringVec = stringForLength;
		
		try {
			Vector<String> result = new Vector<String>();
			Document doc = Jsoup.connect(url).get();
			String text = doc.body().text();
			if(!text.equals("")) {
				for(String content : text.split(" ")) {
					result.add(content);
				}
			}
			String title = doc.title();
			if(!title.equals("")) {
				for(String content : title.split(" ")) {
					result.add(content);
				}
			}
			return result;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public long extractContentLengthLong() {
		try {
			URL place = new URL(url);
			URLConnection connection = place.openConnection(); 
			long length = connection.getContentLengthLong();
			if(length == -1L) {
				Iterator<String> vecItor = this.stringVec.iterator();
				while(vecItor.hasNext()) {
					length += (long)vecItor.next().length();
				}
				System.out.println("Found length == -1L");
			}
			return length;
			
		}catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
		
	}
	
	public Vector<String> extractLinks() throws ParserException

	{
		// extract links in url and return them
		// ADD YOUR CODES HERE
		Vector<String> v_link = new Vector<String>();
		LinkBean lb = new LinkBean();
		lb.setURL(url);
		URL[] URL_array = lb.getLinks();
		for(int i=0; i<URL_array.length; i++){
			v_link.add(URL_array[i].toString());
		}
		return v_link;
	}
	
	public String extractTitle() throws IOException
	
	{
		Document doc = Jsoup.connect(url).get();
        String title = doc.title();
        return title;
	}
	
	public String extractLastModifiedDate() 
	
	{
		try {
			URL place = new URL(url);
			URLConnection  connection = place.openConnection();
			long date = connection.getLastModified();
			SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, MMMM d, yyyy");
			if(date == 0) {
				date = connection.getDate();
			}
			return dateFormatter.format(new Date(date));
		}catch (Exception e) {
			return null;
		}
		//System.out.println("Header = " + connection.getHeaderField("Last-Modified"));
	}
	
	public static void main (String[] args)
	{
		try
		{
			Crawler crawler = new Crawler("http://www.cse.ust.hk/");


			Vector<String> words = crawler.extractWords();		
			
			System.out.println("Words in "+crawler.url+":");
			for(int i = 0; i < words.size(); i++)
				System.out.println(words.get(i)+" ");
			System.out.println("\n\n");
			

	
			Vector<String> links = crawler.extractLinks();
			System.out.println("Links in "+crawler.url+":");
			for(int i = 0; i < links.size(); i++)		
				System.out.println(links.get(i));
			System.out.println("");
			
			System.out.println("Title in " + crawler.url+":");
			try{
				System.out.println(crawler.extractTitle());
			}catch(IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("\n\nLast Modified Date in " + crawler.url+":");
			System.out.println(crawler.extractLastModifiedDate());
			
			System.out.println("\n\nContent Length in " + crawler.url+":");
			System.out.println(crawler.extractContentLengthLong());
			
		}
		catch (ParserException e)
            	{
                	e.printStackTrace ();
            	}

	}
}
	