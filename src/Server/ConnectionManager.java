
package Server;

import ProtocolMessages.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Hashtable;

/**
 * This class handles communication with a specific client
 * First it takes client's informations.
 * Second it handles all the messages from the client.
 * @author Saif Asad
 */
public class ConnectionManager implements Runnable {

    private Socket socket; // socket for client/server communication
    private ConnectedClient connectedClient;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    
    //--------------------------------------------------------------------------
    //Constructor
    ConnectionManager(Hashtable<ConnectedClient, Socket> connectedClientsTable, Socket socket) {
        connectedClient = new ConnectedClient();
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
    public void forwardMessage(Message clientMessage){
        //clientMessage.getUSER();
        clientMessage.getRecipient();
        //get the recipient
        Socket recipientSocket = connectedClientsTable.get();
        outputStream = new ObjectOutputStream(recipientSocket.getOutputStream());
        //send the message to the recipient
        outputStream.writeObject(clientMessage);
        
    }
    //--------------------------------------------------------------------------
    public void broadCastMessage(Message clientMessage){
        //get a list of client
        //get the socket from the table
        Socket targetClientSocket = ;
        outputStream = new ObjectOutputStream(targetClientSocket.getOutputStream());
        chatServer.getListOfConnectedClients();
        outputStream.writeObject(clientMessage);
    }
    //--------------------------------------------------------------------------
    @Override
    public void run() {
        try {
            String clientRequest;
            do {  
                clientRequest = br.readLine(); // blocking
                //System.out.println("Received line: " + clientRequest);   
                
                if(clientMessage instanceOf(IDCheckMessage)){
                    if(){
                        
                    } else {
                        
                    }
                } else if(clientMessage instanceOf(PrivateMessage)){
                    forwardMessage(clientMessage);
                } else if(clientMessage instanceOf(PublicMessage)){
                
                } else if(clientMessage instanceOf(DisconnectMessage)){
                
                }
            } while();   
        } catch (IOException e) {
            System.err.println("Server error: " + e);
        }
    }
}
