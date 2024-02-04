package hh.ont.shiftbooking.model;

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
public class Workplace {

    // TODO: puuttuvat tietokantamääritykset
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String address;
    private String tel;
    private PostOffice zip;

    public Workplace(String title, String address, String tel, PostOffice zip) {
        this.title = title;
        this.address = address;
        this.tel = tel;
        this.zip = zip;
    }

}
