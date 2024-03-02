package hh.ont.shiftbooking.service;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hh.ont.shiftbooking.dto.CreateUserDto;
import hh.ont.shiftbooking.dto.EmployeeAccountDto;
import hh.ont.shiftbooking.dto.EmployerAccountDto;
import hh.ont.shiftbooking.enums.Role;
import hh.ont.shiftbooking.exception.DatabaseException;
import hh.ont.shiftbooking.exception.UsernameExistsException;
import hh.ont.shiftbooking.model.User;
import hh.ont.shiftbooking.repository.UserRepository;

@Service
public class UserDetailService {

    @Autowired
    private UserRepository repository;
    
    // tallentaa uuden käyttäjän tiedot tietokantaan
    public boolean saveNewUser(CreateUserDto uDto) throws Exception {

        try {
            if (!checkUserExists(uDto.getUsername())) {
                User user = convertDtoToUser(uDto);
                repository.save(user);
                return true;
            } else {
                throw new UsernameExistsException("Valitse toinen käyttäjätunnus");
            }
        } catch (IllegalArgumentException e) {
            throw new DatabaseException("Tilin luonti epäonnistui.");
        }        
    }

    private boolean checkUserExists(String username) {
        return repository.findByUsername(username).isPresent();
    }

    private User convertDtoToUser(CreateUserDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setTel(dto.getTel());
        user.setUsername(dto.getUsername());
        // TODO: hash password
        //String pwd = dto.getPassword();
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        return user;
    }

    // hakee käyttäjän tiedot, palauttaa dto:n roolin mukaan
    public Object getAccountDetails(Long id) throws Exception {
        try {
            User user = repository.findById(id).get();
            if (user.getRole().equals(Role.EMPLOYEE)) {
                return new EmployeeAccountDto(user);
            } else {
                return new EmployerAccountDto(user);
            }
        } catch (NoSuchElementException | IllegalArgumentException e) {
            throw new DatabaseException("Tietojen haku epäonnistui.");
        }
    }
}
