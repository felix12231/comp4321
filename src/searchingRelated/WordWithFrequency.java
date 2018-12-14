package searchingRelated;

public class WordWithFrequency implements Comparable<WordWithFrequency>{
	public String word;
	public int frequency;
	
	public WordWithFrequency(String word, int frequency) {
		this.word = word;
		this.frequency = frequency;
	}
	
	public int compareTo(WordWithFrequency anotherWord) {
		return (anotherWord.frequency < frequency) ? -1: ((anotherWord.frequency == frequency) ? 0: 1);
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	@Override
	public String toString() {
		return word + " " + frequency + " ";
	}
}