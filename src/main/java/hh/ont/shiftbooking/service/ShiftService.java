package hh.ont.shiftbooking.service;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import hh.ont.shiftbooking.enums.ShiftStatus;
import hh.ont.shiftbooking.exception.DatabaseException;
import hh.ont.shiftbooking.model.Shift;
import hh.ont.shiftbooking.model.Workplace;
import hh.ont.shiftbooking.repository.ShiftRepository;
import hh.ont.shiftbooking.repository.WorkplaceRepository;

@Service
public class ShiftService {
    
    @Autowired
    ShiftRepository shiftRepository;

    @Autowired
    WorkplaceRepository workplaceRepository;

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
}
