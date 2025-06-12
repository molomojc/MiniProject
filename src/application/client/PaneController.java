package application.client;

import javafx.scene.layout.Pane;

/**
 * Controls the main pane and manages switching between different views.
 * This class acts as a controller for the application's main user interface.
 */
public class PaneController {

    /** The main pane that holds the current view */
    private Pane mainPane;

    /** The MedClient view */
    private MedClient medClient;

    /** The Diagnose view */
    private Diagnose diagnose;

    /** The DatabaseHandler view */
    private DatabaseHandler databaseHandler;

    /**
     * Constructs a PaneController and initializes the views.
     * Sets the initial view to MedClient.
     */
    public PaneController() {
        mainPane = new Pane();
        medClient = new MedClient(this);
        diagnose = new Diagnose(this);

        // Set the initial pane to MedClient
        mainPane.getChildren().add(medClient.getPane());
    }

    /**
     * Switches the main pane to display the DatabaseHandler view.
     */
    public void switchToDatabase() {
        mainPane.getChildren().clear();
        mainPane.getChildren().add(databaseHandler.returnpane());
    }

    /**
     * Switches the main pane to display the Diagnose view.
     */
    public void switchToDiagnose() {
        mainPane.getChildren().clear();
        mainPane.getChildren().add(diagnose.getPane());
    }

    /**
     * Switches the main pane to display the MedClient view.
     */
    public void switchToMedClient() {
        mainPane.getChildren().clear();
        mainPane.getChildren().add(medClient.getPane());
    }

    /**
     * Returns the main pane.
     *
     * @return The main pane.
     */
    public Pane getPane() {
        return mainPane;
    }
}
