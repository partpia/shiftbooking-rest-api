package hh.ont.shiftbooking.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import hh.ont.shiftbooking.dto.WorkplaceResponseDto;
import hh.ont.shiftbooking.exception.DatabaseException;
import hh.ont.shiftbooking.model.PostOffice;
import hh.ont.shiftbooking.model.User;
import hh.ont.shiftbooking.model.Workplace;
import hh.ont.shiftbooking.repository.UserRepository;
import hh.ont.shiftbooking.repository.WorkplaceRepository;

@Service
public class WorkplaceService {
    
    private final WorkplaceRepository workRepository;
    private final PostOfficeService postService;
    private final UserRepository userRepository;

    public WorkplaceService(WorkplaceRepository workRepository, PostOfficeService postService,
            UserRepository userRepository) {
        this.workRepository = workRepository;
        this.postService = postService;
        this.userRepository = userRepository;
    }

    /**
     * Tallentaa uuden työpaikan tiedot tietokantaan.
     * @param workplace
     * @return Tallennetun työpaikan tiedot (WorkplaceResponseDto)
     * @throws Exception
     */
    public WorkplaceResponseDto saveWorkplace(Workplace workplace) throws Exception {

        try {
            // haetaan postitoimipaikan tiedot
            PostOffice postOffice = postService.getPostOfficeDetails(workplace.getZip());
            workplace.setZip(postOffice);
            // haetaan työnantajan tiedot
            User employer = userRepository.findById(workplace.getContactPerson().getUserId()).orElseThrow(
                () -> new DatabaseException("Työpaikan yhteyshenkilön tietoja ei löytynyt."));
            workplace.setContactPerson(employer);
            Workplace saved = workRepository.save(workplace);
            return new WorkplaceResponseDto(saved);
        } catch (IllegalArgumentException e) {
            throw new DatabaseException("Tallennus epäonnistui.");
        }
    }

    /**
     * Poistaa työpaikan tiedot tietokannasta.
     * @param id Poistettavan työpaikan yksilöllinen tunnus
     * @return Poisto-operaatio tehty (true/false)
     */
    public boolean deleteWorkplace(Long id) throws Exception{

        try {
            workRepository.findById(id).orElseThrow(
                () -> new DatabaseException("Virheelliset työpaikan tiedot."));
            workRepository.deleteById(id);
            return true;
        } catch (IllegalArgumentException e) {
            throw new DatabaseException("Työpaikan tietojen poisto epäonnistui.");
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("""
                Työpaikan tietojen poisto epäonnistui. Työpaikalla on työvuoroja varattavissa.""");
        }
    }
}
