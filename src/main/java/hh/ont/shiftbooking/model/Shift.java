package hh.ont.shiftbooking.model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import hh.ont.shiftbooking.enums.ShiftStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "SHIFT")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="SHIFT_ID", nullable=false, updatable = false)
    private Long shiftId;
    
    @FutureOrPresent
    @DateTimeFormat
    @Column(name="SHIFT_ONSET", nullable=false)
    private LocalDateTime startDateTime;

    @FutureOrPresent
    @DateTimeFormat
    @Column(name="SHIFT_OFFSET", nullable=false)
    private LocalDateTime endDateTime;

    @NotEmpty
    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="STATUS", nullable=false)
    private ShiftStatus status;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = true)
    private User employee;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "WORKPLACE_ID")
    private Workplace location;

    public Shift(LocalDateTime startDateTime, LocalDateTime endDateTime, String description,
            Workplace location) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.description = description;
        this.location = location;
    }
    
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

    public Shift(Long shiftId, LocalDateTime startDateTime, LocalDateTime endDateTime, String description,
            ShiftStatus status, User employee, Workplace location) {
        this.shiftId = shiftId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.description = description;
        this.status = status;
        this.employee = employee;
        this.location = location;
    }
}
