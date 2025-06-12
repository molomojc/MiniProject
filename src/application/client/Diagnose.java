package application.client;

import java.io.*;
import java.net.Socket;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

/**
 * Represents the Diagnose Pane in the application.
 * This class is responsible for rendering the diagnostic interface and handling server communication.
 * It allows users to input patient details, upload X-ray images, and perform diagnoses.
 * 
 * @author JM Molomo
 * @author D MASINE
 * @author A NKANYANA
 * @author DN MBOYI
 */
public class Diagnose {

    /** The port number for server connection */
    private int Port = 1450;

    /** The host address for server connection */
    private String localHost = "localhost";

    /** The socket for network communication */
    private Socket ss;

    /** PrintWriter for sending text data to the server */
    private PrintWriter out;

    /** BufferedReader for receiving text data from the server */
    private BufferedReader in;

    /** DataOutputStream for sending binary data to the server */
    private DataOutputStream dous;

    /** DataInputStream for receiving binary data from the server */
    private DataInputStream dips;

    /** The main pane for UI components */
    Pane pane = new Pane();

    /** Controller for managing pane navigation */
    private PaneController paneController;

    /**
     * Constructs a Diagnose instance with a pane controller.
     * 
     * @param paneController The controller for managing pane navigation.
     */
    public Diagnose(PaneController paneController) {
        this.paneController = paneController;
        pane = new Pane();
        pane.setPrefSize(662, 422);
        RenderPane();
    }

    /**
     * Renders the main diagnostic interface with all UI components.
     * This method sets up the layout, buttons, and input fields for the pane.
     */
    /**
     * Renders the main diagnostic interface with all UI components.
     */
    void RenderPane() {
        connect();
        pane.getChildren().clear();
        
        // Header Section
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
        
        Button homeButton = createButton("Home",  500, 7, 57, 29);
        
      
        Button logoutButton = createButton("Log Out", 569, 6, 82, 29);
        logoutButton.setOnAction(e -> {
			stop();
			paneController.switchToMedClient();
		});
        homeButton.setOnAction(e -> {
        	paneController.switchToMedClient();
        });
        
        // Vertical separator
        int Xpoint = 442 * 40 / 100;
        Separator verticalSeparator = new Separator(Orientation.VERTICAL);
        verticalSeparator.setPrefSize(6, 330);
        verticalSeparator.setLayoutX(Xpoint);
        verticalSeparator.setLayoutY(43);
        
        // Patient Information Section
        Text PatientHeader = new Text(15, 60, "Patient Information");
        PatientHeader.setFill(Color.BLACK);
        PatientHeader.setFontSmoothingType(javafx.scene.text.FontSmoothingType.LCD);
        
        Separator horizontalSeparator = new Separator();
        horizontalSeparator.setPrefSize(Xpoint, 6);
        horizontalSeparator.setLayoutX(4);
        horizontalSeparator.setLayoutY(70);
        
        // Patient Fields
        Text patientName = new Text(4, 100, "Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter Patient name");
        int width = Xpoint * 60 / 100;
        nameField.setPrefSize(width, 25);
        nameField.setLayoutX(40);
        nameField.setLayoutY(89);
        
        Text patientAge = new Text(4, 140, "Age:");
        TextField ageField = new TextField();
        ageField.setPromptText("Enter Patient age");
        ageField.setPrefSize(width, 25);
        ageField.setLayoutX(40);
        ageField.setLayoutY(129);
        
        Text patientSymptoms = new Text(4, 180, "Symptoms:");
        TextField symptomsField = new TextField();
        symptomsField.setPromptText("Enter Patient symptoms");
        symptomsField.setPrefSize(width, 25);
        symptomsField.setLayoutX(70);
        symptomsField.setLayoutY(169);
        
        Text contact = new Text(4, 220, "Contact with infected person:");
        TextField contactField = new TextField();
        contactField.setPromptText("Enter YES/NO");
        contactField.setPrefSize(width, 25);
        contactField.setLayoutX(4);
        contactField.setLayoutY(230);
        
        // Image Section
        Text XrayUser = new Text(350, 60, "X-ray of Patient");
        ImageView banner = new ImageView();
        banner.setLayoutX(Xpoint + 70);
        banner.setLayoutY(100);
        banner.setFitWidth(200);
        banner.setFitHeight(200);
        banner.setPreserveRatio(true);
        
        // Upload Button
        Button uploadButton = createButton("Upload Image", 40, 340, 100, 25);
        uploadButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image");
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                Image image = new Image("file:" + file.getAbsolutePath());
                banner.setImage(image);
               
            }
        });
        
        // Diagnose Button - Updated with proper similarity display
        Button diagnoseButton2 = createButton("Diagnose", 410, 290, 100, 25);
        diagnoseButton2.setOnAction(event -> {
            Image image = banner.getImage();
            
           
            if (nameField.getText().isEmpty() || ageField.getText().isEmpty() || 
                symptomsField.getText().isEmpty() || contactField.getText().isEmpty()) {
                
                return;
            }
            new Thread(() -> {
                try {
                    String filePath = image.getUrl().replace("file:", "");
                    File file = new File(filePath);
                    
                    upload(nameField,ageField,symptomsField,contactField, file);
                    
                   
                    stop();
                    connect();
                } catch (Exception e) {
                   
                    e.printStackTrace();
                }
            }).start();
        });
        
        // Treatment Button
        Button treatmentButton = createButton("Treatment", 410, 325, 100, 25);
        treatmentButton.setOnAction(e -> {
//            paneController.switchToDatabase();
        	new DatabaseHandler(new PaneController());
        });
        
        // Footer
        Separator bottomSeparator = new Separator();
        bottomSeparator.setLayoutX(4);
        bottomSeparator.setLayoutY(375);
        bottomSeparator.setPrefSize(654, 6);
        
        Text footer = new Text(233, 403, "Developed by the @A-Team");
        footer.setFont(Font.font("Arial Black", 10));
        
        // Add all components to pane
        pane.getChildren().addAll(
            header, titleText, logo, homeButton, logoutButton,
            verticalSeparator, PatientHeader, horizontalSeparator, patientName, nameField,
            patientAge, ageField, patientSymptoms, symptomsField, contact, contactField,
            uploadButton, XrayUser, banner, diagnoseButton2, treatmentButton,
            bottomSeparator, footer
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
     * Uploads patient data and X-ray image to the server.
     * 
     * @param uploadDetail The patient name field.
     * @param age The patient age field.
     * @param symptomps The patient symptoms field.
     * @param infected The contact with infected person field.
     * @param file The X-ray image file to upload.
     * @throws IOException If there's an error during file upload.
     */
    private void upload(TextField uploadDetail, TextField age, TextField symptomps, TextField infected, File file) throws IOException {
        out.println("UP " + uploadDetail.getText() + " " + age.getText() + " " + symptomps.getText() + " " + infected.getText());
        out.flush();

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] byteArray = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(byteArray)) != -1) {
                dous.write(byteArray, 0, bytesRead);
            }
            dous.flush();
        }
    }

    /**
     * Establishes connection with the server.
     * Initializes all necessary input/output streams.
     */
    public void connect() {
        try {
            ss = new Socket(localHost, Port);
            out = new PrintWriter(ss.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(ss.getInputStream()));
            dous = new DataOutputStream(ss.getOutputStream());
            dips = new DataInputStream(ss.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes all network connections and streams.
     * Should be called when the application exits or when connections need to be reset.
     */
    public void stop() {
        try {
            if (ss != null) ss.close();
            if (out != null) out.close();
            if (in != null) in.close();
            if (dous != null) dous.close();
            if (dips != null) dips.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
