package hh.ont.shiftbooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hh.ont.shiftbooking.dto.WorkplaceResponseDto;
import hh.ont.shiftbooking.model.Workplace;
import hh.ont.shiftbooking.service.WorkplaceService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("workplaces")
public class WorkplaceController {

    @Autowired
    WorkplaceService workService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createWorkplace(@Valid @RequestBody Workplace workplace) throws Exception {

        try {
            WorkplaceResponseDto created = workService.saveWorkplace(workplace);
            return new ResponseEntity<>(created,
                HttpStatus.CREATED);
        } catch (MethodArgumentNotValidException e) {
            return new ResponseEntity<>("Työpaikan tietojen tallennus epäonnistui.",
                HttpStatus.BAD_REQUEST);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWorkplace(@PathVariable Long id) throws Exception {

        // TODO: poisto-oikeus

        boolean deleted = workService.deleteWorkplace(id);

        return deleted ? new ResponseEntity<>(
            "Työpaikan tiedot poistettu.",
            HttpStatus.OK) :
            new ResponseEntity<>(
            "Työpaikan tietojen poisto epäonnistui.",
            HttpStatus.BAD_REQUEST);
    }
}
