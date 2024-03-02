package hh.ont.shiftbooking.dto;

import java.util.ArrayList;
import java.util.List;

import hh.ont.shiftbooking.model.Shift;
import hh.ont.shiftbooking.model.User;
import lombok.Data;

@Data
public class EmployeeAccountDto {

    private String firstName;
    private String lastName;
    private String email;
    private String tel;
    private String username;
    private List<ShiftResponseDto> shifts;

    public EmployeeAccountDto(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.tel = user.getTel();
        this.username = user.getUsername();
        this.shifts = getShiftDtos(user.getShifts());
    }

    private List<ShiftResponseDto> getShiftDtos(List<Shift> shifts) {
        List<ShiftResponseDto> dtos = new ArrayList<>();
        if (!shifts.isEmpty()) {
            for (Shift shift : shifts) {
                dtos.add(new ShiftResponseDto(shift));
            }
        }
        return dtos;
    }
}
