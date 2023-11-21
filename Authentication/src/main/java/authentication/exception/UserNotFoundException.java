package authentication.exception;

import java.util.ArrayList;
import java.util.List;

public class UserNotFoundException  extends RuntimeException {

    private List<String> errorMessages = new ArrayList<>();

    public UserNotFoundException(List<String> errorMessages) {
        super(String.join(", ", errorMessages));
        System.out.println(errorMessages.toString());
        this.errorMessages = errorMessages;
    }

    public UserNotFoundException(String errorMessage) {
        super(errorMessage);
        System.out.println(errorMessage);
        this.errorMessages.add(errorMessage);
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
