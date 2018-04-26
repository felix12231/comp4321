package searchingRelated;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
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
			Vector<Integer> moreThanOneWord = new Vector<Integer>();
			Vector<String> keywordToDocumentWithPosition = new Vector<String>();
			for(int i = 0; i < keywords.size(); i++){
				String word = keywords.elementAt(i);
				if(word.contains(" ")){
					moreThanOneWord.add(i);
					continue;
				}
				if (!stopStem.isStopWord(word)){
					String temp = stopStem.stem(word);
					if(indexToDocPos.getValue(temp) == null) {
						continue;
					}
					keywordToDocumentWithPosition.add(indexToDocPos.getValue(temp));
					System.out.println(indexToDocPos.getValue(temp));
				}
			}
			if(keywordToDocumentWithPosition.size() == 0 && moreThanOneWord.size() == 0) {
				Vector<Page> result = new Vector<Page>();
				return result;
			}
			
			int docNum = visitedPage.getNumKey();
			System.out.println(docNum);
			@SuppressWarnings("unchecked")
			Hashtable<Integer,Double>[] tfxidfMap = (Hashtable<Integer,Double>[])new Hashtable<?,?>[keywordToDocumentWithPosition.size() + moreThanOneWord.size()];
			Vector<Integer> allPages = new Vector<Integer>();
			for(int i = 0; i < keywordToDocumentWithPosition.size() + moreThanOneWord.size(); i++) {
				if(moreThanOneWord.contains(i)) {
					boolean haveStopWord = false;
					String[] wordList = keywords.elementAt(i).split(" ");
					for(int j = 0; j < wordList.length; j++) {
						if (stopStem.isStopWord(wordList[j])){
							haveStopWord = true;
							break;
						}else {
							wordList[j] = stopStem.stem(wordList[j]);
						}
					}
					if(haveStopWord) {
						tfxidfMap[i] = new Hashtable<Integer, Double>();
					}else {
						Vector<String> store = new Vector<String>(Arrays.asList(indexToDocPos.getValue(wordList[0]).split(" ")));
						System.out.println(keywords.elementAt(i));
						for(int j = 1; j < wordList.length; j++) {
							Vector<String> currentIndexList = new Vector<String>(Arrays.asList(indexToDocPos.getValue(wordList[j]).split(" ")));
							int currentIndexListPosition = 0;
							for(int k = 0; k < store.size() && currentIndexListPosition < currentIndexList.size(); ) {
								if(Integer.parseInt(store.elementAt(k).substring(3)) < Integer.parseInt(currentIndexList.elementAt(currentIndexListPosition).substring(3))) {
									store.removeElementAt(k);			//remove the document
									store.removeElementAt(k);			//remove the index
								}else if(Integer.parseInt(store.elementAt(k).substring(3)) > Integer.parseInt(currentIndexList.elementAt(currentIndexListPosition).substring(3))) {
									currentIndexListPosition += 2;
								}else			//same document
									if(Integer.parseInt(store.elementAt(k+1)) > Integer.parseInt(currentIndexList.elementAt(currentIndexListPosition+1)) - j) {
										store.removeElementAt(k);		//remove the document
										store.removeElementAt(k);		//remove the index
								}else if(Integer.parseInt(store.elementAt(k+1)) < Integer.parseInt(currentIndexList.elementAt(currentIndexListPosition+1)) - j) {
									currentIndexListPosition += 2;
								}else {  		//have the phase
									k += 2;
									currentIndexListPosition += 2;
								}
							}
						} // so after the for loop should only have the documents and the indexPosition that have the whole phase
						if(store.size() == 0) {
							continue;
						}
						String stringStore = "";
						Hashtable<Integer,Integer> tfMap = new Hashtable<Integer, Integer>();
						tfxidfMap[i] = new Hashtable<Integer, Double>();
						int counter = 0;
						int df = 0;
						for(int j = 0; j < store.size(); j += 2) {
							if(stringStore.equals(store.elementAt(j))) {
								counter++;
							}else if(j != 0){
								df++;
								stringStore = store.elementAt(j);
								tfMap.put(Integer.valueOf(store.elementAt(j-2).substring(3)), counter);
								System.out.println("tf of " + store.elementAt(j-2) + " is " + tfMap.get(Integer.valueOf(store.elementAt(j-2).substring(3))));
								counter = 1;
							}else if(j == 0) {
								stringStore = store.elementAt(0);
								counter = 1;
							}
						}
						df++;
						stringStore = store.elementAt(store.size()-2);
						tfMap.put(Integer.valueOf(stringStore.substring(3)), counter);
						System.out.println("tf of " + stringStore + " is " + tfMap.get(Integer.valueOf(stringStore.substring(3))));
						counter=0;
						for(Integer integer : tfMap.keySet()) {
							if(!allPages.contains(integer)) {
								allPages.add(integer);
							}
							double tfxidf = tfMap.get(integer) * Math.log(1.0*docNum/df)/Math.log(2); //tf*idf, i is ith word while integer is the document number
							tfxidfMap[i].put(integer, tfxidf);
							System.out.println(integer + " " + tfxidfMap[i].get(integer));
						}
						System.out.println("df= " + df);
						System.out.println("idf= " + (Math.log(1.0*docNum/df)/Math.log(2)));
					}
					continue;
				}
				int df = 0;
				String[] listOfAppearance = keywordToDocumentWithPosition.get(i).split(" ");
				String stringStore = "";
				Hashtable<Integer,Integer> tfMap = new Hashtable<Integer, Integer>();
				tfxidfMap[i] = new Hashtable<Integer, Double>();
				int counter = 0;
				if(listOfAppearance.length == 0)
					continue;
				for(int j = 0; j < listOfAppearance.length; j += 2) {
					if(stringStore.equals(listOfAppearance[j])) {
						counter++;
					}else if(j != 0){
						df++;
						stringStore = listOfAppearance[j];
						tfMap.put(Integer.valueOf(listOfAppearance[j-2].substring(3)), counter);
						System.out.println("tf of " + listOfAppearance[j-2] + " is " + tfMap.get(Integer.valueOf(listOfAppearance[j-2].substring(3))));
						
						counter = 1;
					}else if(j == 0) {
						stringStore = listOfAppearance[0];
						counter = 1;
					}
				}
				df++;
				stringStore = listOfAppearance[listOfAppearance.length-2];
				tfMap.put(Integer.valueOf(stringStore.substring(3)), counter);
				System.out.println("tf of " + stringStore + " is " + tfMap.get(Integer.valueOf(stringStore.substring(3))));
				counter=0;
				for(Integer integer : tfMap.keySet()) {
					if(!allPages.contains(integer)) {
						allPages.add(integer);
					}
					double tfxidf = tfMap.get(integer) * Math.log(1.0*docNum/df)/Math.log(2); //tf*idf, i is ith word while integer is the document number
					tfxidfMap[i].put(integer, tfxidf);
					System.out.println(integer + " " + tfxidfMap[i].get(integer));
				}
				System.out.println("df= " + df);
				System.out.println("idf= " + (Math.log(1.0*docNum/df)/Math.log(2)));
			}
			Vector<Page> result = new Vector<Page>();
			for(Integer integer: allPages) {
				String store = indexToWordWithFrequency.getValue(integer.toString());
				String[] listOfWord = store.split(" ");
				int maxNum = 0;
				for(int i = 1; i < listOfWord.length; i+=2) {
					int currentNum = Integer.parseInt(listOfWord[i]);
					if(currentNum > maxNum) {
						maxNum = currentNum;
					}
				}
				//below is calculating cosineSimilarity, page by page
				double sumDQ = 0;
				double sumDk = 0;
				double sumQk = 0;
				int titleMatch = 0;
				for(int i = 0; i < tfxidfMap.length; i++) {
					double currentQk = 1;			//please note that currently assume that no repeat term in query
					sumQk += currentQk*currentQk;
					if(!tfxidfMap[i].containsKey(integer))
						continue;
					double currentDk = tfxidfMap[i].get(integer);
					sumDk += currentDk*currentDk;
					sumDQ += currentDk*currentQk;
					if(indexToTitle.getValue(integer.toString()).contains(keywords.get(i))) {
						titleMatch++;
					}
				}
				sumDk = Math.sqrt(sumDk);
				sumQk = Math.sqrt(sumQk);
				double cosineSimilarity = (sumDQ / sumDk) / sumQk + titleMatch;
				System.out.println(sumDk + " " + sumQk + " " + sumDQ + " " + cosineSimilarity);
				Page currentPage = new Page();
				//create
				currentPage.setScore(cosineSimilarity);
				currentPage.setUrl(indexToPageURL.getValue(integer.toString()));
				currentPage.setPageSize(Integer.parseInt(indexToPageSize.getValue(integer.toString())));
				currentPage.setPageTitle(indexToTitle.getValue(integer.toString()));
				currentPage.setLastUpdateTime(indexToLastModifiedDate.getValue(integer.toString()));
				//child link and parent link are added below
				if(indexToChildLink.checkEntry(integer.toString())){
					String[] childLinks = indexToChildLink.getValue(integer.toString()).split(" ");
					for(String childLink : childLinks) {
						currentPage.addChildrenLink(childLink);
					}
				}
				//please help check link 179-184, and the output, the first number after amount is amount of parentLink
				if(linkToParentLink.checkEntry(indexToPageURL.getValue(integer.toString()))){		//seems have problem, request for checking
					String[] parentLinks = linkToParentLink.getValue(indexToPageURL.getValue(integer.toString())).split(" ");
					for(String parentLink : parentLinks) {
						currentPage.addParentLink(parentLink);
					}
				}
				result.add(currentPage);
			}
			Collections.sort(result); //sort from largest to smallest
			return result;
			
			
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String whereIsStopWord() {
		File file = new File("comp4321/stopwords.txt");
		return file.getAbsolutePath();
	}
	
	public void getAllParentLink() {
		try {
		linkToParentLink.printAll();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String string1 = "Professor Chan Fintech";

	    String[] str1 = string1.split(" ");
		List<String> list = Arrays.asList(str1);
	    Vector<String> vector = new Vector<String>(list);
	    vector.add("Hong Kong");
		for(int i=0; i< vector.size(); i++){
			System.out.println(vector.get(i) + "<br/>");
	    }

	    Searcher se = new Searcher();
		Vector<Page> result = se.search(vector);
		System.out.println(result);
		
		System.out.println(se.whereIsStopWord());
		//se.getAllParentLink();
	}

}
