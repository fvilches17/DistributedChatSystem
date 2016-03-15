

package Client;

import java.net.UnknownHostException;


public class PublicMessage extends Message {
    
    private final String BODY;

    public PublicMessage(String BODY, User user) {
        super(user);
        this.BODY = BODY;
    }

    public String getBODY() {
        return BODY;
    }

    @Override
    public String toString() {
        return super.toString() + BODY + "\n\n";
    }
    
    public static void main(String[] args) throws UnknownHostException {
        PublicMessage publicMessage = new PublicMessage("Hello this is a public message", new User("fvilches17"));
        System.out.println(publicMessage);
    }
}
