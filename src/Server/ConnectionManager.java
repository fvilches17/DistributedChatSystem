package Server;

import Protocol.DisconnectMessage;
import Protocol.LogInMessage;
import Protocol.Message;
import Protocol.PrivateMessage;
import Protocol.PublicMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handles communication with a specific client First it takes
 * client's informations. Second it handles all the messages from the client.
 *
 * @author Saif Asad
 */
public class ConnectionManager implements Runnable {

    private Message clientMessage;
    private Socket socket; // socket for client/server communication
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    //--------------------------------------------------------------------------
    //Constructor
    ConnectionManager(Hashtable<String, Socket> connectedClientsTable, Socket socket) {
        this.socket = socket;
        //Setting up streams
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.err.println("Unable to set output/input stream with socket ");
            System.err.println(ex);
        }
    }
    //--------------------------------------------------------------------------
    public void forwardMessage(Message clientMessage) throws IOException {
        //get the recipient's socket
        PrivateMessage message = (PrivateMessage)clientMessage;
        String recipient = message.getRECIPIENT();
        Socket targetClientSocket = ChatServer.getConnectedClientsTable().get(recipient);
        outputStream = new ObjectOutputStream(targetClientSocket.getOutputStream());
        //send the message to the target recipient
        outputStream.writeObject(clientMessage);
    }

    //--------------------------------------------------------------------------
    public void broadCastMessage(Message clientMessage) throws IOException {
        if (!ChatServer.getConnectedClientsTable().isEmpty()) {
            for(String client : ChatServer.getConnectedClientsTable().keySet()){
                Socket targetClientSocket = ChatServer.getConnectedClientsTable().get(client);
                outputStream = new ObjectOutputStream(targetClientSocket.getOutputStream());
                outputStream.writeObject(clientMessage);
            }
        }
    }
    //--------------------------------------------------------------------------
    @Override
    public void run() {
        boolean clientDisconnected = false;
        try {
            try {
                clientMessage = (Message) inputStream.readObject();
                do {
                    String client = clientMessage.getUSER().getID();
                    if (!client.isEmpty()) {
                        if (clientMessage instanceof LogInMessage) {
                            if (ChatServer.getConnectedClientsTable().containsKey(client)) {
                                outputStream.writeObject(new Boolean(false));
                            } else {
                                ChatServer.getConnectedClientsTable().put(client, socket);
                                outputStream.writeObject(new Boolean(true));
                            }
                        } else if (clientMessage instanceof PrivateMessage) {
                            forwardMessage(clientMessage);
                        } else if (clientMessage instanceof PublicMessage) {
                            broadCastMessage(clientMessage);
                        } else if (clientMessage instanceof DisconnectMessage) {
                            if (ChatServer.getConnectedClientsTable().contains(client)) {
                                ChatServer.getConnectedClientsTable().remove(client);
                            }
                            clientDisconnected = true;
                        }
                    }
                } while (!clientDisconnected);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e);
        }
    }
}
