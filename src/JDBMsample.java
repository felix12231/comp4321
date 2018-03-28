import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;


public class JDBMsample
{
	public static void main(String[] args)
	{
		try
		{
			RecordManager recman;
			HTree hashtable;
			recman = RecordManagerFactory.createRecordManager("testRM");
			long recid = recman.getNamedObject("ht1");
			if (recid != 0)
			{
				hashtable = HTree.load(recman, recid);		
			}
			else
			{
				hashtable = HTree.createInstance(recman);
				recman.setNamedObject( "ht1", hashtable.getRecid() );
			}
		
			hashtable.put("key1", "context 1");
			hashtable.put("key2", "context 2");
			hashtable.put("key3", "context 3");
            hashtable.put("key4", "context 4");
		
			System.out.println( hashtable.get("key3"));
		
			hashtable.remove( "key2" );
        
			FastIterator iter = hashtable.keys();
			String key;	
			while( (key = (String)iter.next())!=null)
			{
				System.out.println(key + " : " + hashtable.get(key));
			}
	
			recman.commit();
			
			recman.close();
		}
		catch(java.io.IOException ex)
		{
			System.err.println(ex.toString());
		}

	}
}