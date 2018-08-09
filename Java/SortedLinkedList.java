/**
 *
 * Homework 3 SortedLinkedList Implementation with Recursion.
 *
 * Andrew ID: jiajunc1
 * @author Jiajun Chen(Carson)
 */
public class SortedLinkedList implements MyListInterface {
    /**
     * The head node that indicates the first element in the singly linked list.
     */
    private Node head;
    /**
     * Nested class for Node item.
     * Andrew ID: jiajunc1
     * @author Jiajun Chen(Carson)
     */
    private class Node {
        /**
         * Data of a node.
         */
        private String data;
        /**
         * Reference to next node.
         */
        private Node next;
        /**
         * Construct a new node with specified data.
         */
        public Node() {
            this.data = null;
            this.next = null;
        }
        /**
         * Data setter.
         * @param newData data as String.
         */
        public void setData(String newData) {
            this.data = newData;
        }
        /**
         * Next node setter.
         * @param newNext next node.
         */
        public void setNext(Node newNext) {
            this.next = newNext;
        }
        /**
         * Data getter.
         * @return data as String
         */
        public String getData() {
            return data;
        }
        /**
         * Next node getter.
         * @return next node
         */
        public Node getNext() {
            return next;
        }
    }
    /**
     * Default constructor.
     */
    public SortedLinkedList() {
       this(new String[0]);
    }
    /**
     * Constructor of the sorted linked list class.
     * @param list a list of unordered elements.
     */
    public SortedLinkedList(String[] list) {
        if (list.length > 1 && list[0] != null) {
            constructorHelper(list, 0);
        }
    }
    /**
     * Helper function to add element into the linked list.
     * @param list the list of elements.
     * @param index current index.
     */
    private void constructorHelper(String[] list, int index) {
        /**
         * Recursively iterate over the list, when the index reaches the end of the list, return.
         */
        if (index == list.length) { // base case
            return;
        }
        add(list[index]);
        constructorHelper(list, index + 1); // recursive case.
    }
    /**
     * Inserts a new String.
     * No duplicates allowed and maintain the order in ascending order.
     * @param value String to be added.
     */
    @Override
    public void add(String value) {
        if (value != null && isWord(value)) {
            Node newNode = new Node();
            newNode.setData(value);
            // if the head node is null, let the new node be the head node.
            if (head == null) {
                head = newNode;
                return;
            }
            if (!contains(value)) {
                // if the new node value is smaller than the head node value, let the new node be the head node.
                if (value.compareTo(head.getData()) < 0) {
                    newNode.setNext(head);
                    head = newNode;
                    return;
                }
                // call helper function to iterate to the node that is smaller than the new node value
                // while the next node value is larger than the new node value.
                Node cursor = addHelper(head, value);
                newNode.setNext(cursor.getNext());
                cursor.setNext(newNode);
            }
        }
    }
    /**
     * Helper function to iterate to the node that is smaller than the new node value
     * while the next node value is larger than the new node value.
     * @param cur reference to the current node.
     * @param value the value that needs to be inserted.
     * @return the node before that new node that needs to be inserted.
     */
    private Node addHelper(Node cur, String value) {
        /**
         * Recursively iterate over to the last node or to the node before the new node that needs to be inserted.
         */
        if (cur.getNext() == null) { // base case
            return cur;
        }
        if (value.compareTo(cur.getData()) >= 0 && value.compareTo(cur.getNext().getData()) < 0) { // base case
            return cur;
        }
        return addHelper(cur.getNext(), value); // recursive case
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
     * Checks the size (number of data items) of the list.
     * @return the size of the list
     */
    @Override
    public int size() {
        return sizeHelper(head);
    }
    /**
     * Helper function to count the size of the linked list.
     * @param cur reference to the current node.
     * @return 1 plus values from the recursive functions.
     */
    private int sizeHelper(Node cur) {
        /**
         * Recursively sum up the number of nodes until there are no more nodes.
         */
        if (cur == null) { // base case
            return 0;
        }
        return 1 + sizeHelper(cur.getNext()); //recursive case
    }
    /**
     * Displays the values of the list.
     */
    @Override
    public void display() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        displayHelper(sb, head);
        sb.append("]");
        System.out.println(sb.toString());
    }
    /**
     * Helper function to iterate over the linked list and print each value.
     * @param sb reference for StringBuilder.
     * @param cur reference to the current node.
     */
    private void displayHelper(StringBuilder sb, Node cur) {
        /**
         * Recursively iterate over the linked list and print each value until the node is null.
         */
        if (cur == null) { // base case
            return;
        }
        sb.append(cur.getData());
        if (cur.getNext() != null) {
            sb.append(", ");
        }
        displayHelper(sb, cur.getNext()); //recursive case
    }
    /**
     * Returns true if the key value is in the list.
     * @param key String key to search
     * @return true if found, false if not found
     */
    @Override
    public boolean contains(String key) {
        return containsHelper(head, key);
    }
    /**
     * Helper function to iterate over the linked list and try to find specific value.
     * @param cur reference to the current node.
     * @param key specific value that needs to be searched.
     * @return true if found, false if not found.
     */
    private boolean containsHelper(Node cur, String key) {
        /**
         * Recursively iterate over the linked list until the desired value is found or reach the end of the list.
         */
        if (cur == null || key == null) { // base case
            return false;
        }
        if (cur.getData().equals(key)) { // base case
            return true;
        }
        return containsHelper(cur.getNext(), key); // recursive case
    }
    /**
     * Returns true is the list is empty.
     * @return true if it is empty, false if it is not empty
     */
    @Override
    public boolean isEmpty() {
        return head == null;
    }
    /**
     * Removes and returns the first String object of the list.
     * @return String object that is removed. If the list is empty, returns null
     */
    @Override
    public String removeFirst() {
        if (head == null) {
            return null;
        }
        String data = head.getData();
        head = head.getNext();
        return data;
    }
    /**
     * Removes and returns String object at the specified index.
     * @param index index to remove String object
     * @return String object that is removed
     * @throws RuntimeException for invalid index value (index < 0 || index >= size())
     */
    @Override
    public String removeAt(int index) {
        if (index < 0 || index >= size()) {
            throw new RuntimeException("Index number is not valid.");
        }
        if (index == 0) {
            return removeFirst();
            }
        return removeAtHelper(head, index);
    }
    /**
     * Helper function to iterate over the linked list and remove a node.
     * @param cur reference to the current node.
     * @param index the position of the node that needs to be removed.
     * @return String object that is removed.
     */
    private String removeAtHelper(Node cur, int index) {
        /**
         * Recursively iterate over the linked list and find the node that needs to be removed,
         * once found, remove the node and return the value of that node.
         */
        if (index == 1) { // base case
            String data = cur.getNext().getData();
            cur.setNext(cur.getNext().getNext());
            return data;
        }
        return removeAtHelper(cur.getNext(), index - 1); //recursive case
    }
}
