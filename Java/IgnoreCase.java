import java.util.Comparator;
/**
 * 08722 Data Structures for Application Programmers.
 *
 * Homework Assignment 6
 *
 * A comparator that sorts words by case insensitive alphabetical order.
 * If this comparator is passed into buildIndex method,
 * then all the words need to be converted into lower case and then added into
 * the BST.
 *
 * Andrew ID: jiajunc1
 * @author Jiajun Chen(Carson)
 */
public class IgnoreCase implements Comparator<Word> {

    /**
     * Implementation of compare method to compare Word objects by alphabet and ignore case.
     * @return positive, 0 or negative values
     */
    @Override
    public int compare(Word o1, Word o2) {
            return o1.getWord().compareToIgnoreCase(o2.getWord());
    }

}
