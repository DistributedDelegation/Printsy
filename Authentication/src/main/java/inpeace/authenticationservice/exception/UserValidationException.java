package inpeace.authenticationservice.exception;

import java.util.ArrayList;
import java.util.List;

public class UserValidationException extends RuntimeException{

    private List<String> errorMessages = new ArrayList<>();

    public UserValidationException(List<String> errorMessages){
        super(String.join(", ", errorMessages));
        System.out.println(errorMessages.toString());
        this.errorMessages = errorMessages;
    }

    public UserValidationException(String errorMessage){
        super(errorMessage);
        System.out.println(errorMessage);
        this.errorMessages.add(errorMessage);
    }


    public List<String> getErrorMessages(){
        return errorMessages;
    }
}
