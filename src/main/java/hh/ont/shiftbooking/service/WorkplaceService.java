package hh.ont.shiftbooking.service;

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

    // tallentaa uuden työpaikan tiedot tietokantaan, palauttaa dto:n
    public WorkplaceResponseDto saveWorkplace(Workplace workplace) throws Exception {

        try {
            // haetaan postitoimipaikan tiedot
            PostOffice postOffice = postService.getPostOfficeDetails(workplace.getZip());
            workplace.setZip(postOffice);
            // haetaan työnantajan tiedot
            User employer = userRepository.findById(workplace.getContactPerson().getUserId()).get();
            workplace.setContactPerson(employer);
            Workplace saved = workRepository.save(workplace);
            return new WorkplaceResponseDto(saved);
        } catch (IllegalArgumentException e) {
            throw new DatabaseException("Tallennus epäonnistui.");
        }
    }

}
