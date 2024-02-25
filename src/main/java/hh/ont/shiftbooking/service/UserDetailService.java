package hh.ont.shiftbooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hh.ont.shiftbooking.dto.CreateUserDto;
import hh.ont.shiftbooking.exception.UsernameExistsException;
import hh.ont.shiftbooking.model.User;
import hh.ont.shiftbooking.repository.UserRepository;

@Service
public class UserDetailService {

    @Autowired
    private UserRepository repository;
    
    public boolean saveNewUser(CreateUserDto uDto) throws Exception {

        if (!checkUserExists(uDto.getUsername())) {
            User user = new User();
            user.setFirstName(uDto.getFirstName());
            user.setLastName(uDto.getLastName());
            user.setEmail(uDto.getEmail());
            user.setTel(uDto.getTel());
            user.setUsername(uDto.getUsername());
            // TODO: hash password
            //String pwd = uDto.getPassword();
            user.setPassword(uDto.getPassword());
            user.setRole(uDto.getRole());
            repository.save(user);
            return true;
        } else {
            throw new UsernameExistsException("Valitse toinen käyttäjätunnus");
        }
    }

    private boolean checkUserExists(String username) {
        return repository.findByUsername(username).isPresent();
    }

}
