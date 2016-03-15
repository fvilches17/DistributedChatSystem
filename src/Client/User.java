

package Client;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class User implements Serializable {
    private final String ID;
    private final InetAddress IP_ADDRESS;
    
    public User(String ID) throws UnknownHostException {
        this.ID = ID;
        IP_ADDRESS = InetAddress.getLocalHost();
    }

    public String getID() {
        return ID;
    }

    public InetAddress getIP_ADDRESS() {
        return IP_ADDRESS;
    }
    
    /**
     * For debugging purposes only
     * @return User object info
     */
    @Override
    public String toString() {
        return "User{" + "ID=" + ID + ", IP_ADDRESS=" + IP_ADDRESS + '}';
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
