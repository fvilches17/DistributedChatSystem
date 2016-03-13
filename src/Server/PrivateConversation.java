/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import ProtocolMessages.MessageTo;
import java.util.ArrayList;

/**
 *
 * @author Saif Asad
 */
public class PrivateConversation extends Conversation {
    private ConnectedClient client1;
    private ConnectedClient client2;
    private ArrayList<MessageTo> messages;
    
    public PrivateConversation(){
        client1 = new ConnectedClient();
        client2 = new ConnectedClient();
        messages = new ArrayList<>();
    }

    /**
     * @return the client1
     */
    public ConnectedClient getClient1() {
        return client1;
    }

    /**
     * @param client1 the client1 to set
     */
    public void setClient1(ConnectedClient client1) {
        this.client1 = client1;
    }

    /**
     * @return the client2
     */
    public ConnectedClient getClient2() {
        return client2;
    }

    /**
     * @param client2 the client2 to set
     */
    public void setClient2(ConnectedClient client2) {
        this.client2 = client2;
    }

    /**
     * @return the messages
     */
    public ArrayList<MessageTo> getMessages() {
        return messages;
    }

    /**
     * @param messages the messages to set
     */
    public void setMessages(ArrayList<MessageTo> messages) {
        this.messages = messages;
    }
    
    
    
}
