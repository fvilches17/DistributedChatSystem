package Client;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public abstract class Message implements Serializable {
    
    private final User USER;
    private Date timeStamp;

    public Message(User user) {
        this.USER = user;
        setTimeStamp();
    }

    public User getUSER() {
        return USER;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    private void setTimeStamp() {
        Calendar calendar = Calendar.getInstance();
        timeStamp = calendar.getTime();
    }
    
    @Override
    public String toString() {
        return USER.getID() + "\n" + "Transmition: " + timeStamp + "\n";
    }
}
