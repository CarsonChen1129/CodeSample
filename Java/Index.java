import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
/**
 * 08722 Data Structures for Application Programmers.
 *
 * Homework Assignment 6
 * Build an index tree in different ways.
 *
 * Andrew ID: jiajunc1
 * @author Jiajun Chen(Carson)
 */
public class Index {
    /**
     * A tree of index.
     */
    private BST<Word> indexTree;

    /**
     * Parse an input text file and build an index tree using a natural alphabetical order.
     * @param fileName an input file name
     * @return an index tree
     */
    public BST<Word> buildIndex(String fileName) {
        return buildIndex(fileName, null);
    }

    /**
     * Parse an input file and build an index tree.
     * using a specific ordering among words provided by a specific comparator.
     * @param fileName an input file name
     * @param comparator a comparator
     * @return an index tree
     */
    public BST<Word> buildIndex(String fileName, Comparator<Word> comparator) {
        BufferedReader br = null;
        indexTree = new BST<Word>(comparator);
        int lineCount = 1;
        try {
            File file = new File(fileName);
            if (file != null) {
                FileReader fr = new FileReader(file);
                if (fr != null) {
                    br = new BufferedReader(fr);
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        if (comparator != null) {
                            if (comparator.getClass().getName().equals("IgnoreCase")) {
                                line = line.toLowerCase();
                            }
                        }
                        addWord(line, lineCount);
                        lineCount++;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException ie) {
            System.err.println(ie.getMessage());
        } catch (NullPointerException ne) {
            System.err.println(ne.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        return indexTree;
    }

    /**
     * Takes Word objects in a list from the first to the end.
     * and allows to rebuild an index tree using a different ordering specified by a comparator.
     * @param list a list of Word objects
     * @param comparator a comparator
     * @return an index tree
     */
    public BST<Word> buildIndex(ArrayList<Word> list, Comparator<Word> comparator) {
        if (list == null) {
            return indexTree;
        }
        indexTree = new BST<Word>(comparator);
        int length = list.size();
        for (int i = 0; i < length; ++i) {
            indexTree.insert(list.get(i));
        }
        return indexTree;
    }
    /**
     * Add words into the index tree.
     * @param string a line of words.
     * @param lineCount current line count.
     */
    private void addWord(String string, int lineCount) {
        if (string == null || string.length() < 1) {
            return;
        }
        String[] wordsFromLine = string.split("\\W");
        for (String word: wordsFromLine) {
            if (word == null || word.length() < 1) {
                continue;
            }
            if (isWord(word)) {
                // create a temporary object.
                Word tempWord = new Word(word);
                // search the bst to see if it exists
                Word searchResult = indexTree.search(tempWord);
                if (searchResult == null) {
                    // if a node with this value does not exist, add the word
                    tempWord.addToIndex(lineCount);
                    indexTree.insert(tempWord);
                } else {
                    // if the word already exist, update frequency and index.
                    searchResult.addToIndex(lineCount);
                    searchResult.setFrequency(searchResult.getFrequency() + 1);
                }
            }
        }
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
     * Sort the words in alphabetical order.
     * @param tree index tree of words.
     * @return A sorted list of words.
     */
    public ArrayList<Word> sortByAlpha(BST<Word> tree) {
        /*
         * Even though there should be no ties with regard to words in BST,
         * in the spirit of using what you wrote,
         * use AlphaFreq comparator in this method.
         */
        if (tree == null) {
            return null;
        }
        ArrayList<Word> wordList = new ArrayList<Word>();
        Iterator<Word> treeIter = tree.iterator();
        while (treeIter.hasNext()) {
            wordList.add(treeIter.next());
        }
        Collections.sort(wordList, new AlphaFreq());
        return wordList;
    }

    /**
     * Sort the words by frequency.
     * @param tree index tree of words.
     * @return A sorted list of words
     */
    public ArrayList<Word> sortByFrequency(BST<Word> tree) {
        if (tree == null) {
            return null;
        }
        ArrayList<Word> wordList = new ArrayList<Word>();
        Iterator<Word> treeIter = tree.iterator();
        while (treeIter.hasNext()) {
            wordList.add(treeIter.next());
        }
        Collections.sort(wordList, new Frequency());
        return wordList;
    }

    /**
     * Get the words with highest frequency.
     * @param tree index tree of words
     * @return a list of the words with highest frequency.
     */
    public ArrayList<Word> getHighestFrequency(BST<Word> tree) {
        if (tree == null) {
            return null;
        }
        ArrayList<Word> wordList = sortByFrequency(tree);
        ArrayList<Word> highestList = new ArrayList<Word>();
        if (wordList.isEmpty()) {
            return new ArrayList<Word>();
        }
        int index = 0;
        int highestFreq = wordList.get(index).getFrequency();
        while (index < wordList.size()) {
            highestList.add(wordList.get(index));
            index++;
            if (wordList.get(index).getFrequency() != highestFreq) {
                break;
            }
        }
        return highestList;
    }
}
