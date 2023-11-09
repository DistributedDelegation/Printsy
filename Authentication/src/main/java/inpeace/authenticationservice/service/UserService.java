package inpeace.authenticationservice.service;

import inpeace.authenticationservice.dao.PasswordResetTokenRepository;
import inpeace.authenticationservice.dao.UserRepository;
import inpeace.authenticationservice.dto.UserCredentialRequest;
import inpeace.authenticationservice.exception.UserValidationException;
import inpeace.authenticationservice.model.PasswordResetToken;
import inpeace.authenticationservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserCredentialValidator userCredentialValidator;
    private final MessageSource messageSource;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                       UserCredentialValidator userCredentialValidator, MessageSource messageSource,
                       PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userCredentialValidator = userCredentialValidator;
        this.messageSource = messageSource;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public void registerNewUser(UserCredentialRequest userCredentialRequest) {
        //  Log registration of new user
        System.out.println("Registering new user!");

        // Create a new user
        User newUser = new User();

        //   Check email address and password
        Errors errors = new BeanPropertyBindingResult(userCredentialRequest, "newUser");
        userCredentialValidator.validatePassword(userCredentialRequest.getPassword(), errors);
        userCredentialValidator.validateEmailAddress(userCredentialRequest.getEmailAddress(), errors);

        if (errors.hasErrors()){
            List<String> errorMessages = errors.getAllErrors()
                    .stream()
                    .map(objectError -> messageSource.getMessage(objectError.getCode(), null, Locale.getDefault()))
                    .toList();
            throw new UserValidationException(errorMessages);
        }


        // If no errors, assign properties to user
        newUser.setEmailAddress(userCredentialRequest.getEmailAddress());
        newUser.setEncodedPassword(passwordEncoder.encode(userCredentialRequest.getPassword()));
        newUser.setUsername(userCredentialRequest.getEmailAddress());
        newUser.setDateCreated(LocalDateTime.now());
        newUser.setDateUpdated(LocalDateTime.now());

        userRepository.save(newUser);
    }

    public void updateUser(UserCredentialRequest updateUserRequest, String emailAddress){
        //  Log registration of new users
        System.out.println("Updating user info!");
        System.out.println(emailAddress);

        User updatedUser = userRepository.findByEmailAddress(emailAddress)
                .orElseThrow(()-> new UserValidationException(List.of("User not found with the provided email address")));

        //   Check email address and password
        Errors errors = new BeanPropertyBindingResult(updateUserRequest, "updatedUser");

        if (updateUserRequest.getEmailAddress() != null && !updateUserRequest.getEmailAddress().isEmpty() && !updateUserRequest.getEmailAddress().equals(updatedUser.getEmailAddress())){
            userCredentialValidator.validateEmailAddress(updateUserRequest.getEmailAddress(), errors);
        }
        if (updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isEmpty()){
            userCredentialValidator.validatePassword(updateUserRequest.getPassword(), errors);
        }

        if (errors.hasErrors()){
            List<String> errorMessages = errors.getAllErrors()
                    .stream()
                    .map(objectError -> messageSource.getMessage(objectError.getCode(), null, Locale.getDefault()))
                    .toList();
            throw new UserValidationException(errorMessages);
        }

        // If no errors, assign properties to user
        if (updateUserRequest.getEmailAddress() != null){
            updatedUser.setEmailAddress(updateUserRequest.getEmailAddress());
        }
        if (updateUserRequest.getPassword() != null){
            updatedUser.setEncodedPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
        }
        updatedUser.setDateUpdated(LocalDateTime.now());

        userRepository.save(updatedUser);
    }

    public void resetPassword(UserCredentialRequest updatePasswordRequest, String token){
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new UserValidationException("Token is invalid or has expired");
        }

        User user = userRepository.findById(resetToken.getUserID())
                .orElseThrow(()-> new UserValidationException(List.of("User not found.")));

        user.setEncodedPassword(passwordEncoder.encode(updatePasswordRequest.getPassword()));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }


}
