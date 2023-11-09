package inpeace.authenticationservice.dto;

public class CurrentUserResponse {
    private Long userID;
    private String emailAddress;

    public CurrentUserResponse(Long userID, String emailAddress) {
        this.userID = userID;
        this.emailAddress = emailAddress;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
