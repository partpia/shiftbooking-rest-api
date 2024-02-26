package hh.ont.shiftbooking.repository;

import org.springframework.data.repository.CrudRepository;

import hh.ont.shiftbooking.model.Shift;

public interface ShiftRepository extends CrudRepository<Shift, Long> {
    
}
