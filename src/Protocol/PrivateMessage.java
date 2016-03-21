
package Protocol;

import Client.User;
import java.net.UnknownHostException;

/**
 * Object that represents a Message indicating to the server to forward it to a
 * specified recipient
 * @author Francisco Vilches | Saif Asad
 */
public class PrivateMessage extends Message {
    //Fields
    private final String BODY;
    private final String RECIPIENT;

    //--------------------------------------------------------------------------
    /**
     * Constructor
     * @param body a String representing the message itself
     * @param recipient a String representing the user that is to receive this message
     * @param user a String representing the user that's sending this message
     */
    public PrivateMessage(String body, String recipient, User user) {
        super(user);
        this.BODY = body;
        this.RECIPIENT = recipient;
    }

    //--------------------------------------------------------------------------
    public String getBODY() {
        return BODY;
    }

    //--------------------------------------------------------------------------
    public String getRECIPIENT() {
        return RECIPIENT;
    }
    
    //--------------------------------------------------------------------------
    @Override
    public String toString() {
        return super.toString() + BODY + "\n";
    }
    
    /**
     * For debugging purposes only
     * @param args
     * @throws UnknownHostException 
     */
    public static void main(String[] args) throws UnknownHostException {
        PrivateMessage pm = new PrivateMessage("hello, this is me", "Mark", new User("fvilches17"));
        System.out.println(pm);
    }
}