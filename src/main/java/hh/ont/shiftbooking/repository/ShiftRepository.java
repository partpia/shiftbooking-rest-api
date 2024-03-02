package hh.ont.shiftbooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import hh.ont.shiftbooking.enums.ShiftStatus;
import hh.ont.shiftbooking.model.Shift;

public interface ShiftRepository extends CrudRepository<Shift, Long> {

    @Query("select s from Shift s where s.status = ?1")
    List<Shift> findByStatus(ShiftStatus status);
    
}
