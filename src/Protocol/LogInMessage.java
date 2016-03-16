

package Protocol;

import Client.User;


public class LogInMessage extends Message {

    public LogInMessage(User user) {
        super(user);
    }

    @Override
    public String toString() {
        return super.toString() + "Logged in = true";
    }
    
    

}
