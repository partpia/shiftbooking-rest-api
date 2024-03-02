package hh.ont.shiftbooking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hh.ont.shiftbooking.dto.ShiftResponseDto;
import hh.ont.shiftbooking.model.Shift;
import hh.ont.shiftbooking.service.ShiftService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("shifts")
public class ShiftController {

    @Autowired
    ShiftService shiftService;
    
    // uuden työvuoron lisääminen
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
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
}
