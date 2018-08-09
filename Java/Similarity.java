import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 08722 Data Structures for Application Programmers.
 *
 * Homework Assignment 5
 * Calculate similarity between two documents.
 *
 * Interface from the Java Collections Framework:
 * 1. Iterator
 *
 * Data structures from the Java Collections Framework:
 * 1. HashMap
 * Purpose: to store the term frequency from input, which requires
 * a lot of search but the order does not matter.
 *
 * Andrew ID: jiajunc1
 * @author Jiajun Chen(Carson)
 */
public class Similarity {
    /**
     * constant for number 1 as a BigInteger.
     */
    private static final BigInteger ONE = BigInteger.ONE;
    /**
     * a Map as the underlying data structure.
     */
    private Map<String, BigInteger> wordMap;
    /**
     * total number of words.
     */
    private BigInteger numOfWords;
    /**
     * total number of lines.
     */
    private int numOfLines;
    /**
     * Constructs a new Similarity object with String input.
     * @param string input string
     */
    public Similarity(String string) {
        System.out.println(string);
        wordMap = new ConcurrentHashMap<String, BigInteger>();
        numOfWords = new BigInteger("0");
        numOfLines = 1;
        addWord(string);
    }
    /**
     * Constructs a new Similarity object with File input.
     * @param file input file
     */
    public Similarity(File file) {
        BufferedReader br = null;
        wordMap = new ConcurrentHashMap<String, BigInteger>();
        numOfWords = new BigInteger("0");
        try {
            if (file != null) {
                FileReader fr = new FileReader(file);
                if (fr != null) {
                    br = new BufferedReader(fr);
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        addWord(line);
                        numOfLines = numOfLines + 1;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find the file. [Abort]");
        } catch (IOException eio) {
            System.err.println("Unable to read the line. [Abort]");
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.err.println("Unable to close the reader.");
                }
            }
        }
    }
    /**
     * Check the input string and add words into the map.
     * @param string input string
     */
    private void addWord(String string) {
        if (string == null) {
            return;
        }
        String[] wordsFromText = string.split("\\W");
        for (String word: wordsFromText) {
            word.compareToIgnoreCase(word);
            if (isWord(word)) {
                String theWord = word.toLowerCase();
                numOfWords = numOfWords.add(ONE);
//                System.out.println(theWord);
                if (wordMap.containsKey(theWord)) {
                    // if a word already exist, get the word from map and add 1.
                    wordMap.put(theWord, wordMap.get(theWord).add(BigInteger.valueOf(1)));
                } else {
                    wordMap.put(theWord, BigInteger.valueOf(1));
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
     * Returns the number of lines in the input String or File.
     * @return number of lines
     */
    public int numOfLines() {
        return numOfLines;
    }
    /**
     * Returns the number of words in the input String or File.
     * @return number of words in BigInteger
     */
    public BigInteger numOfWords() {
        return new BigInteger(this.numOfWords.toString());
    }
    /**
     * Returns the number of distinct words in the input String or File.
     * @return number of distinct words
     */
    public int numOfWordsNoDups() {
        return wordMap.size();
    }
    /**
     * Calculates the Euclidean norm of the vector.
     * @return euclidean norm value
     */
    public double euclideanNorm() {
        return Math.sqrt(dotProduct(wordMap));
    }
    /**
     * Calculate the dot product between two maps (vectors).
     *
     * Since one word that does not exist in another map contributes nothing
     * to the dot product (e.g: one map has 100 word 'java', and the other map has 0,
     * then their multiple is 0), thus we only calculate the common words in one iteration,
     * which makes the running time complexity do not fall into quadratic on average.
     * @param map map that contains word frequency
     * @return dot product value
     */
    public double dotProduct(Map<String, BigInteger> map) {
        return dot(wordMap, map);
    }
    /**
     * Calculate the dot product of two frequency vectors(map).
     * @param map1 map that contains word frequency
     * @param map2 map that contains word frequency
     * @return dot product value
     */
    private double dot(Map<String, BigInteger> map1, Map<String, BigInteger> map2) {
        BigInteger sum = new BigInteger("0");
        if (map1 != null && map2 != null) {
            Iterator it = map1.entrySet().iterator();
            while (it.hasNext()) {
                Entry pair = (Entry) it.next();
                // since one word that does not exist in another map contributes
                // nothing to the dot product, thus we only calculate the common words
                if (map2.containsKey(pair.getKey())) {
                    // sum += map1[key1] * map2[key2]
                    sum = sum.add(map2.get(pair.getKey()).multiply((BigInteger) pair.getValue()));
                }
            }
        }
        return sum.doubleValue();
    }
    /**
     * Calculate the cosine similarity between two frequency vectors.
     * @param map map that contains word frequency
     * @return distance value
     */
    public double distance(Map<String, BigInteger> map) {
        double dotPro = dotProduct(map);
        if (dotPro == 0.0) {
            return Math.PI / 2;
        }
        double beforeAcos = dotPro / (euclideanNorm() * Math.sqrt(dot(map, map)));
        if (beforeAcos > 1) {
            beforeAcos = 1.0;
        }
        return Math.acos(beforeAcos);
    }
    /**
     * Returns a defensive copy of the map.
     * @return map
     */
    public Map<String, BigInteger> getMap() {
        return new ConcurrentHashMap<String, BigInteger>(wordMap);
    }
}
