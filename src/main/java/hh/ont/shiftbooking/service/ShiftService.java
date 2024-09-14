package hh.ont.shiftbooking.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import hh.ont.shiftbooking.dto.ShiftResponseDto;
import hh.ont.shiftbooking.enums.ShiftStatus;
import hh.ont.shiftbooking.exception.DatabaseException;
import hh.ont.shiftbooking.model.Shift;
import hh.ont.shiftbooking.model.User;
import hh.ont.shiftbooking.model.Workplace;
import hh.ont.shiftbooking.repository.ShiftRepository;
import hh.ont.shiftbooking.repository.UserRepository;
import hh.ont.shiftbooking.repository.WorkplaceRepository;
import jakarta.transaction.Transactional;

@Service
public class ShiftService {
    
    @Autowired
    ShiftRepository shiftRepository;

    @Autowired
    WorkplaceRepository workplaceRepository;

    @Autowired
    UserRepository userRepository;

    // tallentaa uuden työvuoron tiedot tietokantaan
    public Shift saveShift(Shift shift) throws DatabaseException {
        try {
            shift.setStatus(ShiftStatus.BOOKABLE);
            // haetaan työpaikan tiedot
            Long workplaceId = shift.getLocation().getWorkplaceId();
            Workplace workplace = workplaceRepository.findById(workplaceId).get();
            shift.setLocation(workplace);
            return shiftRepository.save(shift);
        } catch (NoSuchElementException | NullPointerException e) {
            throw new DatabaseException("Työpaikan tiedot virheelliset.");
        } catch (DataAccessException e) {
            throw new DatabaseException("Tietojen tallennus epäonnistui.");
        }
    }

    // hakee varattavissa olevat työvuorot tietokannasta
    public List<ShiftResponseDto> getAllBookableShifts() throws DatabaseException {
        try {
            List<Shift> list = (List<Shift>) shiftRepository.findByStatus(ShiftStatus.BOOKABLE);
            return !list.isEmpty() ? convertShiftListToDtos(list) : new ArrayList<>();
        } catch (Exception e) {
            throw new DatabaseException("Työvuoroja ei voida näyttää.");
        }
    }

    // muuntaa Shift-entityt dto-muotoon
    private List<ShiftResponseDto> convertShiftListToDtos(List<Shift> shifts) {
        List <ShiftResponseDto> dtos = new ArrayList<>();

        for (Shift shift : shifts) {
            ShiftResponseDto dto = new ShiftResponseDto(shift);
            dtos.add(dto);
        }
        return dtos;
    }

    // lisää työvuorolle työntekijän ja muuttaa vuoron statuksen varatuksi (ShiftStatus.BOOKED)
    @Transactional
    public boolean bookShift(Long shiftId, Long userId) throws DatabaseException {

        try {
            // haetaan työvuoron tiedot
            Shift shift = shiftRepository.findById(shiftId).orElseThrow(
                () -> new DatabaseException("Vuoron tietoja ei löytynyt."));

            if (isBookable(shift)) {
                shift.setStatus(ShiftStatus.BOOKED);
                
                // haetaan työntekijän tiedot
                User employee = userRepository.findById(userId).orElseThrow(
                    () -> new DatabaseException("Virhe käyttäjätietojen haussa."));
                
                shift.setEmployee(employee);
                shiftRepository.save(shift);
                return true;
            }
            return false;
        } catch (DatabaseException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    // poistaa työvuorolta työntekijän ja muuttaa vuoron statuksen varattavissa olevaksi (ShiftStatus.BOOKABLE)
    // perumisen ehtona: työvuoron alkuun on oltava vähintään kolme päivää
    public boolean cancelShift(Long id) throws Exception {

        try {
            Shift shift = shiftRepository.findById(id).orElseThrow(
                () -> new DatabaseException("Vuoron tietoja ei löytynyt."));

            if (shiftIsPossibleToCancel(shift)) {
                shift.setEmployee(null);
                shift.setStatus(ShiftStatus.BOOKABLE);
                shiftRepository.save(shift);

                return true;
            }
            return false;
        } catch (DatabaseException e) {
            throw new DatabaseException(e.getMessage());
        } catch (Exception e) {
            throw new DatabaseException("Vuoron peruutus epäonnistui. Ota yhteyttä työnantajaan.");
        }
    }

    // tarkastaa onko vuoro peruttavissa
    private boolean shiftIsPossibleToCancel(Shift calcelled) {
        
        LocalDateTime shiftStartTime = calcelled.getStartDateTime();
        LocalDateTime deadline = shiftStartTime.minusDays(3);

        return LocalDateTime.now().isBefore(deadline) ? true : false;
    }

    // tarkastaa onko vuoro varattavissa (ShiftStatus.BOOKABLE)
    private boolean isBookable(Shift bookable) {
        return bookable != null && bookable.getStatus().equals(ShiftStatus.BOOKABLE);
    }
}
