package hh.ont.shiftbooking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import hh.ont.shiftbooking.model.PostOffice;
import hh.ont.shiftbooking.model.Workplace;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WorkplaceResponseDto {

    private String title;
    private String address;
    private String tel;
    @JsonIgnoreProperties("postId")
    private PostOffice zip;

    public WorkplaceResponseDto(Workplace workplace) {
        this.title = workplace.getTitle();
        this.address = workplace.getAddress();
        this.tel = workplace.getTel();
        this.zip = workplace.getZip();
    }
}
