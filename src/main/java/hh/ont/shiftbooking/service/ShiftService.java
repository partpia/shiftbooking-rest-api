package hh.ont.shiftbooking.service;

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
import hh.ont.shiftbooking.model.Workplace;
import hh.ont.shiftbooking.repository.ShiftRepository;
import hh.ont.shiftbooking.repository.WorkplaceRepository;

@Service
public class ShiftService {
    
    @Autowired
    ShiftRepository shiftRepository;

    @Autowired
    WorkplaceRepository workplaceRepository;

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
}
