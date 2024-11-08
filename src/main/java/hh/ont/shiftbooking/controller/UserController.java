package hh.ont.shiftbooking.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hh.ont.shiftbooking.dto.CreateUserDto;
import hh.ont.shiftbooking.dto.CredentialsDto;
import hh.ont.shiftbooking.dto.ShiftResponseDto;
import hh.ont.shiftbooking.dto.UpdateUserDto;
import hh.ont.shiftbooking.dto.WorkplaceResponseDto;
import hh.ont.shiftbooking.exception.PasswordMatchException;
import hh.ont.shiftbooking.model.User;
import hh.ont.shiftbooking.security.AuthenticationService;
import hh.ont.shiftbooking.service.ShiftService;
import hh.ont.shiftbooking.service.UserService;
import hh.ont.shiftbooking.service.WorkplaceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("accounts")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final WorkplaceService workService;
    private final ShiftService shiftService;
    private final AuthenticationService authService;

    // uuden käyttäjän/tilin lisääminen
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/register")
    public ResponseEntity<String> createNewUser(@Valid @RequestBody CreateUserDto userDTO) throws Exception {

        if (userDTO.getPassword().equals(userDTO.getPasswordCheck())) {
            User account = service.saveNewUser(userDTO);
            return ResponseEntity.created(new URI(String.valueOf(account.getUserId()))).build();
        } else {
            throw new PasswordMatchException("Salasanat eivät täsmää");
        }
    }

    // JSON Web Tokenin luominen
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/authenticate")
    public ResponseEntity<?> getToken(@RequestBody CredentialsDto dto) throws Exception {
        String token = authService.authenticate(dto);
        
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization").build();
    }

    // käyttäjätietojen hakeminen
    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.userId")
    public Object getAccount(@PathVariable(required = true) Long id) throws Exception {
        return service.getAccountDetails(id);
    }
    
    // poistaa käyttäjätilin
    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.userId")
    public ResponseEntity<String> deleteAccount(@PathVariable @NotNull Long id) throws Exception {

        boolean deleted = service.deleteAccount(id);

        return deleted ? new ResponseEntity<>(
            "Käyttätili poistettu.",
            HttpStatus.OK) :
            new ResponseEntity<>(
            "Käyttäjätilin poisto epäonnistui.",
            HttpStatus.BAD_REQUEST);
    }

    // käyttäjäroolin EMPLOYER työpaikkatietojen hakeminen (vain omat tiedot)
    @GetMapping("/{id}/workplaces")
    @PreAuthorize("hasAuthority('EMPLOYER') && #id == authentication.principal.userId")
    public List<WorkplaceResponseDto> getWorkplaces(@PathVariable Long id) throws Exception {
        return workService.getAllWorkplacesByEmployer(id);
    }

    // käyttäjäroolin EMPLOYEE työvuorotietojen hakeminen (vain omat tiedot)
    @GetMapping("/{id}/shifts")
    @PreAuthorize("hasAuthority('EMPLOYEE') && #id == authentication.principal.userId")
    public List<ShiftResponseDto> getShifts(@PathVariable Long id) throws Exception {
        return shiftService.getAllShiftsByEmployee(id);
    }

    // päivittää käyttäjän tiedot
    @PutMapping()
    @PreAuthorize("#dto.userId == authentication.principal.userId")
    public ResponseEntity<String> updateAccount(@Valid @RequestBody UpdateUserDto dto) throws Exception {
        boolean updated = service.updateUserDetails(dto);

        return updated ? new ResponseEntity<>(
            "Käyttäjän tiedot päivitetty.",
            HttpStatus.OK) :
            new ResponseEntity<>(
            "Käyttäjän tietojen päivitys epäonnistui.",
            HttpStatus.BAD_REQUEST);
    }
}
