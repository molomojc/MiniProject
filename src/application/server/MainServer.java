package application.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Represents the main server that listens for client connections and handles them.
 * This server creates a new thread for each client using the `ClientHandler` class.
 * 
 * Responsibilities include:
 * - Listening for incoming client connections.
 * - Creating and starting a new thread for each connected client.
 * 
 * 
 */
public class MainServer {

    /** The port number on which the server listens for connections */
    private int Port = 1450;

    /** The server socket used to accept client connections */
    private ServerSocket sSocket;

    /**
     * Constructs a MainServer and starts listening for client connections.
     * Initializes the server socket and calls the `connectClient` method.
     */
    public MainServer() {
        try {
            sSocket = new ServerSocket(Port);
            connectClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Continuously listens for and accepts client connections.
     * For each connected client, a new thread is created and started using the `ClientHandler` class.
     */
    public void connectClient() {
        System.out.println("Starting...");
        while (true) {
            try {
                // Accept connection
                Socket cClient = sSocket.accept();

                // Create and start a new thread for the client
                ClientHandler cH = new ClientHandler(cClient);
                Thread SClient = new Thread(cH);
                SClient.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The main method to start the server.
     * Creates an instance of `MainServer` to begin listening for client connections.
     * 
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new MainServer();
    }
}
