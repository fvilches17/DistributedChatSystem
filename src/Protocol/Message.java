package Protocol;

import Client.User;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author Francisco Vilches | Said Asad
 */
public abstract class Message implements Serializable {
    //Fields
    private final User USER; //Person who is sending. i.e Cient
    private Date timeStamp;
    
    //--------------------------------------------------------------------------
    /**
     * Constructor which takes in a User object. The user object represents the
     * user that is sending the message.
     * @param user 
     */
    public Message(User user) {
        this.USER = user;
        setTimeStamp();
    }

    //--------------------------------------------------------------------------
    public User getUSER() {
        return USER;
    }
    
    //--------------------------------------------------------------------------
    public Date getTimeStamp() {
        return timeStamp;
    }

    //--------------------------------------------------------------------------
    /**
     * At call time, gets the device's time, and saves a timestamp. The timestamp
     * is recorded to keep record of when this message was sent
     */
    private void setTimeStamp() {
        Calendar calendar = Calendar.getInstance();
        timeStamp = calendar.getTime();
    }
    
    //--------------------------------------------------------------------------
    @Override
    public String toString() {
        return "<<" + USER.getID() + ">>\n" + "(" + timeStamp + ")\n";
    }
}