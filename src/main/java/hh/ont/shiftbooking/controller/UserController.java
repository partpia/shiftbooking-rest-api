package hh.ont.shiftbooking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hh.ont.shiftbooking.dto.CreateUserDto;
import hh.ont.shiftbooking.dto.ShiftResponseDto;
import hh.ont.shiftbooking.dto.WorkplaceResponseDto;
import hh.ont.shiftbooking.exception.PasswordMatchException;
import hh.ont.shiftbooking.service.ShiftService;
import hh.ont.shiftbooking.service.UserDetailService;
import hh.ont.shiftbooking.service.WorkplaceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


@RestController
@RequestMapping("accounts")
public class UserController {

    @Autowired
    private UserDetailService service;

    @Autowired
    private WorkplaceService workService;

    @Autowired
    private ShiftService shiftService;

    // uuden käyttäjän/tilin lisääminen
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createNewUser(@Valid @RequestBody CreateUserDto userDTO) throws Exception {

        if (userDTO.getPassword().equals(userDTO.getPasswordCheck())) {
            boolean userCreated = service.saveNewUser(userDTO);
            return userCreated ? new ResponseEntity<>(
                "Tili luotu käyttäjätunnuksella " + userDTO.getUsername(),
                HttpStatus.CREATED) :
                new ResponseEntity<>(
                "Tilin luonti epäonnistui",
                HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            throw new PasswordMatchException("Salasanat eivät täsmää");
        }
    }

    // käyttäjätietojen hakeminen
    // TODO: pathvariable pois > jwt:n rooli ?
    @GetMapping("/{id}")
    public Object getAccount(@PathVariable(required = true) Long id) throws Exception {
        return service.getAccountDetails(id);
    }
    
    // poistaa käyttäjätilin
    // TODO: todentaminen, oikeus poistoon
    @DeleteMapping("/{id}")
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
    public List<WorkplaceResponseDto> getWorkplaces(@PathVariable Long id) throws Exception {
        // TODO: hakuoikeus
        return workService.getAllWorkplacesByEmployer(id);
    }

    // käyttäjäroolin EMPLOYEE työvuorotietojen hakeminen (vain omat tiedot)
    @GetMapping("/{id}/shifts")
    public List<ShiftResponseDto> getShifts(@PathVariable Long id) throws Exception {
        // TODO: hakuoikeus
        return shiftService.getAllShiftsByEmployee(id);
    }

    // TODO: put
}
