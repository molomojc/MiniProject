package application.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a node in a graph with coordinates and a list of neighboring nodes.
 * This class is used to model a grid or graph structure where each node can connect to other nodes.
 * 
 */
class Node {
    int x, y; // Coordinates of the node in the graph this is pixels 
    List<Node> neighbors = new ArrayList<>();

    
    /**
	 * Constructor to create a node with specified coordinates.
	 * @param x The x-coordinate of the node.
	 * @param y The y-coordinate of the node.
	 */
    Node(int x, int y) {
        this.x = x;
        this.y = y;
    }
     
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Node)) return false;
        Node other = (Node) o;
        return this.x == other.x && this.y == other.y;
    }
   /**
    * Returns a string representation of the node's coordinates.
    * @return A string in the format "(x, y)".
    * ensures that each node is unique
    */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
