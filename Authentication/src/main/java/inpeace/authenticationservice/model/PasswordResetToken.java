package inpeace.authenticationservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name="passwordresettokens")
public class PasswordResetToken {

    @Id
    @Column(name="userid")
    private Long userID;

    @Column(name="token")
    private String token;

    @Column(name="expirydate")
    private LocalDateTime expirationDate;

    public PasswordResetToken(){
    }
    public PasswordResetToken(User user, String token) {
        this.userID = user.getUserID();
        this.token = token;
        this.expirationDate = LocalDateTime.now().plusMinutes(10);
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expiration) {
        this.expirationDate = expiration;
    }
}
