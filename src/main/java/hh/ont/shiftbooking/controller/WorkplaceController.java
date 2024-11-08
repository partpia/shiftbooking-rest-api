package hh.ont.shiftbooking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hh.ont.shiftbooking.dto.WorkplaceResponseDto;
import hh.ont.shiftbooking.model.Workplace;
import hh.ont.shiftbooking.service.WorkplaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("workplaces")
@RequiredArgsConstructor
public class WorkplaceController {

    private final WorkplaceService workService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostAuthorize("hasAuthority('EMPLOYER')")
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

    @GetMapping()
    public List<WorkplaceResponseDto> getWorkplaces() throws Exception {
        return workService.getAllWorkplaces();
    }

    @PutMapping()
    @PreAuthorize("hasAuthority('EMPLOYER') && #workplace.contactPerson.userId == authentication.principal.userId")
    public ResponseEntity<String> updateWorkplace(@Valid @RequestBody Workplace workplace) throws Exception {
        boolean updated = workService.updateWorkplaceDetails(workplace);

        return updated ? new ResponseEntity<>(
            "Työpaikan tiedot päivitetty.",
            HttpStatus.OK) :
            new ResponseEntity<>(
            "Työpaikan tietojen päivitys epäonnistui.",
            HttpStatus.BAD_REQUEST);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYER')")
    public ResponseEntity<String> deleteWorkplace(@PathVariable Long id) throws Exception {
        boolean deleted = workService.deleteWorkplace(id);

        return deleted ? new ResponseEntity<>(
            "Työpaikan tiedot poistettu.",
            HttpStatus.OK) :
            new ResponseEntity<>(
            "Työpaikan tietojen poisto epäonnistui.",
            HttpStatus.BAD_REQUEST);
    }
}
