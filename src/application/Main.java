package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import application.client.*;

/**
 * Entry point for the JavaFX Application.
 * This class initializes and starts the application.
 * 
 * @author JM Molomo
 * @author D MASINE
 * @author A NKANYANA
 * @author DN MBOYI
 */
public class Main extends Application {

    /**
     * Starts the JavaFX application.
     * This method is called after the application is launched.
     * 
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize the root pane controller
            PaneController root = new PaneController();
            
            // Create a scene with the specified dimensions
            Scene scene = new Scene(root.getPane(), 662, 422);
            
            // Set the scene to the primary stage and display it
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            // Print stack trace in case of an exception
            e.printStackTrace();
        }
    }

    /**
     * The main method serves as the entry point for the application.
     * 
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
