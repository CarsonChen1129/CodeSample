import java.util.HashSet;
import java.util.Set;
/**
 * 08722 Data Structures for Application Programmers.
 *
 * Homework Assignment 5
 * A word class to store word, index tree and frequency.
 *
 * Andrew ID: jiajunc1
 * @author Jiajun Chen(Carson)
 */
public class Word implements Comparable<Word> {
    /**
     * The content of the word.
     */
    private String word;
    /**
     * A set to store line numbers of each word.
     */
    private Set<Integer> index;
    /**
     * The frequency of the word.
     */
    private int frequency;

    /**
     * Constructs a new Word object.
     */
    public Word() {
        this(null);
    }
    /**
     * Constructs a new Word object with a string.
     * @param string input string
     */
    public Word(String string) {
        setWord(string);
        if (index == null) {
            index = new HashSet<Integer>();
        }
        setFrequency(1);
    }
    /**
     * Getter of the word variable.
     * @return the word
     */
    public String getWord() {
        return word;
    }
    /**
     * Setter of the word variable.
     * @param text the word
     */
    public void setWord(String text) {
        if (isWord(text)) {
            this.word = text;
        }
    }
    /**
     * Getter of frequency variable.
     * @return frequency of the word
     */
    public int getFrequency() {
        return frequency;
    }
    /**
     * Setter of frequency variable.
     * @param freq frequency of the word
     */
    public void setFrequency(int freq) {
        this.frequency = freq;
    }
    /**
     * Simple private helper method to validate a word.
     * @param text text to check
     * @return true if valid, false if not
     */
    private boolean isWord(String text) {
        return text != null && text.matches("[a-zA-Z]+");
    }
    /**
     * Returns the set that contains all of the line numbers for the word.
     * @return a Set<Integer> that contains all of the line numbers for the word.
     */
    public Set<Integer> getIndex() {
        return this.index;
    }
    /**
     * Add the new line number for the word into the index.
     * @param line an integer that indicates a new line number for the word that should be added to the index.
     */
    public void addToIndex(Integer line) {
        index.add(line);
    }
    /**
     * Uses word to compare Word objects.
     * @return positive, 0 or negative
     */
    @Override
    public final int compareTo(Word o) {
        return getWord().compareTo(o.getWord());
    }
    /**
     * @return String representation of word objects
     */
    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getWord());
        sb.append(" ");
        sb.append(getFrequency());
        sb.append(" ");
        sb.append(getIndex().toString());
        return sb.toString();
    }
}
