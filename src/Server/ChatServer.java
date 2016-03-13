/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.net.Socket;
import java.util.Hashtable;

/**
 *
 * @author Saif Asad
 */
public class ChatServer implements Runnable{
    //data strcuture for the messages
    //data structure for users and sockets
    private Hashtable<ConnectedClient, Socket> connectedClientsTable;
    
    public ChatServer(){
        
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
