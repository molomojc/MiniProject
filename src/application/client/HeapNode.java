package application.client;

/**
 * Represents a node in a heap data structure.
 * Each node contains a key and an array of associated values.
 */
public class HeapNode {

    /** The key of the heap node */
    int key;

    /** The array of values associated with the heap node */
    String[] values;

    /**
     * Constructs a HeapNode with the specified key.
     * Initializes the values array with a default size of 100.
     *
     * @param key The key of the heap node.
     */
    HeapNode(int key) {
        this.key = key;
        this.values = new String[100];
    }

    /**
     * Adds a value to the heap node at the specified index.
     *
     * @param key The index at which the value should be added.
     * @param value The value to add.
     */
    void addValue(int key, String value) {
        values[0] = value; // Currently adds the value at the first index.
    }

    /**
     * Returns the key of the heap node.
     *
     * @return The key of the heap node.
     */
    public int getKey() {
        return key;
    }

    /**
     * Returns the array of values associated with the heap node.
     *
     * @return The array of values.
     */
    public String[] getValues() {
        return values;
    }
}
