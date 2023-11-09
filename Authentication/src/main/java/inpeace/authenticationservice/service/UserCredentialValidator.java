package inpeace.authenticationservice.service;

import inpeace.authenticationservice.dao.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

@Service
public class UserCredentialValidator {

    private final UserRepository userRepository;

    public UserCredentialValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validatePassword(String password, Errors errors) {

        // Check Password is not empty
        if (password.isEmpty()) {
            System.out.println("Password cannot be empty");
            errors.reject("Invalid.password.null");
        }

        // Check Password length and characters
        if (password.length() < 8) {
            System.out.println("Password must have over 8 characters.");
            errors.reject("Invalid.password.length");
        }

        // Assuming a valid password needs to contain at least one uppercase letter, one lowercase letter, one number and one special character
        if (!(containsLowerCase(password) && containsUpperCase(password) && containsSpecialCharacter(password) && containsNumber(password))) {
            System.out.println("Password must have at least one upper case, one lower case, and one special character.");
            errors.reject("Invalid.password.characters");
        }
    }

    public void validateEmailAddress(String emailAddress, Errors errors) {
        // Check Password is not empty
        if (emailAddress.isEmpty()) {
            System.out.println("Email address cannot be empty.");
            errors.reject("Invalid.password.null");
        }

        // Check if Email exits
        if (userRepository.existsByEmailAddress(emailAddress)) {
            System.out.println("Email address is already registered.");
            errors.reject("Invalid.email.address");
        }

        // Check email format
        if (!emailAddress.matches("[A-Za-z0-9+_.\\-]+@[A-Za-z0-9+_.\\-]+\\.[A-Za-z0-9+_.\\-]{2,}")) {
            System.out.println("Invalid email format.");
            errors.reject("Invalid.email.format");
        }

    }
    private boolean containsUpperCase(String input) {
        for (char c : input.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsLowerCase(String input) {
        for (char c : input.toCharArray()) {
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsNumber(String input) {
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsSpecialCharacter(String input) {
        for (char c : input.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return true;
            }
        }
        return false;
    }
}
