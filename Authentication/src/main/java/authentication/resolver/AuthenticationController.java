//package authentication.resolver;
//
//import authentication.dto.UserCredentialRequest;
//import authentication.exception.UserValidationException;
//import authentication.security.CustomUser;
//import authentication.security.JWTService;
//import authentication.security.UserDetailsConfig;
//import authentication.service.UserService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.authentication.security.authentication.AuthenticationManager;
//import org.springframework.authentication.security.authentication.BadCredentialsException;
//import org.springframework.authentication.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.authentication.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//public class AuthenticationController {
//
//    private final AuthenticationManager authenticationManager;
//    private final JWTService jwtService;
//    private final UserDetailsConfig userDetailsConfig;
//    private final UserService userService;
//    private final ControllerUtils controllerUtils;
//
//    private final BCryptPasswordEncoder registerPasswordEncoder = new BCryptPasswordEncoder();
//
//    // Inject the dependencies via the constructor
//    public AuthenticationController(AuthenticationManager authenticationManager, JWTService jwtService,
//            UserDetailsConfig userDetailsConfig, UserService userService, ControllerUtils controllerUtils) {
//        this.authenticationManager = authenticationManager;
//        this.jwtService = jwtService;
//        this.userDetailsConfig = userDetailsConfig;
//        this.userService = userService;
//        this.controllerUtils = controllerUtils;
//    }
//
//    @PostMapping("/authenticate")
//    public ResponseEntity<?> createAuthenticationToken(@RequestBody UserCredentialRequest userCredentialRequest)
//            throws Exception {
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            userCredentialRequest.getEmailAddress(),
//                            userCredentialRequest.getPassword()));
//        } catch (BadCredentialsException e) {
//            throw new Exception("Incorrect email address or password", e);
//        }
//
//        final CustomUser userDetails = userDetailsConfig.loadUserByUsername(userCredentialRequest.getEmailAddress());
//
//        final String jwt = jwtService.createToken(userDetails.getUsername(), userDetails.getUserID());
//
//        return ResponseEntity.ok(jwt);
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody UserCredentialRequest userCredentialRequest) {
//        try {
//            userService.registerNewUser(userCredentialRequest);
//            return ResponseEntity.ok(controllerUtils.getSuccessResponse("User registered successfully."));
//        } catch (UserValidationException ex) {
//            System.out.println(ex.toString());
//            return ResponseEntity.badRequest().body(controllerUtils.getErrorResponse(ex.getMessage()));
//        } catch (Exception ex) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(controllerUtils.getErrorResponse("An error occurred."));
//        }
//
//    }
//
//    @GetMapping("/current-user")
//    public ResponseEntity<?> currentUser(@RequestHeader("Authorization") String bearer) {
//        try {
//            Long id = jwtService.parseTokenGetUserID(controllerUtils.getTokenFromBearer(bearer));
//            return ResponseEntity.ok(userService.getUserById(id));
//        } catch (Exception ex) {
//            System.out.println(ex.toString());
//            return ResponseEntity.badRequest().body(controllerUtils.getErrorResponse(ex.getMessage()));
//        }
//    }
//
//    @GetMapping("/username-available/{username}")
//    public ResponseEntity<?> availableUserName(@PathVariable String suggestedUserName){
//        try {
//            Boolean isAvailable = userService.checkUserName(suggestedUserName);
//            return ResponseEntity.ok(controllerUtils.getSuccessResponse("Username availability: " + isAvailable));
//        } catch (Exception ex) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(controllerUtils.getErrorResponse("An error occurred."));
//        }
//    }
//
//}
