package cart.messages;

import java.io.Serializable;
import java.util.Date;

public class ExpirationInfo implements Serializable {

    private Long userID;
    private Date expirationTime;
    private ExpirationAction action;

    // Enum for predefined actions
    public enum ExpirationAction {
        REMOVE_ITEMS,
        NOTIFY_USER
        // ... other actions
    }

    // Constructors, getters, setters, etc.
    public ExpirationInfo() {
    }

    public ExpirationInfo(Long userID, Date expirationTime, ExpirationAction action) {
        this.userID = userID;
        this.expirationTime = expirationTime;
        this.action = action;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public ExpirationAction getAction() {
        return action;
    }

    public void setAction(ExpirationAction action) {
        this.action = action;
    }
}

