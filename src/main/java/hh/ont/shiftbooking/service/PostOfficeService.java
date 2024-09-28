package hh.ont.shiftbooking.service;

import org.springframework.stereotype.Service;

import hh.ont.shiftbooking.exception.DatabaseException;
import hh.ont.shiftbooking.model.PostOffice;
import hh.ont.shiftbooking.repository.PostOfficeRepository;
import jakarta.transaction.Transactional;

@Service
public class PostOfficeService {
    
    private final PostOfficeRepository repository;

    public PostOfficeService (PostOfficeRepository repository) {
        this.repository = repository;
    }

    /**
     * Tarkastaa onko postitoimipaikan tiedot tietokannassa:
     * <p>
     * true: palautta olemassa olevan, false: tallentaa uuden postitoimipaikan tiedot
     * @param po PostOffice-objekti
     * @return Postitoimipaikan tiedot (PostOffice) 
     * @throws Exception
     */
    @Transactional
    public PostOffice getPostOfficeDetails(PostOffice po) throws Exception {

        try {
            PostOffice postOffice = repository.findByPostalCode(po.getPostalCode());
            return (postOffice != null) ? postOffice : repository.save(po);
        } catch (Exception e) {
            throw new DatabaseException("Tallennus epäonnistui.");
        }
    }
}
