
package Protocol;

import Client.User;
import java.net.UnknownHostException;


public class PrivateMessage extends Message {
    private final String BODY;
    private final String RECIPIENT;

    public PrivateMessage(String body, String recipient, User user) {
        super(user);
        this.BODY = body;
        this.RECIPIENT = recipient;
    }

    public String getBODY() {
        return BODY;
    }

    public String getRECIPIENT() {
        return RECIPIENT;
    }

    @Override
    public String toString() {
        return super.toString() + BODY + "\n\n";
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