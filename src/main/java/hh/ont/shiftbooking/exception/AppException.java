package hh.ont.shiftbooking.exception;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AppException {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

}
