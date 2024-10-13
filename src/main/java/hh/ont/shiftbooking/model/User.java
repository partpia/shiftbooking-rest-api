package hh.ont.shiftbooking.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import hh.ont.shiftbooking.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="ACCOUNT")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="USER_ID", nullable=false, updatable = false)
    private Long userId;

    @Column(name="FIRST_NAME", nullable=false)
    private String firstName;

    @Column(name="LAST_NAME", nullable=false)
    private String lastName;

    @Column(name="EMAIL", nullable=false)
    private String email;

    @Column(name="PHONE_NUMBER", nullable=false)
    private String tel;

    @Column(name="USERNAME", nullable=false, unique = true)
    private String username;

    @Column(name="PASSWORD", nullable=false)
    private String password;

    @Column(name="ROLE", nullable=false)
    private Role role;

    @OneToMany(mappedBy = "employee")
    private List<Shift> shifts;

    @OneToMany(mappedBy = "contactPerson")
    private List<Workplace> myWorkplaces;

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

    public User(String firstName, String lastName, String email, String tel, String username, String password,
            Role role, List<Shift> shifts, List<Workplace> myWorkplaces) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.tel = tel;
        this.username = username;
        this.password = password;
        this.role = role;
        this.shifts = shifts;
        this.myWorkplaces = myWorkplaces;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(() -> this.role.toString());
    }
    
}
