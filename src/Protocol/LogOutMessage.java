

package Protocol;

import Client.User;
import java.net.UnknownHostException;

public class LogOutMessage extends Message {
    
    public LogOutMessage(User user) {
        super(user);
    }

    @Override
    public String toString() {
        return super.toString() + "Logged in: false " + "\n";
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
