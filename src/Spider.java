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
	static InvertedIndex words;
	static Index indexToPage;
	
	
	public static void initializeDatabase() {
		try {
			recman = RecordManagerFactory.createRecordManager("database");
			visitedPage = new Index(recman,"visitedPage");
			indexToPage = new Index(recman, "indexToPage");
			
			words = new InvertedIndex(recman, "words");
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
					visitedPage.addEntry(currentPage, numPages + 1);
					indexToPage.addEntry(numPages, currentPage);
					Vector<String> currentPageWords = crawler.extractWords();
					int nthWord = 0;
					for(String currentWords : currentPageWords) {
						if(stopStem.isStopWord(currentWords)) {
							nthWord++;
							continue;
						}
						currentWords = stopStem.stem(currentWords);
						words.addEntry(currentWords, numPages, nthWord + 1);
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
			indexToPage.printAll();
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
	}
}
