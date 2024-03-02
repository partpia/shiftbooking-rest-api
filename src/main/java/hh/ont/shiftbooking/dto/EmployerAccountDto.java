package hh.ont.shiftbooking.dto;

import java.util.ArrayList;
import java.util.List;

import hh.ont.shiftbooking.model.User;
import hh.ont.shiftbooking.model.Workplace;
import lombok.Data;

@Data
public class EmployerAccountDto {

    private String firstName;
    private String lastName;
    private String email;
    private String tel;
    private String username;
    private List<WorkplaceResponseDto> myWorkplaces;

    public EmployerAccountDto(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.tel = user.getTel();
        this.username = user.getUsername();
        this.myWorkplaces = getWorkplacetDtos(user.getMyWorkplaces());
    }

    private List<WorkplaceResponseDto> getWorkplacetDtos(List<Workplace> places) {
        List<WorkplaceResponseDto> dtos = new ArrayList<>();
        if (!places.isEmpty()) {
            for (Workplace place : places) {
                dtos.add(new WorkplaceResponseDto(place));
            }
        }
        return dtos;
    }
}
