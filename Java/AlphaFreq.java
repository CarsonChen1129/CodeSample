import java.util.Comparator;
/**
 * 08722 Data Structures for Application Programmers.
 *
 * Homework Assignment 6
 *
 * A comparator that sorts words according to alphabets first and if there is a tie,
 * then words are sorted by their frequencies in ascending order
 * (a word with lowest frequency comes first)
 *
 * Andrew ID: jiajunc1
 * @author Jiajun Chen(Carson)
 */
public class AlphaFreq implements Comparator<Word> {

    /**
     * Implementation of compare method to compare Word objects by alphabet and frequency.
     * @return positive, 0 or negative values
     */
    @Override
    public int compare(Word o1, Word o2) {
        if (o1 == null && o2 != null) {
            return -1;
        } else if (o1 != null && o2 == null) {
            return 1;
        }
        int n = o1.compareTo(o2);
        if (n == 0) {
            return Integer.compare(o1.getFrequency(), o2.getFrequency());
        }
        return n;
    }
}
