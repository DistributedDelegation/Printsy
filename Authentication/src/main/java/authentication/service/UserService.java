package authentication.service;

import authentication.exception.UserNotFoundException;
import authentication.repository.UserRepository;
import authentication.dto.UserCredentialInput;
import authentication.exception.UserValidationException;
import authentication.model.User;
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

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
            UserCredentialValidator userCredentialValidator, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userCredentialValidator = userCredentialValidator;
        this.messageSource = messageSource;
    }


    public void registerNewUser(UserCredentialInput userCredentialRequest) {
        // Log registration of new user
        System.out.println("Registering new user!");

        // Create a new user
        User newUser = new User();

        // Check email address and password
        Errors errors = new BeanPropertyBindingResult(userCredentialRequest, "newUser");
        userCredentialValidator.validatePassword(userCredentialRequest.getPassword(), errors);
        userCredentialValidator.validateEmailAddress(userCredentialRequest.getEmailAddress(), errors);

        if (errors.hasErrors()) {
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

        userRepository.save(newUser);
    }

    public Boolean checkUserName(String username){
        return userRepository.existsByUsername(username);
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("UserID not found: " + id));
    }
}
