

package Protocol;

import Client.User;

/**
 * Object that represents a Message indicating to the server that a User has
 * logged in
 * @author Francisco Vilches | Saif Asad
 */
public class LogInMessage extends Message {
    //--------------------------------------------------------------------------
    /**
     * Constructor
     * @param user a user object which represents the user sending this message 
     */
    public LogInMessage(User user) {
        super(user);
    }
    
    //--------------------------------------------------------------------------
    @Override
    public String toString() {
        return super.toString() + "[User succesfully logged in!]\n";
    }
}
