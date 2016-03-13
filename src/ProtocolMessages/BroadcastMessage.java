package ProtocolMessages;
/**
 *
 * @author Saif Asad
 */
public class BroadcastMessage extends Message{
    private String textMessage;
    
    public BroadcastMessage(){
        super();
        setStatus(1);
    }

    /**
     * @return the textMessage
     */
    public String getTextMessage() {
        return textMessage;
    }

    /**
     * @param textMessage the textMessage to set
     */
    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }
}
