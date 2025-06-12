package application.client;

/**
 * Represents an entry in a graph or search algorithm.
 * This class stores the index, cost, and heuristic value of a node.
 * It is typically used in pathfinding algorithms like A*.
 */
public class Entry {

    /** The index of the node in the graph */
    int index;

    /** The cost to reach this node */
    int cost;

    /** The heuristic value of this node */
    int heuristic;

    /**
     * Constructs an Entry with the specified index, cost, and heuristic.
     *
     * @param index The index of the node.
     * @param cost The cost to reach this node.
     * @param heuristic The heuristic value of this node.
     */
    public Entry(int index, int cost, int heuristic) {
        this.index = index;
        this.cost = cost;
        this.heuristic = heuristic;
    }
}
