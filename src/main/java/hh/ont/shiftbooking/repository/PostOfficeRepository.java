package hh.ont.shiftbooking.repository;

import org.springframework.data.repository.CrudRepository;

import hh.ont.shiftbooking.model.PostOffice;

public interface PostOfficeRepository extends CrudRepository<PostOffice, Long> {

    PostOffice findByPostalCode(String postalCode);
    
}
