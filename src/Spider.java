import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;

public class Spider {
	static String firstPage = "http://www.cse.ust.hk";
	static int maxPages = 30;
	static RecordManager recman;
	static StopStem stopStem = new StopStem("stopwords.txt");
	static Index visitedPage; // page's URL to primary key
	static InvertedIndex indexToDocPos; // words to page ID and position
	static Index indexToPageURL; // page's primary key to page's URL
	static Index indexToTitle; // page's primary key to page's title
	static Index indexToLastModifiedDate; // page's primary key to page's last modified date
	static MoreThanOneIndex indexToWordWithFrequency; // page's primary key to indexed words with frequency
	// ignore links but not numbers 
	static MoreThanOneIndex indexToChildLink; // to-be-done
	static Index indexToPageSize; // to-be-done
	static MoreThanOneIndex linkToParentLink; // to-be-done
	
	public static void initializeDatabase() {
		try {
			recman = RecordManagerFactory.createRecordManager("database");
			visitedPage = new Index(recman,"visitedPage");
			indexToPageURL = new Index(recman, "indexToPage");
			indexToTitle = new Index(recman, "indexToTitle");
			indexToLastModifiedDate = new Index(recman, "indexToLastModifiedDate");
			indexToWordWithFrequency = new MoreThanOneIndex(recman, "indexToWordWithFrequency");
			indexToChildLink = new MoreThanOneIndex(recman, "indexToChildLink");
			indexToPageSize = new Index(recman, "indexToPageSize");
			linkToParentLink = new MoreThanOneIndex(recman,"indexToParentLink");
			
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
			System.out.println("\nGet 30 pages");
			while(!pages.isEmpty() && numPages < maxPages) {
				String currentPage = pages.get(0);
				System.out.println(currentPage);
				pages.remove(0);
				if(currentPage.charAt(currentPage.length()-1) == '/') {
					currentPage = currentPage.substring(0, currentPage.length()-1);
				}
				if(currentPage.contains("lang=hk") || currentPage.contains("lang=cn")) {
					continue;
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
					indexToPageSize.addEntry(String.valueOf(numPages), String.valueOf(crawler.extractContentLengthLong()));
					
					int nthWord = 0;
					for(String currentWords : currentPageWords) {
						if(stopStem.isStopWord(currentWords)) {
							nthWord++;
							continue;
						}
						currentWords = stopStem.stem(currentWords);
						if(currentWords == " " || currentWords == "") {
							nthWord++;
							continue;
						}
						indexToDocPos.addEntry(currentWords, numPages, nthWord);
						indexToWordWithFrequency.addEntryFrequency(String.valueOf(numPages), currentWords);
						nthWord++;
					}
					for(String currentLink : crawler.extractLinks()) {
						indexToChildLink.addEntry(String.valueOf(numPages), currentLink);
						linkToParentLink.addEntry(currentLink, currentPage);
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
			//System.out.println("\n\nindexToChildLink");
			//indexToChildLink.printAll();
			System.out.println("\n\nindexToLastModifiedDate");
			indexToLastModifiedDate.printAll();
			System.out.println("\n\nindexToPageSize");
			indexToPageSize.printAll();
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
	//To be done
		/*
		 * need: get things from database and output back to the spider_result.txt
		 * 
		 */
	public static void output() {
		String fileName = "spider_result.txt";
		try {
			System.out.println("\n\nWriting onto spider_result.txt:");
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			FastIterator itor = indexToTitle.getFastIterator();
			String primaryKey;
			String title;
			primaryKey= (String) itor.next();
			while(primaryKey!= null) {
				System.out.println("primaryKey = "+primaryKey);
				title = indexToTitle.getValue(primaryKey);
				if(!title.equals("")) {
					writer.write(title);
					System.out.println("Ttitle: " + title);
				}else {
					writer.write("No title");
					System.out.println("No title");
				}
				writer.newLine();
				writer.write(indexToPageURL.getValue(primaryKey));
				writer.newLine();
				writer.write(indexToLastModifiedDate.getValue(primaryKey));
				writer.write(", ");
				writer.write(indexToPageSize.getValue(primaryKey)); /* to-be-updated */
				writer.newLine();
				String allS = indexToWordWithFrequency.getValue(primaryKey);
				String[] allList;
				int i;
				if(allS != null) {
					allList = allS.split(" ");
					String current, freq;
					for(i = 0; i < allList.length-2; i+=2) {
						current = allList[i];
						writer.write(current);
						freq = allList[i+1];
						writer.write(" "+freq+"; ");
					}
					// for the last keyword and frequency:
					writer.write(allList[i]);
					writer.write(" "+allList[i+1]);			
					writer.newLine();
				}else {
					writer.write("No keyword indexed");
					writer.newLine();
				}
				allS = (String) indexToChildLink.getValue(primaryKey);
				if(allS != null) {
					allList = allS.split(" ");
					for(i = 0; i < allList.length; ++i) {
						writer.write(allList[i]);
						writer.newLine();
					}
				}else {
					writer.write("No child link");
					writer.newLine();
				}
				if((primaryKey = (String) itor.next())!=null) {
					writer.write("-------------------------------------------------------------------------------------------");
					writer.newLine();
				}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		   
	}
	

	public static void main(String[] arg)
	{	
		Spider.initializeDatabase();
		Spider.crawlPages();
		Spider.getPages();
		Spider.output();
		Spider.finalizingPages();
	}
	
}	
	
