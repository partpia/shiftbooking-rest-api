package hh.ont.shiftbooking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CredentialsDto {

    private String username;
    private String password;

}
