package hh.ont.shiftbooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hh.ont.shiftbooking.dto.WorkplaceResponseDto;
import hh.ont.shiftbooking.exception.DatabaseException;
import hh.ont.shiftbooking.model.PostOffice;
import hh.ont.shiftbooking.model.Workplace;
import hh.ont.shiftbooking.repository.WorkplaceRepository;

@Service
public class WorkplaceService {
    
    @Autowired
    private WorkplaceRepository workRepository;

    @Autowired
    private PostOfficeService postService;

    public WorkplaceResponseDto saveWorkplace(Workplace workplace) throws Exception {

        try {
            // haetaan postitoimipaikan tiedot
            PostOffice postOffice = postService.getPostOfficeDetails(workplace.getZip());
            workplace.setZip(postOffice);
            // TODO: lisää contactPerson
            Workplace saved = workRepository.save(workplace);
            return new WorkplaceResponseDto(saved);
        } catch (IllegalArgumentException e) {
            throw new DatabaseException("Tallennus epäonnistui.");
        }
    }

}
