package application.client;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Handles the database operations and determines the recommended treatments.
 * This class uses a heap data structure to manage and display treatment data.
 * It also provides a graphical representation of the heap.
 * 
 * @author JM Molomo
 * @author D MASINE
 * @author A NKANYANA
 * @author DN MBOYI
 */
public class DatabaseHandler {

    private static final int NODE_RADIUS = 20;
    private static final int LEVEL_SPACING = 80;
    private Heap heap;
    private PaneController paneController;
    private Pane panee;

    // Variables used in the readText method
    private String name, age, symptom, contact, result;

    /**
     * Constructs a DatabaseHandler instance and initializes the GUI components.
     * 
     * @param paneControl The controller used to manage pane switching.
     */
    public DatabaseHandler(PaneController paneControl) {
        TextArea consoleOutput = new TextArea();
        consoleOutput.setEditable(false);
        consoleOutput.setWrapText(true);
        consoleOutput.setPrefSize(800, 300);

        // Redirect System.out to the TextArea
        PrintStream consoleStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                Platform.runLater(() -> consoleOutput.appendText(String.valueOf((char) b)));
            }
        });
        System.setOut(consoleStream);
        
        //Laying out the pane to display the HEAP
        this.paneController = paneControl;
        panee = new Pane();
        panee.setPrefSize(662, 422);
        heap = new Heap();
        heap.readFile("data/Treatments.txt", heap.details, heap.heap); // Loading the heap data
        //Using canvas to visualize the heap
        Canvas canvas = new Canvas(800, 422);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        if (!heap.isEmpty()) {
            drawHeap(gc, 0, canvas.getWidth() / 2, NODE_RADIUS * 2, canvas.getWidth() / 4);
        }

        Stage heapStage = new Stage();
        Pane cPane = new Pane();
        Pane tPane = new Pane();
        tPane.setLayoutY(300);
        tPane.setPrefSize(800, 422);
        tPane.getChildren().addAll(consoleOutput);
        cPane.getChildren().addAll(canvas);
        panee.getChildren().addAll(cPane, tPane);

        Scene scene = new Scene(panee, 800, 600);
        heapStage.setTitle("Recommended Treatments");
        heapStage.setScene(scene);
        heapStage.show();

        System.out.println("*********************************************");
        System.out.println("     Heap implementation with arrayList:     ");
        System.out.println("*********************************************\n");
        heap.display(heap.heap);

        // Reading the user data from a text file
        int targetKey = readText("data/server/Info.txt");
        //Using the A* algorithm to search for a key(calculated percentage similarity) in the HEAP
        List<Integer> foundIndices = heap.aStarSearchRange(heap.heap, targetKey);
        if (!foundIndices.isEmpty()) {
            if (contact.toUpperCase().equals("YES")) {
                System.out.println("\nRecommended treatment for age " + age + " with '" + symptom + "' symptoms, "
                        + "had contact with infected person, and " + targetKey + "% similarity is:");
            } else if (contact.toUpperCase().equals("NO")) {
                System.out.println("\nRecommended treatment for age " + age + " with '" + symptom + "' symptoms, "
                        + "had no contact with infected person, and " + targetKey + "% similarity is:");
            }
            for (int index : foundIndices) {
                HeapNode node = heap.heap.get(index);
                System.out.println(node.values[0]);
            }
        } else {
            System.out.println("\nNo keys found within ±5 of " + targetKey);
        }

        //Demonstrating how the delete method of the HEAP works
        heap.deleteNode(heap.heap, 67);
        System.out.println("\n*********************************************");
        System.out.println("         After deleting a key element        ");
        System.out.println("*********************************************\n");
        heap.display(heap.heap);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.setOut(System.out);
        }));
    }

    /**
     * Draws the heap graphically on the canvas.
     * 
     * @param gc      The GraphicsContext used for drawing.
     * @param index   The current index in the heap.
     * @param x       The x-coordinate of the node.
     * @param y       The y-coordinate of the node.
     * @param xOffset The horizontal spacing between nodes.
     */
    private void drawHeap(GraphicsContext gc, int index, double x, double y, double xOffset) {
        if (index >= heap.size())
            return;

        HeapNode node = heap.heap.get(index);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        //Drawing white-filled circle with black border
        gc.setFill(Color.WHITE);
        gc.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        gc.strokeOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

        //Writing the node key
        gc.setFill(Color.BLACK);
        gc.strokeText(String.valueOf(node.getKey()), x - 5, y + 5);

        int leftIndex = heap.leftChild(index);
        int rightIndex = heap.rightChild(index);

        //Drawing the left child of a node
        if (heap.hasLeft(index)) {
            double leftX = x - xOffset;
            double leftY = y + LEVEL_SPACING;

            double angle = Math.atan2(leftY - y, leftX - x);
            double startX = x + NODE_RADIUS * Math.cos(angle);
            double startY = y + NODE_RADIUS * Math.sin(angle);
            double endX = leftX - NODE_RADIUS * Math.cos(angle);
            double endY = leftY - NODE_RADIUS * Math.sin(angle);

            gc.strokeLine(startX, startY, endX, endY);
            drawHeap(gc, leftIndex, leftX, leftY, xOffset / 2);
        }
        //Drawing the right child of a node
        if (heap.hasRight(index)) {
            double rightX = x + xOffset;
            double rightY = y + LEVEL_SPACING;

            double angle = Math.atan2(rightY - y, rightX - x);
            double startX = x + NODE_RADIUS * Math.cos(angle);
            double startY = y + NODE_RADIUS * Math.sin(angle);
            double endX = rightX - NODE_RADIUS * Math.cos(angle);
            double endY = rightY - NODE_RADIUS * Math.sin(angle);

            gc.strokeLine(startX, startY, endX, endY);
            drawHeap(gc, rightIndex, rightX, rightY, xOffset / 2);
        }
    }

    /**
     * Reads patient data from a text file and extracts the similarity percentage.
     * 
     * @param path The path to the text file.
     * @return The similarity percentage extracted from the file.
     */
    public int readText(String path) {
        int percent = 0;

        File f = new File(path);
        try {
            Scanner sc = new Scanner(f);
            while (sc.hasNext()) {
                String line = sc.nextLine();
                StringTokenizer tokenizer = new StringTokenizer(line, " ");
                name = tokenizer.nextToken();
                age = tokenizer.nextToken();
                symptom = tokenizer.nextToken();
                contact = tokenizer.nextToken();

                result = tokenizer.nextToken();
                percent = (int) Double.parseDouble(result);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return percent;
    }

    /**
     * Returns the main pane containing the database handler interface.
     * 
     * @return The root Pane of the database handler interface.
     */
    public Pane returnpane() {
        return panee;
    }
}
