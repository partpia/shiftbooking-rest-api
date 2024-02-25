package hh.ont.shiftbooking.exception;

public class UsernameExistsException extends Exception {
    
    public UsernameExistsException(String errorMessage) {
        super(errorMessage);
    }
}
