package Protocol;

import Client.User;
import java.net.UnknownHostException;

/**
 * Object that represents a Message indicating to the server that the user sending
 * it has requested to be logged out
 * @author Francisco Vilches | Saif Asad
 */
public class PublicMessage extends Message {
    //Fields
    private final String BODY;
    
    //--------------------------------------------------------------------------
    /**
     * Constructor
     * @param BODY a String representing the user that is to receive this message
     * @param user a String representing the user that's sending this message
     */
    public PublicMessage(String BODY, User user) {
        super(user);
        this.BODY = BODY;
    }

    //--------------------------------------------------------------------------
    public String getBODY() {
        return BODY;
    }

    //--------------------------------------------------------------------------
    @Override
    public String toString() {
        return super.toString() + BODY + "\n";
    }
    
    public static void main(String[] args) throws UnknownHostException {
        PublicMessage publicMessage = new PublicMessage("Hello this is a public message", new User("fvilches17"));
        System.out.println(publicMessage);
    }
}