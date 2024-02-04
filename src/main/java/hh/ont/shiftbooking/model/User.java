package hh.ont.shiftbooking.model;

import java.util.List;

import hh.ont.shiftbooking.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

    // TODO: puuttuvat tietokantamääritykset

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String tel;
    private String username;
    private String password;
    private Role role;
    private List<Shift> shifts;

    public User(String firstName, String lastName, String email, String tel, String username, String password,
            Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.tel = tel;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(String firstName, String lastName, String email, String tel, String username, String password,
            Role role, List<Shift> shifts) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.tel = tel;
        this.username = username;
        this.password = password;
        this.role = role;
        this.shifts = shifts;
    }

}
