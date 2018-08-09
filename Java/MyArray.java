/**
 * An implementation of MyArray class that contains multiple operations.
 * Such as add, search, getSize, getCapacity, display, removeDups, etc.
 * @author Jiajun Chen(Carson) jiajunc1
 */
public class MyArray {
    /**
     * An empty array instance.
     */
    private static final String[] EMPTY_DATAARRAY = {};
    /**
     * The underlying String array to store data.
     */
    private String[] dataArray;
    /**
     * The size of the MyArray.
     */
    private int size;
    /**
     * The capacity of the MyArray.
     */
    private int capacity;
    /**
     * Constructor that initializes an empty data array.
     */
    public MyArray() {
        this.dataArray = new String[0];
    }
    /**
     * Constructor with initial capacity to initialize a data array.
     * @param initialCapacity the initial capacity of the data array.
     */
    public MyArray(int initialCapacity) {
        if (initialCapacity > 0) {
            this.dataArray = new String[initialCapacity];
            capacity = initialCapacity;
        } else if (initialCapacity == 0) {
            this.dataArray = EMPTY_DATAARRAY;
        }
    }
    /**
     * Appends String text into the data array.
     * @param text String to be added to the data array.
     */
    public void add(String text) {
        if (text != null) {
            if (text.matches("[a-zA-Z]+")) {
                ensureCapacity(size + 1);
                dataArray[size++] = text;
            }
        }
    }
    /**
     * Check and increase the capacity of the data array to ensure new data can be inserted.
     * @param minCapacity the minimum capacity.
     */
    private void ensureCapacity(int minCapacity) {
        int oldCapacity = dataArray.length;
        if (minCapacity > oldCapacity) {
            int newCapacity = oldCapacity * 2;
            if (oldCapacity == 0) {
                newCapacity = 1;
            }
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            capacity = newCapacity;
            copyOf(newCapacity);
        }
    }
    /**
     * Create a new data array and migrate the old data into the new one.
     * @param newCapacity new capacity of the data array.
     */
    private void copyOf(int newCapacity) {
        if (isEmpty()) {
            dataArray = new String[newCapacity];
        } else {
            String[] oldData = dataArray.clone();
            dataArray = new String[newCapacity];
            System.arraycopy(oldData, 0, dataArray, 0, oldData.length);
        }
    }
    /**
     * Check if the data array is empty.
     * @return true or false.
     */
    private boolean isEmpty() {
        return dataArray.length == 0;
    }
    /**
     * Check if a text exist in the data array.
     * @param key Text terms to be searched.
     * @return true or false.
     */
    public boolean search(String key) {
        if (key == null) {
            for (int i = 0; i < size; i++) {
                if (dataArray[i] == null) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (key.equals(dataArray[i])) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Return the number of elements in the data array.
     * @return the number of elements in the data array.
     */
    public int size() {
        return size;
    }
    /**
     * Return the capacity of the data array.
     * @return the capacity of the data array.
     */
    public int getCapacity() {
        return capacity;
    }
    /**
     * Print all words in one line with space as separator.
     */
    public void display() {
        for (int i = 0; i < size; i++) {
            System.out.print(dataArray[i] + " ");
        }
        System.out.println();
    }
    /**
     * Remove duplicates in the data array.
     */
    public void removeDups() {
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (dataArray[i].equals(dataArray[j])) {
                    remove(j);
                    j--; // update j since the size is changed;
                }
            }
        }
    }
    /**
     * Remove a text at the specified position in the data array.
     * @param index the index of the text to be removed.
     */
    private void remove(int index) {
        if (size - index - 1 > 0) {
            System.arraycopy(dataArray, index + 1, dataArray, index, size - index);
        }
        dataArray[--size] = null;
    }
}
