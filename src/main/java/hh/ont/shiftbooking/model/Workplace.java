package hh.ont.shiftbooking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "WORKPLACE")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Workplace {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="WORKPLACE_ID", nullable=false, updatable = false)
    private Long workplaceId;

    @Column(name="TITLE", nullable=false)
    @NotEmpty
    private String title;

    @Column(name="ADDRESS", nullable=false)
    @NotEmpty
    private String address;

    @Column(name="PHONE_NUMBER", nullable=false)
    @NotEmpty
    private String tel;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "POST_ID")
    private PostOffice zip;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User contactPerson;

    public Workplace(String title, String address, String tel, PostOffice zip) {
        this.title = title;
        this.address = address;
        this.tel = tel;
        this.zip = zip;
    }

    public Workplace(@NotEmpty String title, @NotEmpty String address, @NotEmpty String tel, @NotNull PostOffice zip,
            User contactPerson) {
        this.title = title;
        this.address = address;
        this.tel = tel;
        this.zip = zip;
        this.contactPerson = contactPerson;
    }
}
