/* --
COMP336 Lab1 Exercise
Student Name: Chan Hiu Lok Felix
Student ID: 20177897
Section:
Email: hlfchanaa@ust.hk
*/

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;
import java.io.IOException;	

public class MoreThanOneIndex
{
	private RecordManager recman;
	private HTree hashtable;

	MoreThanOneIndex(RecordManager recordmanager, String objectname) throws IOException
	{
		recman = recordmanager;
		long recid = recman.getNamedObject(objectname);
			
		if (recid != 0)
			hashtable = HTree.load(recman, recid);
		else
		{
			hashtable = HTree.createInstance(recman);
			recman.setNamedObject( objectname, hashtable.getRecid() );
		}
	}
	
	MoreThanOneIndex(String recordmanager, String objectname) throws IOException
	{
		recman = RecordManagerFactory.createRecordManager(recordmanager);
		long recid = recman.getNamedObject(objectname);
			
		if (recid != 0)
			hashtable = HTree.load(recman, recid);
		else
		{
			hashtable = HTree.createInstance(recman);
			recman.setNamedObject( objectname, hashtable.getRecid() );
		}
	}


	public void finalize() throws IOException
	{
		recman.commit();
		recman.close();				
	} 

	public void addEntry(String word, int x, int y) throws IOException
	{
		// Add a "docX Y" entry for the key "word" into hashtable
		// ADD YOUR CODES HERE
		//prevent entering same entry
		if (hashtable.get(word) != null && ((String) hashtable.get(word)).contains("doc" + x + " " + y))
		{
			return;
		}
		String original = (String) hashtable.get(word);
		String added = "doc" + x + " " + y;
		if(original == null || original.equals(""));
		else {
			added = original + " " + added;
		}
		hashtable.put(word, added);
	}
	public void addEntry(String word, String added) throws IOException
	{
		// Add a "docX Y" entry for the key "word" into hashtable
		// ADD YOUR CODES HERE
		//prevent entering same entry
		if (hashtable.get(word) != null && ((String) hashtable.get(word)).contains(added))
		{
			return;
		}
		String original = (String) hashtable.get(word);
		
		if(original == null || original.equals(""));
		else {
			added = original + " " + added;
		}
		hashtable.put(word, added);
	}
	public void addEntryFrequency(String word, String added) throws IOException
	{
		// Add a "docX Y" entry for the key "word" into hashtable
		// ADD YOUR CODES HERE
		//prevent entering same entry
		/*if (hashtable.get(word) != null && ((String) hashtable.get(word)).contains(added))
		{
			return;
		}
		String original = (String) hashtable.get(word);
		
		if(original == null || original.equals(""));
		else {
			added = original + " " + added;
		}
		hashtable.put(word, added);*/
		if(hashtable.get(word) == null) {
			hashtable.put(word, added + " 1");
		}else {
			String[] allList = ((String) hashtable.get(word)).split(" ");
			boolean haveAdded = false;
			for(int i = 0; i < allList.length; i+=2) {
				String current = allList[i];
				if(current.equals(added)) {
					int next = Integer.parseInt(allList[i+1]) + 1;
					allList[i+1] = String.valueOf(next);
					haveAdded = true;
					break;
				}
			}
			StringBuilder builder = new StringBuilder();
			for(String current : allList) {
				if(current!=allList[0])
					builder.append(" " + current);
				else
					builder.append(current);
			}
			String str = builder.toString();
			if(!haveAdded) {
				str += (" " + added + " 1");
			}
			hashtable.put(word, str);
		}
	}
	public void delEntry(String word) throws IOException
	{
		// Delete the word and its list from the hashtable
		// ADD YOUR CODES HERE
		hashtable.remove(word);
	} 
	public void printAll() throws IOException
	{
		// Print all the data in the hashtable
		// ADD YOUR CODES HERE
		FastIterator iter = hashtable.keys();
		String key;	
		while( (key = (String)iter.next())!=null)
		{
			System.out.println(key + " : " + hashtable.get(key));
		}
	}	
	
	public FastIterator getFastIterator() throws IOException {
		FastIterator iter = hashtable.keys();
		return iter;
	}
	
	public String getValue(String key) throws IOException {
		return (String) hashtable.get(key);
	}
	
	public String getValueWithDocument(String key) throws IOException {
		key = key.substring(3);
		return (String) hashtable.get(key);
	}
	
	public boolean checkEntry(String check) throws IOException{
		String original = (String) hashtable.get(check);
		return (original == null || original.equals("")) ? false : true;
	}
	
	public static void main(String[] args)
	{
		try
		{
			MoreThanOneIndex index = new MoreThanOneIndex("lab1","ht1");
	
			index.addEntry("cat", 2, 6);
			index.addEntry("dog", 1, 33);
			System.out.println("First print");
			index.printAll();
			
			index.addEntry("cat", 8, 3);
			index.addEntry("dog", 6, 73);
			index.addEntry("dog", 8, 83);
			index.addEntry("dog", 10, 5);
			index.addEntry("cat", 11, 106);
			
			index.addEntryFrequency("abc", "lmao");
			index.addEntryFrequency("abc", "haha");
			index.addEntryFrequency("abc", "lol");
			index.addEntryFrequency("abc", "haha");
			System.out.println("Second print");
			index.printAll();
			
			index.delEntry("dog");
			index.delEntry("abc");
			System.out.println("Third print");
			index.printAll();
			
			System.out.println("cat: " + index.checkEntry("cat"));
			System.out.println("dog: " + index.checkEntry("dog"));
			index.finalize();
		}
		catch(IOException ex)
		{
			System.err.println(ex.toString());
		}

	}
}