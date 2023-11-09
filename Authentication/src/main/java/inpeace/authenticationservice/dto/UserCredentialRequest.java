package inpeace.authenticationservice.dto;

import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;

@Component
public class UserCredentialRequest implements Serializable {

    //  serialVersionUID is a special static field in a Serializable class
    //  It is used to ensure that during deserialization, the same class is loaded.
    @Serial
    private static final long serialVersionUID = -8445943548965154778L;

    private String emailAddress;
    private String password;

    public String getEmailAddress(){
        return emailAddress;
    }

    public void setEmailAddress(String newUsername){
        emailAddress = newUsername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String newPassword){
        password = newPassword;
    }
}
