import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

/**
 * 08722 Data Structures for Application Programmers.
 *
 * Homework Assignment 2 Solve Josephus problem
 * with different data structures
 * and different algorithms and compare running times
 *
 * Andrew ID: jiajunc1
 * @author Jiajun Chen(Carson)
 */
public class Josephus {

    /**
     * Uses ArrayDeque class as Queue/Deque to find the survivor's position.
     * @param size Number of people in the circle that is bigger than 0
     * @param rotation Elimination order in the circle. The value has to be greater than 0
     * @return The position value of the survivor
     */
    public int playWithAD(int size, int rotation) {
        if (size < 1 || rotation < 1) {
            throw new RuntimeException("Not possible to find the survivor.");
        }
        Deque<Integer> people = new ArrayDeque<Integer>();
        for (int i = 1; i < size + 1; i++) {
            people.addLast(i);
        }
        while (people.size() > 1) {
            for (int j = 0; j < rotation - 1; j++) {
                people.addLast(people.removeFirst());
            }
            people.removeFirst();
        }
        return people.removeFirst();
    }

    /**
     * Uses LinkedList class as Queue/Deque to find the survivor's position.
     * @param size Number of people in the circle that is bigger than 0
     * @param rotation Elimination order in the circle. The value has to be greater than 0
     * @return The position value of the survivor
     */
    public int playWithLL(int size, int rotation) {
        if (size < 1 || rotation < 1) {
            throw new RuntimeException("Not possible to find the survivor.");
        }
        // Cannot use List here since the Stack&Queue implementation only belongs to LinkedList
        LinkedList<Integer> people = new LinkedList<Integer>();
        for (int i = 1; i < size + 1; i++) {
            people.offer(i);
        }
        while (people.size() > 1) {
            for (int j = 0; j < rotation - 1; j++) {
                people.offer(people.pollFirst());
            }
            people.pollFirst();
        }
        return people.pollFirst();
    }

    /**
     * Uses LinkedList class to find the survivor's position.
     * However, do NOT use the LinkedList as Queue/Deque
     * Instead, use the LinkedList as "List"
     * That means, it uses index value to find and remove a person to be executed in the circle
     *
     * Note: Think carefully about this method!!
     * When in doubt, please visit one of the office hours!!
     *
     * @param size Number of people in the circle that is bigger than 0
     * @param rotation Elimination order in the circle. The value has to be greater than 0
     * @return The position value of the survivor
     */
    public int playWithLLAt(int size, int rotation) {
        if (size < 1 || rotation < 1) {
            throw new RuntimeException("Not possible to find the survivor.");
        }
        LinkedList<Integer> people = new LinkedList<Integer>();
        for (int i = 1; i < size + 1; i++) {
            people.add(i);
        }
        int position = 0;
        while (people.size() > 1) {
            position = (position + rotation - 1) % people.size();
            people.remove(position);
        }
        return people.get(0);
    }
    
    /**
     * Uses Recursion technique to find the survivor's position(Will Stack Overflow).
     * @param size Number of people in the circle that is bigger than 0
     * @param rotation Elimination order in the circle. The value has to be greater than 0
     * @return The position value of the survivor
     */
    public int playWithRecursion(int size, int rotation) {
        if (size < 1 || rotation < 1) {
            throw new RuntimeException("Not possible to find the survivor.");
        }
        if (size == 1) {
            return 1;
        } else {
            return (playWithRecursion(size - 1, rotation) + rotation - 1) % size + 1;
        }
    }

}
