

package Client;

import java.io.Serializable;
import java.net.UnknownHostException;

/**
 * Represents a user logged in to the chat. This object is serializable
 * @author Francisco Vilches | Saif Asad
 */
public class User implements Serializable {
    private final String ID; //userID
    
    public User(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }
    
    /**
     * For debugging purposes only
     * @return User object info
     */
    @Override
    public String toString() {
        return "User{" + "ID=" + ID + '}';
    }
    
    /**
     * For testing purposes only
     * @param args
     * @throws UnknownHostException 
     */
    public static void main(String[] args) throws UnknownHostException {
        User user = new User("frad");
        System.out.println(user);   
    }
}
