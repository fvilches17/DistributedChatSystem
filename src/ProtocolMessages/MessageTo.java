/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProtocolMessages;

/**
 *
 * @author Saif Asad
 */
public class MessageTo extends Message {
    private String testMessage;
    private String clientName;
    
    public MessageTo(){
        super();
        setStatus(0);
    }
    
    /**
     * @return the testMessage
     */
    public String getTestMessage() {
        return testMessage;
    }

    /**
     * @param testMessage the testMessage to set
     */
    public void setTestMessage(String testMessage) {
        this.testMessage = testMessage;
    }

    /**
     * @return the clientName
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * @param clientName the clientName to set
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    
}
