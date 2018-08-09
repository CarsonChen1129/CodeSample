/**
 * 08722 Data Structures for Application Programmers.
 *
 * Homework Assignment 4
 * HashTable Implementation with linear probing
 *
 * Andrew ID: jiajunc1
 * @author Jiajun Chen(Carson)
 */
public class MyHashTable implements MyHTInterface {
    /**
     * constant for default capacity of the array.
     */
    private static final int DEFAULT_CAPACITY = 10;
    /**
     * constant for default load factor.
     */
    private static final double DEFAULT_LOAD_FACTOR = 0.5;
    /**
     * constant for deleted value as a flag.
     */
    private static final String DELETED = "#DEL#";
    /**
     * constant of the base number for calculating hash code.
     */
    private static final int BASE_NUMBER = 27;
    /**
     * DataItem array as the underlying data structure.
     */
    private DataItem[] array;
    /**
     * total number of elements in the hash table.
     */
    private int totalSize;
    /**
     * distinct number of elements in the hash table.
     */
    private int distinctSize;
    /**
     * the hash table is rehashed when its size exceeds this threshold.
     */
    private int threshold;
    /**
     * total number of collisions.
     */
    private int numberOfCollision;
    /**
     * Constructs a new, empty hash table with default capacity.
     */
    public MyHashTable() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Constructs a new, empty hash table with the specified initial capacity and default load factor.
     * @param initialCapacity the initial capacity of the hash table.
     */
    public MyHashTable(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new RuntimeException("Initial capacity cannot be less than or equal to 0");
        } else {
            array = new DataItem[initialCapacity];
        }
        totalSize = 0;
        distinctSize = 0;
        threshold = (int) (initialCapacity * DEFAULT_LOAD_FACTOR);
        numberOfCollision = 0;
    }

    /**
     * Inserts a new String value (word).
     * Frequency of each word to be stored too.
     * @param value String value to add
     */
    @Override
    public void insert(String value) {
        if (value == null) {
            return;
        }
        // In case of non-words, ignore them.
        if (!isWord(value)) {
            return;
        }
        int index = hashValue(value);
        if (array[index] == null) {
            DataItem item = new DataItem();
            item.setValue(value);
            item.setFrequency(1);
            array[index] = item;
            distinctSize++;
            totalSize++;
            // threshold = capacity * load_factor
            if (distinctSize > threshold) {
                rehash();
            }
            return;
        }
        int count = 0;
        while (count < array.length) {
            DataItem item = array[index];
            if (item != null) {
                if (item.getValue().equals(value)) {
                    int frequency = array[index].getFrequency() + 1;
                    array[index].setFrequency(frequency);
                    totalSize++;
                    // threshold = capacity * load_factor
                    if (distinctSize > threshold) {
                        rehash();
                    }
                    return;
                }
            }
            count++;
            index++;
            index = (index) % array.length;
        }
        // in case the value does not exist in the array
        DataItem item = new DataItem();
        item.setValue(value);
        item.setFrequency(1);
        int spotIndex = index;
        boolean collisionCheck = false;
        while (array[spotIndex] != null && !array[spotIndex].getValue().equals(DELETED)) {
            int spotHash = hashValue(array[spotIndex].getValue());
            if (index == spotHash && !array[spotIndex].getValue().equals(value)) {
                collisionCheck = true;
            }
            spotIndex++;
            spotIndex = (spotIndex) % array.length;
        }
        if (collisionCheck) {
            numberOfCollision++;
        }
        array[spotIndex] = item;
        distinctSize++;
        totalSize++;
        // threshold = capacity * load_factor
        if (distinctSize > threshold) {
            rehash();
        }
    }
    /**
     * Simple private helper method to validate a word.
     * @param text text to check
     * @return true if valid, false if not
     */
    private boolean isWord(String text) {
        return text.matches("[a-zA-Z]+");
    }
    /**
     * Returns the size, number of items, of the table.
     * @return the number of items in the table
     */
    @Override
    public int size() {
        return distinctSize;
    }
    /**
     * Displays the values of the table.
     * If an index is empty, it shows **
     * If previously existed data item got deleted, then it should show #DEL#
     */
    @Override
    public void display() {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                System.out.print(" **");
            } else if (array[i].getValue().equals(DELETED)) {
                System.out.print(" " + DELETED);
            } else {
                System.out.print(" " + array[i].toString());
            }
        }
        System.out.println();
    }
    /**
     * Returns true if value is contained in the table.
     * @param key String key value to search
     * @return true if found, false if not found.
     */
    @Override
    public boolean contains(String key) {
        if (key == null) {
            return false;
        }
        int index = hashValue(key);
        int count = 0;
        while (count < array.length) {
            DataItem item = array[index];
            if (item != null) {
                if (item.getValue().equals(key)) {
                    return true;
                }
            }
            count++;
            index++;
            index = (index) % array.length;
        }
        return false;
    }
    /**
     * Returns the number of collisions in relation to insert and rehash.
     * When rehashing process happens, the number of collisions should be properly updated.
     *
     * The definition of collision is "two different keys map to the same hash value."
     * Be careful with the situation where you could overcount.
     * Try to think as if you are using separate chaining.
     * "How would you count the number of collisions?" when using separate chaining.
     * @return number of collisions
     */
    @Override
    public int numOfCollisions() {
        return numberOfCollision;
    }
    /**
     * Returns the hash value of a String.
     * Assume that String value is going to be a word with all lowercase letters.
     * @param value value for which the hash value should be calculated
     * @return int hash value of a String
     */
    @Override
    public int hashValue(String value) {
        int hash = hashFunc(value);
//        System.out.println("hash value is:" + hash);
        return hash;
    }
    /**
     * Returns the frequency of a key String.
     * @param key string value to find its frequency
     * @return frequency value if found. If not found, return 0
     */
    @Override
    public int showFrequency(String key) {
        if (key == null) {
            return 0;
        }
        int index = hashValue(key);
        int count = 0;
        while (count < array.length) {
            DataItem item = array[index];
            if (item != null) {
                if (item.getValue().equals(key)) {
                    return item.getFrequency();
                }
            }
            count++;
            index++;
            index = (index) % array.length;
        }
        return 0;
    }
    /**
     * Removes and returns removed value.
     * @param key String to remove
     * @return value that is removed. If not found, return null
     */
    @Override
    public String remove(String key) {
        if (!contains(key)) {
            return null;
        }
        int index = hashValue(key);
        int count = 0;
        while (count < array.length) {
            DataItem item = array[index];
            if (item != null) {
                if (item.getValue().equals(key)) {
                    String removedKey = item.getValue();
                    item.setValue(DELETED);
                    totalSize--;
                    distinctSize--;
                    return removedKey;
                }
            }
            index++;
            index = (index) % array.length;
            count++;
        }
        return null;
    }
    /**
     * return the absolute value of a number.
     * @param num an integer number
     * @return the absolute value the input number
     */
    private int abs(int num) {
        if (num <= 0) {
            return 0 - num;
        } else {
            return num;
        }
    }
    /**
     * Instead of using String's hashCode, you are to implement your own here.
     * You need to take the table length into your account in this method.
     *
     * In other words, you are to combine the following two steps into one step.
     * 1. converting Object into integer value
     * 2. compress into the table using modular hashing (division method)
     *
     * Helper method to hash a string for English lowercase alphabet and blank,
     * we have 27 total. But, you can assume that blank will not be added into
     * your table. Refer to the instructions for the definition of words.
     *
     * For example, "cats" : 3*27^3 + 1*27^2 + 20*27^1 + 19*27^0 = 60,337
     *
     * But, to make the hash process faster, Horner's method should be applied as follows;
     *
     * var4*n^4 + var3*n^3 + var2*n^2 + var1*n^1 + var0*n^0 can be rewritten as
     * (((var4*n + var3)*n + var2)*n + var1)*n + var0
     *
     * Note: You must use 27 for this homework.
     *
     * However, if you have time, I would encourage you to try with other
     * constant values than 27 and compare the results but it is not required.
     * @param input input string for which the hash value needs to be calculated
     * @return int hash value of the input string
     */
    private int hashFunc(String input) {
        int n = BASE_NUMBER;
        StringBuilder sb = new StringBuilder(input);
        int hash = 0;
        for (int i = 0; i < sb.length(); i++) {
            hash = (n * hash + (sb.charAt(i) - 'a' + 1)) % array.length;
        }
        return abs(hash);
    }
    /**
     * Tests if an input number is a prime number.
     * @param num an integer number
     * @return true if the number is prime, false if the number is not prime
     */
    private boolean isPrime(int num) {
        if (num == 2 || num == 3) {
            return true;
        }
        if (num % 2 == 0) {
            return false;
        }
        for (int i = 3; i * i < num; i += 2) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }
    /**
     * Get the smallest prime number that is larger than the given number.
     * @param num an integer number
     * @return a prime number
     */
    private int getNearestPrime(int num) {
        while (!isPrime(num)) {
            ++num;
        }
        return num;
    }

    /**
     * Doubles array length and rehash items whenever the load factor is reached.
     */
    private void rehash() {
        if (totalSize == 0) {
            return;
        }
        int oldCapacity = array.length;
        DataItem[] oldTable = array;
        int newCapacity = getNearestPrime(oldCapacity * 2);
        DataItem[] newTable = new DataItem[newCapacity];
        threshold = (int) (newCapacity * DEFAULT_LOAD_FACTOR);
        array = newTable;
        numberOfCollision = 0;
        System.out.println("Rehashing " + distinctSize + " items, new length is " + newCapacity);
        for (int i = 0; i < oldCapacity; i++) {
            DataItem item = oldTable[i];
            if (item != null) {
                if (!item.getValue().equals(DELETED)) {
                    int indexHash = hashValue(item.getValue());
                    int spotIndex = indexHash;
                    boolean collisionCheck = false;
                    while (array[spotIndex] != null) {
                        int spotHash = hashValue(array[spotIndex].getValue());
                        if (indexHash == spotHash && !array[spotIndex].getValue().equals(item.getValue())) {
                            collisionCheck = true;
                        }
                        spotIndex++;
                        spotIndex = (spotIndex) % array.length;
                    }
                    if (collisionCheck) {
                        numberOfCollision++;
                    }
                    newTable[spotIndex] = item;
                }
            }
        }
    }

    /**
     * private static data item nested class.
     */
    private static class DataItem {
        /**
         * String value.
         */
        private String value;
        /**
         * String value's frequency.
         */
        private int frequency;

        /**
         * Constructor for DataItem object.
         */
        public DataItem() { }
        /**
         * Return the value of the DataItem object.
         * @return key value as a String
         */
        public String getValue() {
            return value;
        }
        /**
         * Return the frequency of the DataItem object.
         * @return frequency value as an int
         */
        public int getFrequency() {
            return frequency;
        }
        /**
         * Set the key value of the DataItem object.
         * @param val key value as a String
         */
        public void setValue(String val) {
            this.value = val;
        }
        /**
         * Set the frequency value of the DataItem object.
         * @param freq frequency value as a int
         */
        public void setFrequency(int freq) {
            this.frequency = freq;
        }
        /**
         * Returns string representation of DataItem object.
         * @return String value of DataItem Object.
         */
        @Override
        public String toString() {
            return "[" + value + ", " + frequency + "]";
        }
    }
}
