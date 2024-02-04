package hh.ont.shiftbooking.model;

import java.time.LocalDateTime;

import hh.ont.shiftbooking.enums.ShiftStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Shift {

    // TODO: puuttuvat tietokantamääritykset

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String description;
    private ShiftStatus status;
    private User employee;
    private Workplace location;
    
    public Shift(LocalDateTime startDateTime, LocalDateTime endDateTime, String description, ShiftStatus status,
            Workplace location) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.description = description;
        this.status = status;
        this.location = location;
    }

    public Shift(LocalDateTime startDateTime, LocalDateTime endDateTime, String description, ShiftStatus status,
            User employee, Workplace location) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.description = description;
        this.status = status;
        this.employee = employee;
        this.location = location;
    }

}
