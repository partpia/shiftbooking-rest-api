package hh.ont.shiftbooking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hh.ont.shiftbooking.dto.ShiftResponseDto;
import hh.ont.shiftbooking.model.Shift;
import hh.ont.shiftbooking.service.ShiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    // uuden työvuoron lisääminen
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('EMPLOYER')")
    public ResponseEntity<Object> createShift(@Valid @RequestBody Shift shift) throws Exception {
        Shift created = shiftService.saveShift(shift);
        ShiftResponseDto dto = new ShiftResponseDto(created);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    // varattavissa olevien työvuorojen hakeminen
    @GetMapping()
    public List<ShiftResponseDto> getBookableShifts() throws Exception {
        return shiftService.getAllBookableShifts();
    }

    // työvuoron varaaminen
    @PutMapping("/{id}/bookings")
    @PreAuthorize("hasAuthority('EMPLOYEE') && #employee == authentication.principal.userId")
    public ResponseEntity<String> bookShift(@PathVariable(required = true) Long id, @RequestParam Long employee)
        throws Exception {
        boolean booked = shiftService.bookShift(id, employee);
        
        return booked ? new ResponseEntity<>(
                "Vuoron varaus onnistui.",
                HttpStatus.OK) :
                new ResponseEntity<>(
                "Vuoron varaus epäonnistui.",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // varatun työvuoron peruminen
    @PutMapping("/{id}/cancellations")
    public ResponseEntity<String> cancelShift(@PathVariable Long id) throws Exception {        
        boolean calcelled = shiftService.cancelShift(id);

        return calcelled ? new ResponseEntity<>(
            "Vuoro peruttu.",
            HttpStatus.OK) :
            new ResponseEntity<>(
            "Vuoroa ei voi perua, koska vuoron alkuun on alle kolme vuorokautta. Ota yhteyttä työnantajaan.",
            HttpStatus.BAD_REQUEST);
    }
    
    // työvuoron muokkaaminen
    @PutMapping()
    @PreAuthorize("hasAuthority('EMPLOYER')")
    public ResponseEntity<String> updateShift(@Valid @RequestBody Shift shift) throws Exception {    
        boolean updated = shiftService.updateShiftDetails(shift);

        return updated ? new ResponseEntity<>(
            "Vuoro päivitetty.",
            HttpStatus.OK) :
            new ResponseEntity<>(
            "Varattua vuoroa ei voi muokata.",
            HttpStatus.BAD_REQUEST);
    }

    // työvuoron poistaminen
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYER')")
    public ResponseEntity<String> deleteShift(@PathVariable Long id) throws Exception {
        boolean deleted = shiftService.deleteShift(id);

        return deleted ? new ResponseEntity<>(
            "Vuoro poistettu.",
            HttpStatus.OK) :
            new ResponseEntity<>(
            "Varattua vuoroa ei voi poistaa.",
            HttpStatus.BAD_REQUEST);
    }
}
