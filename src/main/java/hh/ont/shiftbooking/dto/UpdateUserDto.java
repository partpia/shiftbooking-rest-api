package hh.ont.shiftbooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {

    @NotNull
    private Long userId;
    
    private String firstName;

    private String lastName;
    
    @Email
    private String email;
    
    private String tel;

    private String password;

    private String newPassword;

    private String passwordCheck;
}
