package Server;


import Protocol.LogInMessage;
import Protocol.LogOutMessage;
import Protocol.Message;
import Protocol.PrivateMessage;
import Protocol.PublicMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handles communication with a specific client First it takes
 * client's informations. Second it handles all the messages from the client.
 *
 * @author Francisco Vilches | Saif Asad
 */
public class ConnectionManager implements Runnable {

    private Message clientMessage;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    //--------------------------------------------------------------------------
    /**
     * Starts input and output streams and initializes a socket object
     * with the appropriate socket received from ChatServer.
     * @param connectedClientsTable
     * @param socket
     * @return N/A
     */
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
    /**
     * Send a message object received from a specific client to another client
     * obtained from getRecipient() method of the message object.
     * @param clientMessage
     * @throws IOException 
     * @retun N/A
     */
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
    /**
     * Broadcast a message object received from a specific client, to all clients
     * that are connected to the server except the send client.
     * @param clientMessage
     * @throws IOException 
     * @return N/A
     */
    public void broadCastMessage(Message clientMessage) throws IOException {
        System.out.println("Broadcasting public message");
        if (!ChatServer.getConnectedClientsTable().isEmpty()) {
            for(String client : ChatServer.getConnectedClientsTable().keySet()){
                if(!(client.equalsIgnoreCase(clientMessage.getUSER().getID()))){
                    Socket targetClientSocket = ChatServer.getConnectedClientsTable().get(client);
                    outputStream = new ObjectOutputStream(targetClientSocket.getOutputStream());
                    outputStream.writeObject(clientMessage);
                }
            }
        }
    }
    //--------------------------------------------------------------------------
    @Override
    public void run() {
        boolean clientDisconnected = false;
        try {
            try {
                do {
                    clientMessage = (Message) inputStream.readObject();
                    String client = clientMessage.getUSER().getID();
                    if (!client.isEmpty()) {
                        if (clientMessage instanceof LogInMessage) {
                            if (ChatServer.getConnectedClientsTable().containsKey(client)) {
                                System.out.println("User ID  " + clientMessage.getUSER().getID() + " already taken!");
                                Boolean boolObject = new Boolean(false);
                                outputStream.writeObject(new Boolean(false));
                            } else {
                                System.out.println("New client ( " + clientMessage.getUSER().getID()  + " ) added to the table");
                                ChatServer.getConnectedClientsTable().put(client, socket);                                      
                                Boolean boolObject = new Boolean(true);
                                outputStream.writeObject(boolObject);
                                
                            }
                        } else if (clientMessage instanceof PrivateMessage) {
                            System.out.println("Private Message received");
                            forwardMessage(clientMessage);
                        } else if (clientMessage instanceof PublicMessage) {
                            System.out.println("Public Message received");
                            broadCastMessage(clientMessage);
                        } else if (clientMessage instanceof LogOutMessage) {
                             System.out.println("Log out message received");
                            if (ChatServer.getConnectedClientsTable().contains(client)) {
                                Socket socket = ChatServer.getConnectedClientsTable().get(client);
                                System.out.println("socket for client " + clientMessage.getUSER().getID() + " was closed");
                                socket.close();
                                ChatServer.getConnectedClientsTable().remove(client);
                                System.out.println("client " + clientMessage.getUSER().getID() + " was removed from the table");
                                closeConnection();
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
    //--------------------------------------------------------------------------
    /**
     * When the client sends a LogOutMessage object, this method will be executed
     * to close the socket and the input/output streams.
     * @params: none.
     * @return: N/A.
     */
    public void closeConnection() {
        try {
            System.out.println("Closing streams");
            outputStream.close();
            inputStream.close();
        } catch (IOException ex) {
            System.err.println("Error closing connection ");
            System.err.println(ex);
        }
    }
}
