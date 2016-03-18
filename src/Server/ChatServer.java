
package Server;

import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 * This is the main server class, in charge of 2 main activities:
 * 1 - Creates and runs a thread (TableTransmitter) for broadcasting a table of
 *     clients that are connected to the server.
 * 2 - Listens for clients connection requests, starts a new ConnectionManager
 *     thread and passes the client socket. 
 * @author Francisco Vilches | Saif Asad
 */
public class ChatServer {
    //Fields.
    private static Hashtable<String, Socket> connectedClientsTable;
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
     * @param: none
     * @return: N/A
     */
    public ChatServer() {
        stopRequested = false;
        connectedClientsTable = new Hashtable<>();
        
        Thread tableTransmitterThread = new Thread(new TableTransmitter());
        tableTransmitterThread.start();
    }
    
    //--------------------------------------------------------------------------
    /**
     * Opens a new TCP socket to listen for clients connections.
     * Upon receiving a connection request, create a new thread and pass it a 
     * ConnectionManager instance and a socket to communicate with the client.
     * @param: none
     * @return: N/A
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
                Thread thread = new Thread(new ConnectionManager
                                     (this.getConnectedClientsTable(), socket));
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
     * Getter for connectedClientsTable
     * @param: none.
     * @return connectedClientsTable
     */
    public static Hashtable<String, Socket> getConnectedClientsTable() {
        return connectedClientsTable;
    }
    //--------------------------------------------------------------------------
    /**
     * stops server AFTER the next client connection has been made
     * or timeout is reached
     * @param: none.
     * @return N/A
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
     * @return N/A
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
        private final ByteArrayOutputStream baos;
        private final DataOutputStream dos;
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
            
            baos = new ByteArrayOutputStream();
            dos = new DataOutputStream(baos);
        }
        //----------------------------------------------------------------------
        @Override
        public void run() {
            //create a timer that will broadcast a table of all the clients
            //connected to the server every 150 ms
            Timer timer = new Timer(150, (ActionEvent e) -> {
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
            int destUDPPort = 8082;
       
            if (!connectedClientsTable.isEmpty()) {
                for (String client : getConnectedClientsTable().keySet()) {
                    try {
                        dos.writeBytes(client);
                        dos.flush();
                        byte[] data = baos.toByteArray();
                        baos.reset();
                        destAddress = getConnectedClientsTable().get(client).getInetAddress();
                        // send the byte array as a datagram
                        DatagramPacket sendDatagram = new DatagramPacket(data,
                                data.length, destAddress, destUDPPort);
                        tableTransmittingSocket.send(sendDatagram);
                    } catch (IOException ex) {
                        Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
