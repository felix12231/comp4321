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



public class Index
{
	private RecordManager recman;
	private HTree hashtable;

	Index(String recordmanager, String objectname) throws IOException
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
	
	Index(RecordManager recordmanager, String objectname) throws IOException
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


	public void finalize() throws IOException
	{
		recman.commit();
		recman.close();				
	} 

	public void addEntry(String word, int x) throws IOException
	{
		// Add a "docX Y" entry for the key "word" into hashtable
		// ADD YOUR CODES HERE
		if(!checkEntry(word))
			hashtable.put(word, ""+x);
	}
	public void addEntry(int word, String x) throws IOException
	{
		// Add a "docX Y" entry for the key "word" into hashtable
		// ADD YOUR CODES HERE
		if(!checkEntry(""+word))
			hashtable.put("" + word, x);
	}
	public void addEntry(String word, String x) throws IOException
	{
		// Add a "docX Y" entry for the key "word" into hashtable
		// ADD YOUR CODES HERE
		if(!checkEntry(word))
			hashtable.put(word, x);
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
	
	public int getNumKey() throws IOException {
		FastIterator iter = hashtable.keys();
		int numKey = 0;
		while(iter.next()!=null) {
			numKey++;
		}
		return numKey;
	}
	
	public boolean checkEntry(String check) throws IOException{
		String original = (String) hashtable.get(check);
		return (original == null || original.equals("")) ? false : true;
	}
	public boolean checkEntry(int check) throws IOException{
		String original = (String) hashtable.get("" + check);
		return (original == null || original.equals("")) ? false : true;
	}
	
	public static void main(String[] args)
	{
		try
		{
			Index index = new Index("indexer","ht1");
			System.out.println("printPrevious");
			index.printAll();
	
			index.addEntry("cat", 2);
			index.addEntry("dog", 1);
			System.out.println("First print");
			index.printAll();
			
			index.addEntry("cat", 8);
			index.addEntry("dog", 6);
			index.addEntry("dog", 8);
			index.addEntry("dog", 10);
			index.addEntry("cat", 11);
			System.out.println("Second print");
			index.printAll();
			
			index.delEntry("dog");
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