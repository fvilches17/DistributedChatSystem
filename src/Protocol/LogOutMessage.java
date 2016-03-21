

package Protocol;

import Client.User;
import java.net.UnknownHostException;

/**
 * Object that represents a Message indicating to the server that a User has
 * logged out
 * @author Francisco Vilches | Saif Asad
 */
public class LogOutMessage extends Message {
    //--------------------------------------------------------------------------
    /**
     * Constructor
     * @param user a user object which represents the user sending this message 
     */
    public LogOutMessage(User user) {
        super(user);
    }
    
    //--------------------------------------------------------------------------
    @Override
    public String toString() {
        return super.toString() + "Logged in: false\n";
    }
    
    /**
     * For testing purposes only
     * @param args
     * @throws UnknownHostException 
     */
    public static void main(String[] args) throws UnknownHostException {
        LogOutMessage sm = new LogOutMessage(new User("fvilches17"));
        System.out.println(sm);
    }
}
