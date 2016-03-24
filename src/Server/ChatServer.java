
package Server;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 * This is the main server class, in charge of 2 main activities:
 * 1 - Creates and runs a thread (TableTransmitter) for broadcasting a table of
 *     clients that are connected to the server.
 * 2 - Listens for clients connection requests, starts a new ServerConnectionManager
     thread and passes the client socket. 
 * @author Francisco Vilches | Saif Asad
 */
public class ChatServer {
    //Fields.
    public static HashMap<String, ObjectOutputStream> connectedClientStreams;
    public static HashMap<String, Socket> connectedClientsSockets;
    private boolean stopRequested;
    public static final String HOST_NAME = "localhost";
    public static final int TCP_PORT = 8080;
    public static final int UDP_PORT = 8082;
    DatagramSocket tableTransmittingSocket = null;
    ServerSocket serverSocket = null;
    InetAddress hostAddress = null; 
    
    //--------------------------------------------------------------------------
    /**
     * Constructor
     * starts a new thread and passes it a TableTransmitter object.
     */
    public ChatServer() {
        stopRequested = false;
        connectedClientStreams = new HashMap<>();
        connectedClientsSockets = new HashMap<>();
        
        Thread tableTransmitterThread = new Thread(new TableTransmitter());
        tableTransmitterThread.start();
    }
    
    //--------------------------------------------------------------------------
    /**
     * Opens a new TCP socket to listen for clients connections.
     * Upon receiving a connection request, create a new thread and pass it a 
     * ServerConnectionManager instance and a socket to communicate with the client.
     */
    public void startServer() {
        stopRequested = false;
        
        try {
            serverSocket = new ServerSocket(TCP_PORT);
            serverSocket.setSoTimeout(2000);
            System.out.println("Server started at " + InetAddress.getLocalHost());
            System.out.println("Host: " + HOST_NAME);
            System.out.println("TCP Port: " + TCP_PORT);
        } catch (IOException ex) {
            System.err.println("Server can't listen on port : " + ex);
            System.exit(-1);
        }
        
        // block until the next client requests a connection
        while (!stopRequested) {  
            try {
                Socket socket = serverSocket.accept(); //blocking
                System.out.println("Connection made with " + socket.getInetAddress());
                Thread thread = new Thread(new ServerConnectionManager(socket));
                thread.start();
            } catch (SocketTimeoutException e) {  // ignore and try again
            } catch (IOException e) {
                System.err.println("Can't accept client connection: " + e);
                stopRequested = true;
            }
        }
        try {
            serverSocket.close();
        } catch (IOException e) {  // ignore
        }
        System.out.println("Server finishing");
    }
    //--------------------------------------------------------------------------
    /**
     * stops server AFTER the next client connection has been made
     * or timeout is reached
     */
    public void requestStop() {
        stopRequested = true;
        System.out.println("closing tableTransmittingSocket");
        tableTransmittingSocket.close();
    }
    //--------------------------------------------------------------------------
    /**
     * Main starting point for the server.
     * @param args
     */
    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.startServer();
    }
    //--------------------------------------------------------------------------
    /**
     * Inner class running on a separate thread than the server.
     * Responsible for broadcasting an array of clients that are currently
     * connected to the server, to each client.
     */
    private class TableTransmitter implements Runnable{
        public TableTransmitter() {
            try {
                tableTransmittingSocket = new DatagramSocket(UDP_PORT);
                hostAddress = InetAddress.getByName(HOST_NAME);
                System.out.println("UDP port opened: " + UDP_PORT);
            } catch (SocketException e) {
                System.err.println("Unable to create UDP socket: " + e);
                stopRequested = true;
            } catch (UnknownHostException e) {
                System.err.println("Unknown host: " + e);
                stopRequested = true;
            }
        }
        //----------------------------------------------------------------------
        @Override
        public void run() {
            //create a timer that will broadcast a table of all the clients
            //connected to the server every 150 ms
            Timer timer = new Timer(1000, (ActionEvent e) -> {
                //System.out.println("Broadcasting table of clients");
                broadcastClientsTable();
            });
            timer.start();
        }
        //----------------------------------------------------------------------
        /**
         * Broadcasts a list of currently connected clients.
         * @param: none.
         * @return N/A
         */
        private void broadcastClientsTable() {
            //check if there are any clients connected to the server
            InetAddress destAddress = null;
            if (!connectedClientStreams.isEmpty()) {
                for (String loggedInClient : connectedClientStreams.keySet()) {
                    for (String recipient : connectedClientStreams.keySet()) {
                        if (!(loggedInClient.equals(recipient))) {
                            try {
                                destAddress = connectedClientsSockets.get(recipient).getInetAddress();
                                byte[] outgoingData = loggedInClient.getBytes();
                                DatagramPacket outPacket = new DatagramPacket(outgoingData, 
                                        outgoingData.length, destAddress, UDP_PORT);
                                tableTransmittingSocket.send(outPacket);
                            } catch (IOException ex) {
                                Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
        }
    }
}
