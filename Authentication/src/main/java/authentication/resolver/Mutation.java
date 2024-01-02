package authentication.resolver;

import authentication.dto.UserCredentialInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import authentication.security.CustomUser;
import authentication.security.JWTService;
import authentication.security.UserDetailsConfig;
import authentication.service.UserService;

@Controller
public class Mutation {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserDetailsConfig userDetailsConfig;
    private final UserService userService;

    @Autowired
    public Mutation(AuthenticationManager authenticationManager, JWTService jwtService,
                    UserDetailsConfig userDetailsConfig, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsConfig = userDetailsConfig;
        this.userService = userService;
    }

    @MutationMapping
    public String authenticate(@Argument UserCredentialInput userCredentialInput) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userCredentialInput.getEmailAddress(),
                            userCredentialInput.getPassword()));
        } catch (BadCredentialsException e) {
            return "Incorrect email address or password: " + e.getMessage();
        }

        final CustomUser userDetails = userDetailsConfig.loadUserByUsername(userCredentialInput.getEmailAddress());
        return jwtService.createToken(userDetails.getUsername(), userDetails.getUserID());
    }

    @MutationMapping
    public String register(@Argument UserCredentialInput userCredentialInput) {
        try {
            userService.registerNewUser(userCredentialInput);
            return "User registered successfully.";
        } catch (Exception ex) {
            return "Registration error: " + ex.getMessage();
        }

    }
}