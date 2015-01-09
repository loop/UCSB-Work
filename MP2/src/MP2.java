import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MP2 {
	public static void main(String args[]) {
		try {
			MessageFilter mfFilter = new MessageFilter();
			String training = args[0];
			String testing = args[1];

			mfFilter.trainAll(training);
			mfFilter.cleanUpTraining();

			mfFilter.splitUpMessages(testing);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

@SuppressWarnings("all")
class MessageFilter {

	HashMap messageWords;
	String splitregex;
	Pattern wordregex;
	String splitline;

	public MessageFilter() {
		messageWords = new HashMap();
		splitregex = "\\W";
		wordregex = Pattern.compile("\\w+");
		splitline = "\\r?\\n";
	}

	public void trainHam(String file) throws IOException {
		String[] tokens = file.split(splitregex);
		int goodTotal = 0;

		for (int i = 0; i < tokens.length; i++) {
			String word = tokens[i].toLowerCase();
			Matcher m = wordregex.matcher(word);
			if (m.matches()) {
				goodTotal++;
				if (messageWords.containsKey(word)) {
					Word w = (Word) messageWords.get(word);
					w.countGood();
				} else {
					Word w = new Word(word);
					w.countGood();
					messageWords.put(word, w);
				}
			}
		}
	}

	public void trainSpam(String file) throws IOException {
		String[] tokens = file.split(splitregex);
		int badTotal = 0;

		for (int i = 0; i < tokens.length; i++) {
			String word = tokens[i].toLowerCase();
			Matcher m = wordregex.matcher(word);
			if (m.matches()) {
				badTotal++;
				if (messageWords.containsKey(word)) {
					Word w = (Word) messageWords.get(word);
					w.countBad();
				} else {
					Word w = new Word(word);
					w.countBad();
					messageWords.put(word, w);
				}
			}
		}
	}

	public void trainAll(String file) throws IOException {
		TextFileReader fr = new TextFileReader(file);
		String content = fr.getTextContent();
		String lines[] = content.split("\\r?\\n");
		for (int i = 0; i < lines.length; i++) {
			String firstWord = null;
			if (lines[i].contains("ham")) {
				trainHam(lines[i]);
			} else if (lines[i].contains("spam")) {
				trainSpam(lines[i]);
			}
		}
	}

	public void splitUpMessages(String file) throws IOException {
		TextFileReader fr = new TextFileReader(file);
		String content = fr.getTextContent();
		String lines[] = content.split("\\r?\\n");
		PrintWriter solution = null;
		try {
			solution = new PrintWriter("predictions.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].length() != 0) {
				if (spamAnalyser(lines[i]) == true) {
					solution.println("spam" + "\n");
				} else {
					solution.println("ham" + "\n");
				}
			}
		}
		solution.close();
	}

	public boolean spamAnalyser(String stuff) {
		ArrayList wordsThatAreInteresting = new ArrayList();

		String[] tokens = stuff.split(splitregex);

		for (int i = 0; i < tokens.length; i++) {
			String s = tokens[i].toLowerCase();
			Matcher m = wordregex.matcher(s);
			if (m.matches()) {

				Word w;

				if (messageWords.containsKey(s)) {
					w = (Word) messageWords.get(s);
				} else {
					w = new Word(s);
					w.setSpamProbability(0.4f);
				}

				int limit = 15;
				if (wordsThatAreInteresting.isEmpty()) {
					wordsThatAreInteresting.add(w);
				} else {
					for (int j = 0; j < wordsThatAreInteresting.size(); j++) {
						Word nw = (Word) wordsThatAreInteresting.get(j);
						if (w.getWord().equals(nw.getWord())) {
							break;
						} else if (w.interestingWords() > nw.interestingWords()) {
							wordsThatAreInteresting.add(j, w);
							break;
						} else if (j == wordsThatAreInteresting.size() - 1) {
							wordsThatAreInteresting.add(w);
						}
					}
				}

				while (wordsThatAreInteresting.size() > limit)
					wordsThatAreInteresting.remove(wordsThatAreInteresting
							.size() - 1);

			}
		}

		float probabilityProduct = 1.0f;
		float differenceProbabilitySpam = 1.0f;

		for (int i = 0; i < wordsThatAreInteresting.size(); i++) {
			Word w = (Word) wordsThatAreInteresting.get(i);
			probabilityProduct *= w.getSpamProbability();
			differenceProbabilitySpam *= (1.0f - w.getSpamProbability());
		}

		float probabilitySpam2 = probabilityProduct
				/ (probabilityProduct + differenceProbabilitySpam);

		if (probabilitySpam2 > 1.1f)
			return true;
		else
			return false;

	}

	public void showMessageStatistics() {
		Iterator iterator = messageWords.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			Word wWord = (Word) messageWords.get(key);
			if (wWord != null) {
				System.out.println(key + " " + wWord.getSpamProbability());
			}
		}
	}

	public void cleanUpTraining() {
		Iterator iterator = messageWords.values().iterator();
		while (iterator.hasNext()) {
			Word word = (Word) iterator.next();
			word.finalProbabilityCalculations();
		}
	}

}

class Word {
	private String word;
	private int countBad;
	private int countGood;
	private float badRating;
	private float goodRating;
	private float spamProbability;

	public Word(String s) {
		word = s;
		countBad = 0;
		countGood = 0;
		badRating = 0.0f;
		goodRating = 0.0f;
		spamProbability = 0.0f;
	}

	public void countBad() {
		countBad++;
	}

	public void countGood() {
		countGood++;
	}

	public void badProbabilityCalculation(int total) {
		if (total > 0)
			badRating = countBad / (float) total;
	}

	public void goodProbabilityCalculation(int total) {
		if (total > 0)
			goodRating = 2 * countGood / (float) total;
	}

	public void finalProbabilityCalculations() {
		if (goodRating + badRating > 0)
			spamProbability = badRating / (badRating + goodRating);
		if (spamProbability < 0.01f)
			spamProbability = 0.01f;
		else if (spamProbability > 0.99f)
			spamProbability = 0.99f;
	}

	public float interestingWords() {
		return Math.abs(0.5f - spamProbability);
	}

	public float getGoodProbability() {
		return goodRating;
	}

	public float getBadProbability() {
		return badRating;
	}

	public float getSpamProbability() {
		return spamProbability;
	}

	public void setSpamProbability(float f) {
		spamProbability = f;
	}

	public String getWord() {
		return word;
	}

}

@SuppressWarnings("all")
class TextFileReader {
	private String filename;
	private String content;

	public TextFileReader(String name) throws IOException {
		filename = name;
		readTextContent();
	}

	public void readTextContent() throws IOException {
		FileInputStream fis = new FileInputStream(filename);
		FileChannel fc = fis.getChannel();

		ByteBuffer bb = ByteBuffer.allocate((int) fc.size());
		fc.read(bb);
		fc.close();

		content = new String(bb.array());
	}

	public String getTextContent() {
		return content;
	}
}