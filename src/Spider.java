import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;

public class Spider {
	static String firstPage = "http://www.cse.ust.hk";
	static int maxPages = 30;
	static RecordManager recman;
	static StopStem stopStem = new StopStem("stopwords.txt");
	static Index visitedPage;
	static InvertedIndex indexToDocPos; // words to page ID and position
	static Index indexToPageURL; // page's primary key to page's URL
	static Index indexToTitle; // page's primary key to page's title
	static Index indexToLastModifiedDate; // page's primary key to page's last modified date
	static InvertedIndex indexToWordWithFrequency; // page's primary key to indexed words with frequency
	// ignore links but not numbers 
	static InvertedIndex indexToChildLink; // to-be-done
	static Index indexToPageSize; // to-be-done
	static InvertedIndex indexToParentLink; // to-be-done
	
	public static void initializeDatabase() {
		try {
			recman = RecordManagerFactory.createRecordManager("database");
			visitedPage = new Index(recman,"visitedPage");
			indexToPageURL = new Index(recman, "indexToPage");
			indexToTitle = new Index(recman, "indexToTitle");
			indexToLastModifiedDate = new Index(recman, "indexToLastModifiedDate");
			indexToWordWithFrequency = new InvertedIndex(recman, "indexToWordWithFrequency");
			indexToChildLink = new InvertedIndex(recman, "indexToChildLink");
			
			indexToDocPos = new InvertedIndex(recman, "words");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void crawlPages() {
		try {
			Vector<String> pages = new Vector<String>();
			pages.add(firstPage);
			int numPages = 0;
			System.out.println("\n\n get all pages");
			while(!pages.isEmpty() && numPages < maxPages) {
				String currentPage = pages.get(0);
				System.out.println(currentPage);
				pages.remove(0);
				if(currentPage.charAt(currentPage.length()-1) == '/') {
					currentPage = currentPage.substring(0, currentPage.length()-1);
				}
				if(visitedPage.checkEntry(currentPage)) {
					System.out.println("already visited: " + currentPage);
					continue;
				}else {
					Crawler crawler = new Crawler(currentPage);
					pages.addAll(crawler.extractLinks());
					visitedPage.addEntry(currentPage, numPages);
					indexToPageURL.addEntry(numPages, currentPage);
					indexToTitle.addEntry(numPages, crawler.extractTitle());
					indexToLastModifiedDate.addEntry(numPages, crawler.extractLastModifiedDate());
					
					Vector<String> currentPageWords = crawler.extractWords();
					int nthWord = 0;
					for(String currentWords : currentPageWords) {
						if(stopStem.isStopWord(currentWords)) {
							nthWord++;
							continue;
						}
						currentWords = stopStem.stem(currentWords);
						if(currentWords == " " || currentWords == "" || (currentWords.contains("http") && currentWords.length() > 17)) {
							nthWord++;
							continue;
						}
						indexToDocPos.addEntry(currentWords, numPages, nthWord);
						indexToWordWithFrequency.addEntryFrequency(String.valueOf(numPages), currentWords);
						nthWord++;
					}
					numPages++;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void getPages() {
		try {
			System.out.println("\n\nvisitedSite:");
			visitedPage.printAll();
			//System.out.println("\n\nwords:");
			//words.printAll();
			System.out.println("\n\nindexToPage:");
			indexToPageURL.printAll();
			System.out.println("\n\nindexToTitle:");
			indexToTitle.printAll();
			System.out.println("\n\nindexToLastModifiedDate:");
			indexToLastModifiedDate.printAll();
			System.out.println("\n\nindexToWordWithFrequency:");
			indexToWordWithFrequency.printAll();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void finalizingPages() {
		try {
			visitedPage.finalize();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] arg)
	{
		Spider.initializeDatabase();
		Spider.crawlPages();
		Spider.getPages();
		Spider.finalizingPages();
		Spider.output();
	}
	
	//To be done
	/*
	 * need: get things from database and output back to the spider_result.txt
	 * 
	 */
	public static void output() {
		String fileName = "spider_result.txt";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			
		} catch (IOException e) {
			
		}
		   
	}
}
