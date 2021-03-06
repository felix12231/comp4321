package searchingRelated;
import java.util.Vector;

public class Page implements Comparable<Page>{
	private double score;
	private String pageTitle;
	private String url;
	private String lastUpdateTime;
	private int pageSize;
	private Vector<String> parentLink;
	private Vector<String> childrenLink;
	private Vector<WordWithFrequency> topFiveWord;
	
	public Page() {
		parentLink = new Vector<String>();
		childrenLink = new Vector<String>();
		topFiveWord = new Vector<WordWithFrequency>();
	}
	
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String storeString = "score: " + score + "\npage title: " + pageTitle + "\nurl: " + url + "\nlast update time: " + lastUpdateTime + "\npage size: " + pageSize + "\namount: " + parentLink.size() + " " + childrenLink.size() + "\n"
				+ "top 5 words: ";
		for(int i = 0; i < topFiveWord.size(); i++) {
			storeString += topFiveWord.get(i) + " ";
		}
		return storeString;
	}



	@Override
	public int compareTo(Page anotherPage) {
		// TODO Auto-generated method stub
		return (anotherPage.score < score) ? -1: ((anotherPage.score == score) ? 0: 1);
	}
	
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getPageTitle() {
		return pageTitle;
	}
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public Vector<String> getParentLink() {
		return parentLink;
	}
	public void addParentLink(String parentLink) {
		this.parentLink.addElement(parentLink);
	}
	public Vector<String> getChildrenLink() {
		return childrenLink;
	}
	public void addChildrenLink(String childrenLink) {
		this.childrenLink.addElement(childrenLink);
	}

	public Vector<WordWithFrequency> getTopFiveWord() {
		return topFiveWord;
	}

	public void addTopFiveWord(WordWithFrequency topFiveWord) {
		this.topFiveWord.addElement(topFiveWord);
	}
	
	
}

