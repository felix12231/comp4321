import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

public class Searcher {
	static RecordManager recman;
	static StopStem stopStem = new StopStem("stopwords.txt");
	static Index visitedPage; // page's URL to primary key
	static InvertedIndex indexToDocPos; // words to page ID and position
	static Index indexToPageURL; // page's primary key to page's URL
	static Index indexToTitle; // page's primary key to page's title
	static Index indexToLastModifiedDate; // page's primary key to page's last modified date
	static MoreThanOneIndex indexToWordWithFrequency; // page's primary key to indexed words with frequency
	// ignore links but not numbers 
	static MoreThanOneIndex indexToChildLink;
	static Index indexToPageSize;
	static MoreThanOneIndex linkToParentLink;
	
	public Searcher() {
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
	
	public Vector<Page> search(Vector<String> keywords) {
		try {
			Vector<String> keywordToDocumentWithPosition = new Vector<String>();
			for(int i = 0; i < keywords.size(); i++){
				String word = keywords.elementAt(i);
				if (!stopStem.isStopWord(word)){
					String temp = stopStem.stem(word);
					keywordToDocumentWithPosition.add(indexToDocPos.getValue(temp));
					System.out.println(indexToDocPos.getValue(temp));
				}
			}
			
			
			int docNum = visitedPage.getNumKey();
			System.out.println(docNum);
			@SuppressWarnings("unchecked")
		    Hashtable<Integer,Integer>[] tfMap = (Hashtable<Integer,Integer>[])new Hashtable<?,?>[keywordToDocumentWithPosition.size()];
			Hashtable<Integer,Double> weightMap = new Hashtable<Integer, Double>();
			for(int i = 0; i < keywordToDocumentWithPosition.size(); i++) {
				int df = 0;
				String[] listOfAppearance = keywordToDocumentWithPosition.get(i).split(" ");
				String stringStore = "";
				tfMap[i] = new Hashtable<Integer, Integer>();
				int counter = 0;
				for(int j = 0; j < listOfAppearance.length; j += 2) {
					if(stringStore.equals(listOfAppearance[j])) {
						counter++;
					}else if(j != 0){
						df++;
						stringStore = listOfAppearance[j];
						tfMap[i].put(Integer.valueOf(listOfAppearance[j-2].substring(3)), counter);
						System.out.println("tf of " + listOfAppearance[j-2] + " is " + tfMap[i].get(Integer.valueOf(listOfAppearance[j-2].substring(3))));
						
						counter = 1;
					}else if(j == 0) {
						stringStore = listOfAppearance[0];
						counter = 1;
					}
				}
				df++;
				stringStore = listOfAppearance[listOfAppearance.length-2];
				tfMap[i].put(Integer.valueOf(stringStore.substring(3)), counter);
				System.out.println("tf of " + stringStore + " is " + tfMap[i].get(Integer.valueOf(stringStore.substring(3))));
				counter=0;
				for(Integer integer : tfMap[i].keySet()) {
					double valueAdded = tfMap[i].get(integer) * Math.log(1.0*docNum/df)/Math.log(2);
					if(weightMap.containsKey(integer)) {
						Double originalWeight = weightMap.get(integer);
						weightMap.put(integer, originalWeight + valueAdded);
					}else {
						weightMap.put(integer, valueAdded);
					}
					System.out.println(integer + " " + weightMap.get(integer));
				}
				System.out.println("df= " + df);
				System.out.println("idf= " + (Math.log(1.0*docNum/df)/Math.log(2)));
			}
			Vector<Page> result = new Vector<Page>();
			for(Integer integer: weightMap.keySet()) {
				Page currentPage = new Page();
				currentPage.setScore(weightMap.get(integer));
				currentPage.setUrl(indexToPageURL.getValue(integer.toString()));
				currentPage.setPageSize(Integer.parseInt(indexToPageSize.getValue(integer.toString())));
				currentPage.setPageTitle(indexToTitle.getValue(integer.toString()));
				currentPage.setLastUpdateTime(indexToLastModifiedDate.getValue(integer.toString()));
				//child link and parent link are not added.
				//the way of calculating the score shd still be changed. currently is using tf * idf.
				result.add(currentPage);
			}
			Collections.sort(result); //sort from largest to smallest
			return result;
			
			
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Searcher abc = new Searcher();
		Vector<String> stringToBeSearched = new Vector<String>();
		stringToBeSearched.add("Professor");
		stringToBeSearched.add("Chan");
		stringToBeSearched.add("HKUST");
		System.out.println(abc.search(stringToBeSearched));
	}

}
