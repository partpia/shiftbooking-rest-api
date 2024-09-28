package hh.ont.shiftbooking.service;

import java.util.ArrayList;
import java.util.List;

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
     * Palauttaa kaikkien työpaikkojen tiedot.
     * @return Listan työpaikoista
     * @throws Exception
     */
    public List<WorkplaceResponseDto> getAllWorkplaces() throws Exception {

        try {
            List<Workplace> list = (List<Workplace>) workRepository.findAll();
            return list.isEmpty() ? new ArrayList<>() : convertWorkplaceListToDtos(list);
        } catch (Exception e) {
            throw new DatabaseException("Työpaikkoja ei voida näyttää.");
        }
    }
    /**
     * Hakee työpaikkojen tiedot, joissa operaation tekijä on merkitty työpaikan yhteyshenkilöksi.
     * @param id Työpaikan yhteyshenkilön yksilöllinen tunnus
     * @return Listan työpaikoista, joissa työpaikka-listauksen hakija on työpaikan yhteyshenkilönä.
     * @throws DatabaseException
     */
    public List<WorkplaceResponseDto> getAllWorkplacesByEmployer(Long id) throws DatabaseException {
        try {
            User user = userRepository.findById(id).get();
            List<Workplace> list = user.getMyWorkplaces();
            return list.isEmpty() ? new ArrayList<>() : convertWorkplaceListToDtos(list);
        } catch (Exception e) {
            throw new DatabaseException("Työpaikkoja ei voida näyttää.");
        }
    }

    /**
     * Päivittää työpaikan tiedot.
     * @param workplace
     * @return Päivitysoperaatio onnistui (true/false)
     * @throws Exception
     */
    public boolean updateWorkplaceDetails(Workplace updated) throws Exception {

        try {
            workRepository.findById(updated.getWorkplaceId()).orElseThrow(
                () -> new DatabaseException("Virheelliset työpaikan tiedot."));
            // postitoimipaikan tiedot
            PostOffice postOffice = postService.getPostOfficeDetails(updated.getZip());
            updated.setZip(postOffice);
            // työnantajan tiedot
            userRepository.findById(updated.getContactPerson().getUserId()).orElseThrow(
                () -> new DatabaseException("Työpaikan yhteyshenkilön tiedot virheelliset."));
            
            workRepository.save(updated);
            return true;
        } catch (IllegalArgumentException e) {
            throw new DatabaseException("Päivitys epäonnistui.");
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

    // muuntaa Workplace-entityt dto-muotoon
    public List<WorkplaceResponseDto> convertWorkplaceListToDtos(List<Workplace> workplaces) {
        List<WorkplaceResponseDto> dtos = new ArrayList<>();

        for (Workplace workplace : workplaces) {
            WorkplaceResponseDto dto = new WorkplaceResponseDto(workplace);
            dtos.add(dto);
        }
        return dtos;
    }
}
