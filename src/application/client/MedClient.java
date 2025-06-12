package application.client;

import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Represents the Medical Client GUI for the application.
 * This class is responsible for rendering the main interface and its components.
 */
public class MedClient {

    private Pane pane = new Pane();
    private PaneController paneController;
   

    /**
     * Constructor to create the MedClient GUI.
     * This constructor initializes the GUI components and sets up the layout.
     *
     * @param paneController The controller used to manage pane switching.
     */
    public MedClient(PaneController paneController) {
        this.paneController = paneController;
        pane.setPrefSize(662, 422);
        RenderPane();
    }

    /**
     * Renders the main pane with all its components.
     * This method sets up the layout, buttons, and other UI elements.
     */
    public void RenderPane() {
        Separator topSeparator = new Separator();
        topSeparator.setLayoutY(44);
        topSeparator.setPrefSize(662, 6);

        Rectangle header = new Rectangle(662, 43);
        header.setArcWidth(5);
        header.setArcHeight(5);
        header.setFill(Color.web("#ff1f43"));
        header.setStroke(Color.BLACK);

        Text titleText = new Text(46, 26, "Medical App");
        titleText.setFill(Color.web("#e1d8d8"));
        titleText.setFontSmoothingType(javafx.scene.text.FontSmoothingType.LCD);
        titleText.setFont(Font.font("Arial Bold Italic", 16));

        Image logoImage = new Image(getClass().getResource("/application/Logo.png").toExternalForm());
        ImageView logo = new ImageView(logoImage);
        logo.setLayoutX(14);
        logo.setLayoutY(7);
        logo.setFitHeight(33);
        logo.setFitWidth(32);
        logo.setPreserveRatio(true);

        Button homeButton = createButton("Home", 500 , 7, 57, 29);
        Button logoutButton = createButton("Log Out", 569, 6, 82, 29);

        Image logoImage2 = new Image(getClass().getResource("/application/Screenshot 2025-04-14 230131.png").toExternalForm());
        ImageView banner = new ImageView(logoImage2);
        banner.setLayoutY(43);
        banner.setFitHeight(330);
        banner.setFitWidth(662);
        banner.setPreserveRatio(true);

        Text welcomeText = new Text(46, 92, "Welcome to,");
        welcomeText.setFill(Color.web("#cc1313"));
        welcomeText.setFontSmoothingType(javafx.scene.text.FontSmoothingType.LCD);
        welcomeText.setFont(Font.font("Arial Black", 19));

        Text appName = new Text(48, 119, " Medical App");
        appName.setFill(Color.web("#5b3232"));
        appName.setFont(Font.font("System Bold Italic", 18));

        Text description = new Text(47, 164, "Our innovative medical app leverages advanced machine learning algorithms, including K-Nearest Neighbors (K-NN), to assist in the early detection of COVID-19 from chest X-ray scans. Designed with accuracy and efficiency in mind, the app analyzes radiographic images using a comprehensive diagnostic model trained on a diverse dataset.");
        description.setFill(Color.web("#793232"));
        description.setFont(Font.font("Consolas Bold Italic", 13));
        description.setWrappingWidth(454);

        Button startNowButton = createButton("Start Now", 48, 260, 113, 25);
        startNowButton.setOnAction(e -> {
            paneController.switchToDiagnose();
        });

        Separator bottomSeparator = new Separator();
        bottomSeparator.setLayoutX(4);
        bottomSeparator.setLayoutY(375);
        bottomSeparator.setPrefSize(654, 6);

        Text footer = new Text(233, 403, "Developed by the @A-Team");
        footer.setFont(Font.font("Arial Black", 10));

        // Add all elements to pane
        pane.getChildren().addAll(
                topSeparator, header, titleText, logo,
                homeButton, logoutButton,
                banner, welcomeText, appName, description,
                startNowButton, bottomSeparator, footer
        );
    }

    /**
     * Creates a styled button with consistent appearance.
     *
     * @param text The button text.
     * @param x The x-coordinate position.
     * @param y The y-coordinate position.
     * @param width The button width.
     * @param height The button height.
     * @return The configured Button instance.
     */
    private Button createButton(String text, double x, double y, double width, double height) {
        Button button = new Button(text);
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setPrefSize(width, height);
        button.setStyle("-fx-background-color: #ff1f43;");
        button.setTextFill(Color.web("#f7f4f4"));
        button.setFont(Font.font("Arial Italic", 13));
        return button;
    }

    /**
     * Returns the main pane containing the diagnostic interface.
     *
     * @return The root Pane of the diagnostic interface.
     */
    public Pane getPane() {
        return pane;
    }
}
