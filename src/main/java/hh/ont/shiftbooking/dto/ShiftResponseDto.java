package hh.ont.shiftbooking.dto;

import java.time.LocalDateTime;

import hh.ont.shiftbooking.enums.ShiftStatus;
import hh.ont.shiftbooking.model.Shift;
import lombok.Data;

@Data
public class ShiftResponseDto {

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String description;
    private ShiftStatus status;
    private WorkplaceResponseDto location;

    public ShiftResponseDto(Shift shift) {
        this.startDateTime = shift.getStartDateTime();
        this.endDateTime = shift.getEndDateTime();
        this.description = shift.getDescription();
        this.status = shift.getStatus();
        this.location = new WorkplaceResponseDto(shift.getLocation());
    }
}
