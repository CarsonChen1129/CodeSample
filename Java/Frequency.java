import java.util.Comparator;
/**
 * 08722 Data Structures for Application Programmers.
 *
 * Homework Assignment 6
 *
 * A comparator that sorts words according to their frequencies
 * (a word with highest frequency comes first).
 *
 * Andrew ID: jiajunc1
 * @author Jiajun Chen(Carson)
 */
public class Frequency implements Comparator<Word> {

    /**
     * Implementation of compare method to compare Word objects by frequency.
     * @return positive, 0 or negative values
     */
    @Override
    public int compare(Word o1, Word o2) {
        int n = Integer.compare(o2.getFrequency(), o1.getFrequency());
        if (n == 0) {
            return o1.compareTo(o2);
        }
        return n;
    }
}
