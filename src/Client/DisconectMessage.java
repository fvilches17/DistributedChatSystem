

package Client;

import java.net.UnknownHostException;

public class DisconectMessage extends Message {
    
    public DisconectMessage(User user, boolean loggedIn) {
        super(user);
    }

    @Override
    public String toString() {
        return super.toString() + "Logged in: false " + "\n";
    }
    
    public static void main(String[] args) throws UnknownHostException {
        DisconectMessage sm = new DisconectMessage(new User("fvilches17"), true);
        System.out.println(sm);
    }
}
