package inpeace.authenticationservice.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serial;
import java.util.Collection;

public class CustomUser extends User {

    @Serial
    private static final long serialVersionUID = 1L;
    private final Long userID;

    public CustomUser(Long userID, String emailAddress, String password, Collection<? extends GrantedAuthority> authorities) {
        super(emailAddress, password, authorities);
        this.userID = userID;
    }

    public Long getUserID() {
        return userID;
    }
}
