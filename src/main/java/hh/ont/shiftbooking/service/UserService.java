package hh.ont.shiftbooking.service;

import java.util.NoSuchElementException;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import hh.ont.shiftbooking.dto.CreateUserDto;
import hh.ont.shiftbooking.dto.EmployeeAccountDto;
import hh.ont.shiftbooking.dto.EmployerAccountDto;
import hh.ont.shiftbooking.dto.UpdateUserDto;
import hh.ont.shiftbooking.enums.Role;
import hh.ont.shiftbooking.exception.DatabaseException;
import hh.ont.shiftbooking.exception.PasswordMatchException;
import hh.ont.shiftbooking.exception.UsernameExistsException;
import hh.ont.shiftbooking.model.User;
import hh.ont.shiftbooking.repository.UserRepository;
import hh.ont.shiftbooking.util.Utility;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    // tallentaa uuden käyttäjän tiedot tietokantaan
    public User saveNewUser(CreateUserDto uDto) throws Exception {

        try {
            if (!checkUserExists(uDto.getUsername())) {
                User user = convertDtoToUser(uDto);
                return repository.save(user);
            } else {
                throw new UsernameExistsException("Valitse toinen käyttäjätunnus");
            }
        } catch (IllegalArgumentException e) {
            throw new DatabaseException("Tilin luonti epäonnistui.");
        }        
    }

    // poistaa käyttäjätilin
    public boolean deleteAccount(Long id) throws Exception {

        try {
            repository.findById(id).orElseThrow(
                () -> new DatabaseException("Virheelliset käyttäjätiedot."));
            repository.deleteById(id);
            return true;
        } catch (IllegalArgumentException e) {
            throw new DatabaseException("Käyttäjätilin poisto epäonnistui.");
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
        user.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        user.setRole(dto.getRole());
        return user;
    }

    // hakee käyttäjän tiedot, palauttaa dto:n roolin mukaan
    public Object getAccountDetails(Long id) throws Exception {
        try {
            User user = repository.findById(id).orElseThrow(
                () -> new DatabaseException("Virheelliset käyttäjätiedot."));
            if (user.getRole().equals(Role.EMPLOYEE)) {
                return new EmployeeAccountDto(user);
            } else {
                return new EmployerAccountDto(user);
            }
        } catch (NoSuchElementException | IllegalArgumentException e) {
            throw new DatabaseException("Tietojen haku epäonnistui.");
        }
    }

    /**
     * Päivittää käyttäjän tiedot tietokantaan.
     * @param dto Päivitetyt tiedot UpdateUserDto-objektissa
     * @return Käyttäjän tietojen päivitys onnistui (true/false)
     * @throws Exception
     */
    public boolean updateUserDetails(UpdateUserDto dto) throws Exception {

        try {
            User current = repository.findById(dto.getUserId()).orElseThrow(
                () -> new DatabaseException("Virheelliset käyttäjätiedot."));
            User user = convertToEntity(current, dto);
            repository.save(user);
            return true;
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            throw new DatabaseException("Käyttäjätietojen päivitys epäonnistui.");
        }
    }

    /**
     * Muuntaa dto:n entityksi.
     * @param cur Käyttäjän nykyiset tiedot User-objektina
     * @param dto Päivitetyt käyttäjän tiedot UpdateUserDto-objektina
     * @return Käyttäjän päivitety tiedot User-entityna
     * @throws Exception
     */
    private User convertToEntity(User cur, UpdateUserDto dto) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // jos uusi salasana annettu, vaihdetaan
        if (checkPasswordUpdate(dto)) {
            dto = handlePasswordChange(cur, dto);
        }

        // jos muuttujalle ei ole uutta arvoa, estää null-arvon asettamisen
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // ohittaa tuntemattomat arvot
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // mappaa annetut arvot dto:sta entityyn
        mapper.updateValue(cur, dto);

        return cur;
    }

    // TODO: aikamoinen himmeli, refaktoroi
    private UpdateUserDto handlePasswordChange(User cur, UpdateUserDto dto) throws Exception {

        String dtoPwd = dto.getPassword();
        String newPwd = dto.getNewPassword();

        // dto:ssa välitetty salasana ei voi olla tyhjä
        if (dtoPwd.trim() == "") {
            throw new PasswordMatchException("Virheellinen tieto.");
        }
        // tarkastetaan täsmääkö pyynnössä välitetty nykyinen salasana tietokannassa olevaan
        if (!dtoPwd.equals(cur.getPassword())) {
            throw new PasswordMatchException("Salasanan vaihto epäonnistui.");
        }
        // tarkastetaan, ettei uusi salasana ja salasanan vahvistus ole null/tyhjä, pituus vähintään 8 merkkiä
        if (Utility.isBlank(newPwd) || Utility.isBlank(dto.getPasswordCheck()) || newPwd.length() < 8 ) {
            throw new PasswordMatchException("Salasanan vaihto epäonnistui. Tarkasta salasanan vaatimukset.");
        }
        // tarkastetaan, että salasanat täsmäävät
        if (!newPwd.equals(dto.getPasswordCheck())) {
            throw new PasswordMatchException("Salasanat eivät täsmää");
        }
        // asetetaan uusi salasana
        dto.setPassword(newPwd);
        return dto;
    }

    // Tarkastaa, onko UpdateUserDto-objektissa annettu salasana
    private boolean checkPasswordUpdate(UpdateUserDto dto) throws Exception {
        String dtoPwd = dto.getPassword();
        return dtoPwd == null ? false : true;
    }
}
