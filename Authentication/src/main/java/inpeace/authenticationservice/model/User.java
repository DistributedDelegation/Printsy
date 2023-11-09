package inpeace.authenticationservice.model;

import java.sql.*;
import java.time.LocalDateTime;


import jakarta.persistence.*;

@Entity
@Table(name="users")
public class User {

    @Id
    @Column(name="userid")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long userID;

    @Column (nullable = false, unique = true, name="emailaddress")
    private String emailAddress;

    @Column (nullable = false, name="encodedpassword")
    private String encodedPassword;

    @Column(nullable = false, name="username")
    private String username;

    @Column(nullable = false, name="datecreated")
    private LocalDateTime dateCreated;

    @Column(nullable = false, name="dateupdated")
    private LocalDateTime dateUpdated;

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

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }


    @Override
    public String toString() {
        return "UserDAO{" +
                "emailAddress='" + emailAddress + '\'' +
                '}';
    }
}
