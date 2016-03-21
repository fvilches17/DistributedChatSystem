package Server;

import Client.User;
import Protocol.LogInMessage;
import Protocol.LogOutMessage;
import Protocol.Message;
import Protocol.PrivateMessage;
import Protocol.PublicMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handles communication with a specific client First it takes
 * client's informations. Second it handles all the messages from the client.
 *
 * @author Francisco Vilches | Saif Asad
 */
public class ServerConnectionManager implements Runnable {
    //Fields
    private Message clientMessage;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String thisClient;

    //--------------------------------------------------------------------------

    /**
     * Starts input and output streams and initializes a socket object with the
     * appropriate socket received from ChatServer.
     *
     * @param socket
     */
    public ServerConnectionManager(Socket socket) {
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
     *
     * @param clientMessage
     * @throws IOException
     * @retun N/A
     */
    public void forwardMessage(Message clientMessage) throws IOException {
        //get the recipient's socket
        PrivateMessage message = (PrivateMessage) clientMessage;
        String recipient = message.getRECIPIENT().trim();
        //send the message to the target recipient
        ObjectOutputStream oos = ChatServer.connectedClientStreams.get(recipient);
        oos.writeObject(message);
        oos.flush();
        //outputStream.writeObject(clientMessage);
        //outputStream.flush();
    }

    //--------------------------------------------------------------------------
    /**
     * Broadcast a message object received from a specific client, to all
     * clients that are connected to the server except the send client.
     *
     * @param clientMessage
     * @throws IOException
     */
    public void broadCastMessage(Message clientMessage) throws IOException {
        System.out.println("Broadcasting public message");
        if (!ChatServer.connectedClientStreams.isEmpty()) {
            for (String client : ChatServer.connectedClientStreams.keySet()) {
                if (!(client.equalsIgnoreCase(clientMessage.getUSER().getID()))) {
                    ObjectOutputStream oos = ChatServer.connectedClientStreams.get(client);
                    oos.writeObject(clientMessage);
                    oos.flush();
                    System.out.println("Sending public message to client: " + client);
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
                    clientMessage = (Message) inputStream.readObject(); //blocking
                    System.out.println(clientMessage.getClass().getTypeName() + "message recieved");
                    String client = clientMessage.getUSER().getID();
                    if (!client.isEmpty()) {
                        if (clientMessage instanceof LogInMessage) {
                            Boolean isUserIDAvailable = !ChatServer.connectedClientStreams.containsKey(client);
                            if (!isUserIDAvailable) {
                                System.out.println("User ID  " + clientMessage.getUSER().getID() + " already taken!");
                            } else { //Log in ok!
                                System.out.println("New client ( " + clientMessage.getUSER().getID() + " ) added to the table");
                                ChatServer.connectedClientStreams.put(client, outputStream);
                                ChatServer.connectedClientsSockets.put(client, socket);
                                thisClient = client;
                            }
                            outputStream.writeObject(isUserIDAvailable);
                            outputStream.flush();
                        } else if (clientMessage instanceof PrivateMessage) {
                            System.out.println("Private Message received");
                            forwardMessage(clientMessage);
                        } else if (clientMessage instanceof PublicMessage) {
                            System.out.println("Public Message received");
                            broadCastMessage(clientMessage);
                        } else if (clientMessage instanceof LogOutMessage) {
                            System.out.println("Log out message received");
                            if (ChatServer.connectedClientStreams.containsKey(client)) {
                                broadCastMessage(clientMessage);
                                System.out.println("socket for client " + clientMessage.getUSER().getID() + " was closed");
                                socket.close();
                                ChatServer.connectedClientStreams.remove(client);
                                ChatServer.connectedClientsSockets.remove(client);
                                System.out.println("client " + clientMessage.getUSER().getID() + " was removed from the table");
                                closeConnection();
                            }
                            clientDisconnected = true;
                        }
                    }
                } while (!clientDisconnected);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServerConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e);
            System.err.println("Logging out " + thisClient);
            try {
                if (thisClient != null) {
                    broadCastMessage(new LogOutMessage(new User(thisClient)));
                    System.out.println("socket for client " + this + " was closed");
                    socket.close();
                    ChatServer.connectedClientStreams.remove(thisClient);
                    ChatServer.connectedClientsSockets.remove(thisClient);
                    System.out.println("client " + thisClient + " was removed from the table");
                }
                closeConnection();
            } catch (IOException ex) {

            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * When the client sends a LogOutMessage object, this method will be
     * executed to close the socket and the input/output streams.
     *
     * @params: none.
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
