package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;

public class ConnectionManager {

    private final String HOST_NAME = "localHost";
    private final int HOST_PORT = 8080;
    private boolean transmitionLive;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public ConnectionManager() {
        //Starting connection
        try {
            socket = new Socket(HOST_NAME, HOST_PORT);
            transmitionLive = true;
            Calendar calendar = Calendar.getInstance();
            Date timeStamp = calendar.getTime();
            System.out.println("Connection started at: " + timeStamp);
            System.out.println("Host: " + HOST_NAME);
            System.out.println("Port: " + HOST_PORT);
        } catch (IOException ex) {
            System.err.println("Unable to establish connection with " + HOST_NAME);
            System.err.println(ex);
            JOptionPane.showMessageDialog(null, "Unable to establish connection",
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        //Setting up streams
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.err.println("Unable to set output/input stream with socket ");
            System.err.println(ex);
        }
    }

    public boolean isTransmitionLive() {
        return transmitionLive;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }
    
    public boolean checkUserIDAvailability(IDCheckMessage message) {
        try {
            outputStream.writeObject(message);
        } catch (IOException ex) {
            System.err.println("Could not deliver message object to server");
            System.err.println(ex);
            return false;
        }
        while (true) {
            try {
                Boolean reply = (Boolean) inputStream.readObject();
                return reply;
            } catch (IOException ex) {
                System.err.println("Could not read message object from server");
                System.err.println(ex);
            } catch (ClassNotFoundException ex) {} //Ignore, check again
        }
    }

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

    public void sendMessage(Message message) throws IOException {
        outputStream.writeObject(message);
    }

    /**
     * For debugging purposes only
     *
     * @param args
     */
    public static void main(String[] args) {
        ConnectionManager cm = new ConnectionManager();
    }
}
