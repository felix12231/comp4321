/* --
COMP4321 Lab2 Exercise
Student Name:
Student ID:
Section:
Email:
*/
import java.util.Vector;
import org.htmlparser.beans.StringBean;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import java.util.StringTokenizer;
import org.htmlparser.beans.LinkBean;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.URLConnection;




public class Crawler
{
	private String url;
	Crawler(String _url)
	{
		url = _url;
	}
	public Vector<String> extractWords() throws ParserException

	{
		// extract words in url and return them
		// use StringTokenizer to tokenize the result from StringBean
		// ADD YOUR CODES HERE
		Vector<String> result = new Vector<String>();
		StringBean sb;

        sb = new StringBean ();
        sb.setLinks (true);
        sb.setURL (url);
        String content = sb.getStrings();
        StringTokenizer st = new StringTokenizer(content);
        while(st.hasMoreTokens()) {
        	result.add(st.nextToken());
        }
        return result;
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
		URLConnection connection = place.openConnection();
		return connection.getHeaderField("Last-Modified");
		}catch (Exception e) {
			return null;
		}
		//System.out.println("Header = " + connection.getHeaderField("Last-Modified"));
	}
	
	public static void main (String[] args)
	{
		try
		{
			Crawler crawler = new Crawler("http://www.cs.ust.hk/~dlee/4321/");


			Vector<String> words = crawler.extractWords();		
			
			System.out.println("Words in "+crawler.url+":");
			for(int i = 0; i < words.size(); i++)
				System.out.print(words.get(i)+" ");
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
			
		}
		catch (ParserException e)
            	{
                	e.printStackTrace ();
            	}

	}
}
	