package Client;

import Protocol.LogInMessage;
import Protocol.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 * Creating an object of this class establishes a connection with the chat server.
 * Therefore, this object contains an output stream and input stream to receive and
 * send messages from and to the server.
 * 
 * This object provides methods useful to communicate to the server via Message
 * objects.
 * @author Francisco Vilches | Said Asad
 */
public class ClientConnectionManager {
    //Fields
    private final String HOST_NAME = "localhost";
    private final int HOST_TCP_PORT = 8080; 
    private boolean transmitionLive;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    
    //--------------------------------------------------------------------------
    /**
     * Constructor
     * 
     * This default constructor establishes a connection with the chat server
     * and creates object and input streams which are kept as fields of the class
     */
    public ClientConnectionManager() {
        try {
            //Starting connection
            socket = new Socket(HOST_NAME, HOST_TCP_PORT);
            transmitionLive = true;
            //Creating a timestamp to indicate time connection was established
            Calendar calendar = Calendar.getInstance();
            Date timeStamp = calendar.getTime();
            System.out.println("Connection started at: " + timeStamp);
            System.out.println("Host: " + HOST_NAME);
            System.out.println("Port: " + HOST_TCP_PORT);
        } catch (IOException ex) {
            //Indicating to user via pop up message that connection wasn't established
            System.err.println("Unable to establish connection with " + HOST_NAME);
            System.err.println(ex);
            JOptionPane.showMessageDialog(null, "Unable to establish connection with host",
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            //Terminating program
            System.exit(-1);
        }

        //Setting up streams
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            //Notifying user via pop up message about the connection error
            System.err.println("Unable to set output/input stream with socket");
            System.err.println(ex);
            JOptionPane.showMessageDialog(null, "Unable to set output/input stream with socket",
                    "Connection Setup Error", JOptionPane.ERROR_MESSAGE);
            //Terminating progam
            System.exit(-1);
        }
    }

    //--------------------------------------------------------------------------
    public boolean isTransmitionLive() {
        return transmitionLive;
    }
    
    //--------------------------------------------------------------------------
    public ObjectInputStream getInputStream() {
        return inputStream;
    }
    
    //--------------------------------------------------------------------------
    /**
     * This method sends a LogInMessage to the chat server and awaits for a Boolean 
     * response from the server to indicate whether the userID (saved in the message)
     * is available or already taken.
     * 
     * @param message a message object of type LogInMessage.
     * @return True if userID available, false otherwise.
     */
    public boolean checkUserIDAvailability(LogInMessage message) {
        try {
            //Passing message to the chat server. Message includes userID to check for
            outputStream.writeObject(message);
            outputStream.flush();
        } catch (IOException ex) {
            System.err.println("Could not deliver message object to server");
            System.err.println(ex);
            return false;
        }
        while (true) {
            try {
                //Receiving server response. Server should return Boolean object
                Boolean reply = (Boolean) inputStream.readObject(); 
                return reply;//True if userID available, false otherwise
            } catch (IOException ex) {
                System.err.println("Could not read message object from server");
                System.err.println(ex);
                return false;
            } catch (ClassNotFoundException ex) {
                System.err.println("Wrong object received");
                System.err.println(ex);
                return false;
            } 
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Closes the client's connection to the chat server
     */
    public void closeConnection() {
        try {
            System.out.println("Closing writer and socket");
            transmitionLive = false;
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException ex) {
            System.err.println("Error closing connection ");
            System.err.println(ex);
        }
    }
    
    /**
     * Method used to send any Message object to the chat server. The server
     * will determine the appropriate action based on the type of message sent.
     * E.g. if a PrivateMessage is sent, then the chat server will forward that
     * private message to the intended recipient.
     * 
     * @param message message object of types: LogIn/LogOut/Private/Public
     * @throws IOException if the output stream fails fails
     */
    public void sendMessage(Message message) throws IOException {
        outputStream.writeObject(message);
        outputStream.flush();
    }

    /**
     * For debugging purposes only
     *
     * @param args
     */
    public static void main(String[] args) {
        ClientConnectionManager cm = new ClientConnectionManager();
    }
}
