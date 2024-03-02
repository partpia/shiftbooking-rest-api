package hh.ont.shiftbooking.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import hh.ont.shiftbooking.enums.ShiftStatus;
import hh.ont.shiftbooking.model.Shift;
import lombok.Data;

@Data
public class ShiftResponseDto {

    private Long id;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String description;
    private ShiftStatus status;
    @JsonIgnoreProperties("id")
    private WorkplaceResponseDto location;

    public ShiftResponseDto(Shift shift) {
        this.id = shift.getShiftId();
        this.startDateTime = shift.getStartDateTime();
        this.endDateTime = shift.getEndDateTime();
        this.description = shift.getDescription();
        this.status = shift.getStatus();
        this.location = new WorkplaceResponseDto(shift.getLocation());
    }
}
