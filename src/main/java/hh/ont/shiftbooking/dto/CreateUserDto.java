package hh.ont.shiftbooking.dto;

import hh.ont.shiftbooking.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateUserDto {

    // TODO: lisää messaget validointivirheitä varten
    
    @NotEmpty @Size(min=1, max=30)
    private String firstName;
    @NotEmpty @Size(min=1, max=50)
    private String lastName;
    @Email
    private String email;
    @NotEmpty @Size(min=6, max=15)
    private String tel;
    @NotEmpty @Size(min=1, max=30)
    private String username;
    @NotEmpty @Size(min=8)
    private String password;
    @NotEmpty @Size(min=8)
    private String passwordCheck;
    @NotNull
    private Role role;

}
