import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;
/**
 * 08722 Data Structures for Application Programmers.
 *
 * Homework Assignment 6
 * A generic class to build and perform operations on Binary Search Tree.
 *
 * Andrew ID: jiajunc1
 * @author Jiajun Chen(Carson)
 * @param <T> Object type
 */
public class BST<T extends Comparable<T>> implements Iterable<T>, BSTInterface<T> {
    /**
     * Root of the binary search tree.
     */
    private Node<T> root;
    /**
     * Default comparator of the binary search tree.
     */
    private Comparator<T> comparator;

    /**
     * Construct a binary search tree without a comparator.
     */
    public BST() {
        this(null);
    }

    /**
     * Construct a binary search tree with a comparator.
     * @param comp a Comparator
     */
    public BST(Comparator<T> comp) {
        comparator = comp;
        root = null;
    }

    /**
     * Return the comparator of the binary search tree.
     * @return a Comparator
     */
    public Comparator<T> comparator() {
        return comparator;
    }
    /**
     * Use comparator to compare two objects, if the comparator is not defined, directly compare them.
     * @param n1 Object one
     * @param n2 Object two
     * @return positive number if n1 is larger, 0 if equal, negative number if n2 is larger.
     */
    public int compare(T n1, T n2) {
        if (comparator == null) {
            return n1.compareTo(n2);
        }
        return comparator.compare(n1, n2);
    }

    /**
     * Return the root object of the binary search tree.
     * @return root node.
     */
    public T getRoot() {
        if (this.root == null) {
            return null;
        }
        return new Node<T>(this.root.data).data;
    }

    /**
     * Get the height of the binary search tree.
     * @return height of the BST
     */
    public int getHeight() {
        return countHeight(this.root);
    }
    /**
     * Helper function to find the height of the BST.
     * @param node current node.
     * @return integer
     */
    private int countHeight(Node<T> node) {
        // base case: when the node is null or its children are null, return.
        if (node == null || (node.left == null && node.right == null)) {
            return 0;
        }
        // recursive cases: count the height of left subtrees and right subtrees.
        int leftHeight = countHeight(node.left);
        int rightHeight = countHeight(node.right);
        if (leftHeight >= rightHeight) {
            return leftHeight + 1;
        } else {
            return rightHeight + 1;
        }
    }

    /**
     * Get number of nodes in the BST.
     * @return number of nodes
     */
    public int getNumberOfNodes() {
        return countNumberOfNodes(this.root);
    }
    /**
     * Helper function to count the number of nodes in BST.
     * @param node current node
     * @return integer
     */
    private int countNumberOfNodes(Node<T> node) {
        // base case: when the node is null, return 0.
        if (node == null) {
            return 0;
        }
        // recursive case: count the number of nodes in its left child and right child,
        // and then plus 1.
        return countNumberOfNodes(node.left) + countNumberOfNodes(node.right) + 1;
    }

    /**
     * Given the value (object), tries to find it.
     * @param toSearch Object value to search
     * @return The value (object) of the search result. If not found, null.
     */
    @Override
    public T search(T toSearch) {
        Node<T> node = searchNode(this.root, toSearch);
        if (node == null) {
            return null;
        }
        return node.data;
    }
    /**
     * Helper function to search the binary search tree.
     * @param node current node
     * @param key the key value that we are looking for
     * @return node with specific key if found, null if not found
     */
    private Node<T> searchNode(Node<T> node, T key) {
        // base case: if the root is null or the root value is the key, return it.
        if (node == null) {
            return null;
        }
        if (compare(node.data, key) == 0) {
            return node;
        }
        // recursive case: when the key is smaller than the current root,
        // search its left children.
        // Otherwise, search its right children.
        if (compare(node.data, key) > 0) {
            return searchNode(node.left, key);
        } else {
            return searchNode(node.right, key);
        }
    }

    /**
     * Inserts a value (object) to the tree.
     * No duplicates allowed.
     * @param toInsert a value (object) to insert into the tree.
     */
    @Override
    public void insert(T toInsert) {
        this.root = insertNode(this.root, toInsert);
    }
    /**
     * Helper function to insert an object into the BST.
     * @param node current node
     * @param key key value to be added
     * @return current root
     */
    private Node<T> insertNode(Node<T> node, T key) {
        // base case: when the current node is null, insert the new node here.
        if (node == null) {
            return new Node<T>(key);
        }
        if (compare(node.data, key) == 0) {
            return node;
        }
        // recursive case: when the key is smaller than the current node,
        // search its left children to find an empty space.
        // Otherwise, search its right children.
        if (compare(node.data, key) > 0) {
            node.left = insertNode(node.left, key);
        } else if (compare(node.data, key) < 0) {
            node.right = insertNode(node.right, key);
        }
        return node;
    }

    /**
     * Get the Iterator of the binary search tree.
     * @return Iterator of the BST.
     */
    @Override
    public Iterator<T> iterator() {
        return new BSTIterator(this.root);
    }
    /**
     * Private nested class of the binary search tree class.
     * The iteration is based on in-order traversal.
     * @author Jiajun Chen(Carson)
     *
     */
    private class BSTIterator implements Iterator<T> {
        /**
         * A stack to record the nodes.
         */
        private Stack<Node<T>> stack = new Stack<Node<T>>();
        /**
         * Construct a Iterator with a root node.
         * @param node root of the bst.
         */
        BSTIterator(Node<T> node) {
            if (node == null) {
                return;
            }
            Node<T> curNode = node;
            while (curNode != null) {
                stack.push(curNode);
                curNode = curNode.left;
            }
        }
        /**
         * Check whether the iterator has reached the end.
         * @return true if the stack is not empty, false if the stack is empty.
         */
        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        /**
         * Get the next node from the stack.
         * @return node data.
         */
        @Override
        public T next() {
            Node<T> res = stack.pop();
            if (res.right != null) {
                Node<T> node = res.right;
                while (node != null) {
                    stack.push(node);
                    node = node.left;
                }
            }
            return res.data;
        }
    }

    /**
     * Private static nested node class.
     * @author Jiajun Chen(Carson)
     *
     * @param <T> object type.
     */
    private static class Node<T> {
        /**
         * Data of the Node.
         */
        private T data;
        /**
         * Left child of the current node.
         */
        private Node<T> left;
        /**
         * Right child of the current node.
         */
        private Node<T> right;

        /**
         * Construct a Node object with input data.
         * @param d data
         */
        Node(T d) {
            this(d, null, null);
        }

        /**
         * Construct a Node object with input data, left child and right child.
         * @param d data
         * @param l node of left child
         * @param r node of right child
         */
        Node(T d, Node<T> l, Node<T> r) {
            data = d;
            left = l;
            right = r;
        }

        /**
         * Return String representation of the Node object.
         * @return String representation of the Node object.
         */
        @Override
        public String toString() {
            return data.toString();
        }
    }

}
