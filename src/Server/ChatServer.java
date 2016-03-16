
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
 *
 * @author Saif Asad
 */
public class ChatServer {
    private static Hashtable<ConnectedClient, Socket> connectedClientsTable;
    private boolean stopRequested;
    public static final String HOST_NAME = "localhost";
    public static final int TCP_PORT = 8888;
    public static final int UDP_PORT = 8889;
    DatagramSocket tableTransmittingSocket = null;
    ServerSocket serverSocket = null;
    InetAddress hostAddress = null; 
    
    //--------------------------------------------------------------------------
    //Constructor
    public ChatServer() {
        stopRequested = false;
        connectedClientsTable = new Hashtable<>();
        
        Thread tableTransmitterThread = new Thread();
        tableTransmitterThread.start();
    }
    
    //--------------------------------------------------------------------------
    public void startServer() {
        stopRequested = false;
        
        try {
            serverSocket = new ServerSocket(TCP_PORT);
            serverSocket.setSoTimeout(2000);
            System.out.println("Server started at " + InetAddress.getLocalHost()
                    + " on Port " + TCP_PORT);
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
    public static Hashtable<ConnectedClient, Socket> getConnectedClientsTable() {
        return connectedClientsTable;
    }
    //--------------------------------------------------------------------------
    // stops server AFTER the next client connection has been made
    // or timeout is reached
    public void requestStop() {
        stopRequested = true;
    }
    //--------------------------------------------------------------------------
    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.startServer();
    }
    //--------------------------------------------------------------------------
    /**
     * class: TableTransmitter
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
                broadcastClientsTable();
            });
            timer.start();
        }
        //----------------------------------------------------------------------
        private void broadcastClientsTable() {
            //check if there are any clients connected to the server
            if (!connectedClientsTable.isEmpty()) {
                getConnectedClientsTable().keySet().stream().forEach((client) -> {
                    try {
                        dos.writeBytes(client.getClientName());
                        dos.flush();
                        byte[] data = baos.toByteArray();
                        baos.reset();
                        // send the byte array as a datagram
                        DatagramPacket sendDatagram = new DatagramPacket(data,
                                data.length, hostAddress, UDP_PORT);
                        tableTransmittingSocket.send(sendDatagram);
                    } catch (IOException ex) {
                        Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
        }
    }
}
