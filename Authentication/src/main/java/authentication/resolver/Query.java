package authentication.resolver;

import authentication.model.User;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import authentication.security.JWTService;
import authentication.service.UserService;

@Controller
public class Query {

    private final JWTService jwtService;
    private final UserService userService;
    private final ControllerUtils controllerUtils;

    @Autowired
    public Query(JWTService jwtService, UserService userService, ControllerUtils controllerUtils) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.controllerUtils = controllerUtils;
    }

    @QueryMapping
    public User currentUser(@Argument String bearer) {
        try {
            Long id = jwtService.parseTokenGetUserID(controllerUtils.getTokenFromBearer(bearer));
            return userService.getUserById(id);
        } catch (Exception ex) {
            throw new RuntimeException("Error retrieving current user: " + ex.getMessage());
        }

        // TODO More helpful error handling
    }

    @QueryMapping
    public Boolean isUsernameAvailable(@Argument String username) {
        try {
            return userService.checkUserName(username);
        } catch (Exception ex) {
            throw new RuntimeException("Error checking username availability: " + ex.getMessage());
        }

        // TODO More helpful error handling
    }
}
