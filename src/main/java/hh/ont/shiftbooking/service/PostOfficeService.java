package hh.ont.shiftbooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hh.ont.shiftbooking.exception.DatabaseException;
import hh.ont.shiftbooking.model.PostOffice;
import hh.ont.shiftbooking.repository.PostOfficeRepository;

@Service
public class PostOfficeService {
    
    @Autowired
    private PostOfficeRepository repository;

    public PostOffice getPostOfficeDetails(PostOffice po) throws Exception {

        try {
            PostOffice postOffice = repository.findByPostalCode(po.getPostalCode());
            return (postOffice != null) ? postOffice : repository.save(po);
        } catch (Exception e) {
            throw new DatabaseException("Tallennus epäonnistui.");
        }
    }
}
