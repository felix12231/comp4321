
public class TestProgram {

	public static void main(String[] args) {
		Spider.initializeDatabase();
		Spider.crawlPages();
		Spider.getPages();
		Spider.output();
		Spider.finalizingPages();
	}

}
