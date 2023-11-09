package inpeace.authenticationservice.controller;

import inpeace.authenticationservice.dto.AuthenticationResponse;
import inpeace.authenticationservice.dto.CurrentUserResponse;
import inpeace.authenticationservice.dto.UserCredentialRequest;
import inpeace.authenticationservice.exception.UserValidationException;
import inpeace.authenticationservice.security.CustomUser;
import inpeace.authenticationservice.security.JWTService;
import inpeace.authenticationservice.security.UserDetailsConfig;
import inpeace.authenticationservice.service.EmailService;
import inpeace.authenticationservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserDetailsConfig userDetailsConfig;
    private final UserService userService;
    private final ControllerUtils controllerUtils;
    private final EmailService emailService;

    BCryptPasswordEncoder registerPasswordEncoder = new BCryptPasswordEncoder();


    //  Inject the dependencies via the constructor
    public AuthenticationController (AuthenticationManager authenticationManager, JWTService jwtService,
                                     UserDetailsConfig userDetailsConfig, UserService userService, ControllerUtils controllerUtils,
                                     EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsConfig = userDetailsConfig;
        this.userService = userService;
        this.controllerUtils = controllerUtils;
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String hello() {
        return "Hello from the Authentication Service!";
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody UserCredentialRequest userCredentialRequest) throws Exception {
        System.out.println("Hi made it to the authenticate route");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userCredentialRequest.getEmailAddress(),
                            userCredentialRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new Exception ("Incorrect email address or password", e);
        }

        final CustomUser userDetails = userDetailsConfig.loadUserByUsername(userCredentialRequest.getEmailAddress());

        final String jwt = jwtService.createToken(userDetails.getUsername(), userDetails.getUserID());

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserCredentialRequest userCredentialRequest) {
        try {
            userService.registerNewUser(userCredentialRequest);
            return ResponseEntity.ok(controllerUtils.getSuccessResponse("User registered successfully."));
        }
        catch (UserValidationException ex) {
            System.out.println(ex.toString());
            return ResponseEntity.badRequest().body(controllerUtils.getErrorResponse(ex.getMessage()));
        }
        catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(controllerUtils.getErrorResponse("An error occurred."));
        }

    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody UserCredentialRequest userCredentialRequest, @RequestHeader("Authorization") String token) {
       try {
           if (token != null && token.startsWith("Bearer ")) {
               token = token.substring("Bearer ".length());
           }
           String emailAddress = jwtService.parseTokenGetEmailAddress(token);
           System.out.println("Parsed emailed address:" + emailAddress);
           userService.updateUser(userCredentialRequest, emailAddress);
           return ResponseEntity.ok(controllerUtils.getSuccessResponse("User updated successfully."));
       }
       catch (UserValidationException ex) {
           System.out.println(ex.toString());
           return ResponseEntity.badRequest().body(controllerUtils.getErrorResponse(ex.getMessage()));
       }
       catch (Exception ex) {
           System.out.println(ex.toString());
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(controllerUtils.getErrorResponse("An error occurred."));
       }
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> currentUser(@RequestHeader("Authorization") String bearer){
        try {
            String token = controllerUtils.getTokenFromBearer(bearer);
            return ResponseEntity.ok(new CurrentUserResponse(jwtService.parseTokenGetUserID(token), jwtService.parseTokenGetEmailAddress(token)));
        }
        catch (Exception ex){
            System.out.println(ex.toString());
            return ResponseEntity.badRequest().body(controllerUtils.getErrorResponse(ex.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> requestPasswordReset(@RequestBody String email){
        try {
            emailService.requestPasswordReset(email);
            return ResponseEntity.ok(controllerUtils.getSuccessResponse("Password reset email sent successfully"));
        }
        catch (Exception ex) {
            System.out.println(ex.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(controllerUtils.getErrorResponse(ex.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody UserCredentialRequest userCredentialRequest) {
        try {
            userService.resetPassword(userCredentialRequest, token);
            return ResponseEntity.ok( controllerUtils.getSuccessResponse("Password updated successfully"));
        }
        catch (Exception ex){
            System.out.println(ex.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(controllerUtils.getErrorResponse(ex.getMessage()));
        }

    }
}

