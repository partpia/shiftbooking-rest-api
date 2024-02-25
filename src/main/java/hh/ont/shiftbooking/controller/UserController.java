package hh.ont.shiftbooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hh.ont.shiftbooking.dto.CreateUserDto;
import hh.ont.shiftbooking.exception.PasswordMatchException;
import hh.ont.shiftbooking.service.UserDetailService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("accounts")
public class UserController {

    @Autowired
    private UserDetailService service;
    
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
}
