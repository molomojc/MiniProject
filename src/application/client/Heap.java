package application.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Represents a Heap data structure implemented using an ArrayList.
 * This class provides methods for heap operations such as insertion, deletion, and searching.
 * It also supports reading data from a file and implementing the A* search algorithm.
 * 
 * @author JM Molomo
 * @author D MASINE
 * @author A NKANYANA
 * @author DN MBOYI
 */
public class Heap {

    /** The underlying ArrayList to store heap nodes */
    ArrayList<HeapNode> heap;

    /** Array to store data read from a file */
    public String[] arr;

    /** List to store details read from a file */
    ArrayList<String> details;

    /**
     * Constructs an empty Heap instance.
     */
    public Heap() {
        heap = new ArrayList<>();
        details = new ArrayList<>();
    }

    /**
     * Returns the index of the parent node for a given index.
     *
     * @param x The index of the current node.
     * @return The index of the parent node.
     */
    public int parent(int x) {
        return (x - 1) / 2;
    }

    /**
     * Returns the index of the left child node for a given index.
     *
     * @param x The index of the current node.
     * @return The index of the left child node.
     */
    public int leftChild(int x) {
        return 2 * x + 1;
    }

    /**
     * Returns the index of the right child node for a given index.
     *
     * @param x The index of the current node.
     * @return The index of the right child node.
     */
    public int rightChild(int x) {
        return 2 * x + 2;
    }

    /**
     * Checks if the current node has a left child.
     *
     * @param x The index of the current node.
     * @return True if the node has a left child, false otherwise.
     */
    public boolean hasLeft(int x) {
        return leftChild(x) < heap.size();
    }

    /**
     * Checks if the current node has a right child.
     *
     * @param x The index of the current node.
     * @return True if the node has a right child, false otherwise.
     */
    public boolean hasRight(int x) {
        return rightChild(x) < heap.size();
    }

    /**
     * Returns the size of the heap.
     *
     * @return The number of elements in the heap.
     */
    public int size() {
        return heap.size();
    }

    /**
     * Checks if the heap is empty.
     *
     * @return True if the heap is empty, false otherwise.
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Swaps two nodes in the heap.
     *
     * @param hT The heap ArrayList.
     * @param i The index of the first node.
     * @param j The index of the second node.
     */
    public void swap(ArrayList<HeapNode> hT, int i, int j) {
        HeapNode temp = hT.get(i);
        hT.set(i, hT.get(j));
        hT.set(j, temp);
    }

    /**
     * Restores the heap property by moving a node up the heap.
     *
     * @param hT The heap ArrayList.
     * @param i The index of the node to move up.
     */
    public void upheap(ArrayList<HeapNode> hT, int i) {
        while (i > 0 && hT.get(i).key > hT.get(parent(i)).key) {
            swap(hT, i, parent(i));
            i = parent(i);
        }
    }

    /**
     * Restores the heap property by moving a node down the heap.
     *
     * @param hT The heap ArrayList.
     * @param i The index of the node to move down.
     */
    public void downheap(ArrayList<HeapNode> hT, int i) {
        int size = hT.size();
        int largest = i;

        if (hasLeft(i) && hT.get(leftChild(i)).key > hT.get(largest).key) {
            largest = leftChild(i);
        }
        if (hasRight(i) && hT.get(rightChild(i)).key > hT.get(largest).key) {
            largest = rightChild(i);
        }
        if (largest != i) {
            swap(hT, i, largest);
            downheap(hT, largest);
        }
    }

    /**
     * Inserts a new node into the heap.
     *
     * @param hT The heap ArrayList.
     * @param newNum The key of the new node.
     * @param info The value associated with the new node.
     */
    public void insert(ArrayList<HeapNode> hT, int newNum, String info) {
        HeapNode newNode = new HeapNode(newNum);
        newNode.values[0] = info;
        hT.add(newNode);
        for (int i = hT.size() / 2 - 1; i >= 0; i--) {
            upheap(hT, i);
            downheap(hT, i);
        }
    }

    /**
     * Deletes a node with the specified key from the heap.
     *
     * @param hT The heap ArrayList.
     * @param num The key of the node to delete.
     */
    public void deleteNode(ArrayList<HeapNode> hT, int num) {
        int size = hT.size();
        int i;
        for (i = 0; i < size; i++) {
            if (num == hT.get(i).key)
                break;
        }

        HeapNode temp = hT.get(i);
        hT.set(i, hT.get(size - 1));
        hT.set(size - 1, temp);

        hT.remove(size - 1);
        for (int j = size / 2 - 1; j >= 0; j--) {
            upheap(hT, j);
            downheap(hT, j);
        }
    }

    /**
     * Displays the contents of the heap.
     *
     * @param array The heap ArrayList.
     */
    public void display(ArrayList<HeapNode> array) {
        System.out.println("~~~~~~~~~~~~~~~DISPLAY~~~~~~~~~~~~~");
        for (HeapNode node : array) {
            for (int i = 0; i < node.values.length; i++) {
                String info = node.values[i];
                if (info != null) {
                    System.out.println("Key: " + node.key + " | Values: " + info);
                }
            }
        }
    }

    /**
     * Reads data from a file and inserts it into the heap.
     *
     * @param filePath The path to the file.
     * @param arrList The list to store file data.
     * @param nodeList The heap ArrayList to store nodes.
     */
    public void readFile(String filePath, ArrayList<String> arrList, ArrayList<HeapNode> nodeList) {
        File f = new File(filePath);
        try {
            Scanner sc = new Scanner(f);
            sc.useDelimiter("\\n\\s*\\n");
            while (sc.hasNext()) {
                arrList.add(sc.next().trim());
            }
            arr = arrList.toArray(new String[1000]);
            int[] _arr1 = {7, 23, 37, 53, 67, 83, 95};
            for (int i = 0; i < _arr1.length; i++) {
                if (arr[i] != null) {
                    insert(nodeList, _arr1[i], arr[i]);
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs a modified A* search to find keys within ±5 of the target.
     *
     * @param heap The heap ArrayList.
     * @param target The target key.
     * @return A list of indices of nodes within the range.
     */
    public List<Integer> aStarSearchRange(ArrayList<HeapNode> heap, int target) {
        List<Integer> foundIndices = new ArrayList<>();
        if (heap.isEmpty()) return foundIndices;

        PriorityQueue<Entry> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.cost + n.heuristic));
        Set<Integer> visited = new HashSet<>();

        openSet.add(new Entry(0, 0, heuristic(heap.get(0).key, target)));

        while (!openSet.isEmpty()) {
            Entry current = openSet.poll();
            int index = current.index;
            int currentKey = heap.get(index).key;

            if (Math.abs(currentKey - target) <= 5) {
                foundIndices.add(index);
            }

            visited.add(index);

            int left = leftChild(index);
            int right = rightChild(index);

            if (hasLeft(index) && !visited.contains(left)) {
                openSet.add(new Entry(left, current.cost + 1, heuristic(heap.get(left).key, target)));
            }

            if (hasRight(index) && !visited.contains(right)) {
                openSet.add(new Entry(right, current.cost + 1, heuristic(heap.get(right).key, target)));
            }
        }
        return foundIndices;
    }

    /**
     * Calculates the heuristic value for the A* search.
     *
     * @param value The current node's key.
     * @param target The target key.
     * @return The heuristic value.
     */
    private int heuristic(int value, int target) {
        int distance = Math.abs(value - target);
        return distance <= 5 ? 0 : distance - 5;
    }
}
