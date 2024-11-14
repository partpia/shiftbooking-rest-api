package hh.ont.shiftbooking.controller;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import hh.ont.shiftbooking.enums.ShiftStatus;
import hh.ont.shiftbooking.model.PostOffice;
import hh.ont.shiftbooking.model.Shift;
import hh.ont.shiftbooking.model.Workplace;
import hh.ont.shiftbooking.service.ShiftService;

@WebMvcTest(ShiftController.class)
@ActiveProfiles("test")
public class ShiftControllerTest {
    
    @MockBean
    private ShiftService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Disabled
    @Test
    @DisplayName("Testaa työvuoron tallennuksen rajapintaa, tallennus onnistuu.")
    void saveShiftReturnsCreatedTest() throws Exception {

        Shift shift = createShift();

        Mockito.when(service.saveShift(any(Shift.class))).thenReturn(shift);

        mockMvc.perform(post("/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shift)))
                .andExpect(status().isCreated());
    }

    @Disabled
    @Test
    @DisplayName("Testaa työvuoron tallennuksen rajapintaa, työvuoron alku menneisyydessä, tulee poikkeus.")
    void saveShiftStartTimeNotFutureOrPresentReturnsBadRequestTest() throws Exception {

        Shift shift = createShift();
        shift.setStartDateTime(LocalDateTime.of(2004, 8, 14, 06, 0));

        mockMvc.perform(post("/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shift)))
                .andExpectAll(
                    result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException),
                    status().isBadRequest(),
                    jsonPath("$.message").value("Virheellinen pyyntö."));
    }

    @Disabled
    @Test
    @DisplayName("Testaa työvuoron tallennuksen rajapintaa, työvuoron loppu menneisyydessä, tulee poikkeus.")
    void saveShiftEndTimeNotFutureOrPresentReturnsBadRequestTest() throws Exception {

        Shift shift = createShift();
        shift.setEndDateTime(LocalDateTime.of(2004, 8, 14, 06, 0));

        mockMvc.perform(post("/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shift)))
                .andExpectAll(
                    result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException),
                    status().isBadRequest(),
                    jsonPath("$.message").value("Virheellinen pyyntö."));
    }

    @Disabled
    @Test
    @DisplayName("Testaa työvuoron varaamisen rajapintaa, varaus onnistuu")
    void bookShiftReturnsOkTest() throws Exception {

        Mockito.when(service.bookShift(any(), any())).thenReturn(true);

        mockMvc.perform(put("/shifts/1/bookings?employee=1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isOk(),
                content().string("Vuoron varaus onnistui."));   
    }

    @Disabled
    @Test
    @DisplayName("Testaa työvuoron varaamisen rajapintaa, varaus epäonnistuu")
    void bookShiftReturnsInternalServerErrorTest() throws Exception {

        Mockito.when(service.bookShift(any(), any())).thenReturn(false);

        mockMvc.perform(put("/shifts/1/bookings?employee=1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isInternalServerError(),
                content().string("Vuoron varaus epäonnistui."));   
    }

    @Disabled
    @Test
    @DisplayName("Testaa työvuoron perumisen rajapintaa, peruminen onnistuu")
    void cancelShiftReturnsOkTest() throws Exception {

        Mockito.when(service.cancelShift(any())).thenReturn(true);

        mockMvc.perform(put("/shifts/1/cancellations")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isOk(),
                content().string("Vuoro peruttu."));   
    }

    @Disabled
    @Test
    @DisplayName("Testaa työvuoron perumisen rajapintaa, peruminen epäonnistuu")
    void cancelShiftReturnsBadRequestTest() throws Exception {

        Mockito.when(service.cancelShift(any())).thenReturn(false);

        mockMvc.perform(put("/shifts/1/cancellations")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isBadRequest(),
                content().string("Vuoroa ei voi perua, koska vuoron alkuun on alle kolme vuorokautta. Ota yhteyttä työnantajaan."));
    }

    @Disabled
    @Test
    @DisplayName("Testaa työvuoron tietojen muokkaamisen rajapintaa, muokkaaminen onnistuu")
    void updateShiftReturnsOkTest() throws Exception {

        Shift shift = createShift();

        Mockito.when(service.updateShiftDetails(any(Shift.class))).thenReturn(true);

        mockMvc.perform(put("/shifts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(shift)))
            .andExpectAll(
                status().isOk(),
                content().string("Vuoro päivitetty."));
    }

    @Disabled
    @Test
    @DisplayName("Testaa työvuoron tietojen muokkaamisen rajapintaa, muokkaaminen epäonnistuu")
    void updateShiftReturnsBadRequestTest() throws Exception {

        Shift shift = createShift();

        Mockito.when(service.updateShiftDetails(any(Shift.class))).thenReturn(false);

        mockMvc.perform(put("/shifts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(shift)))
            .andExpectAll(
                status().isBadRequest(),
                content().string("Varattua vuoroa ei voi muokata."));
    }

    @Disabled
    @Test
    @DisplayName("Testaa työvuoron poistamisen rajapintaa, poistaminen onnistuu")
    void deleteShiftReturnsOkTest() throws Exception {

        Mockito.when(service.deleteShift(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/shifts/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isOk(),
                content().string("Vuoro poistettu."));
    }

    @Disabled
    @Test
    @DisplayName("Testaa työvuoron poistamisen rajapintaa, poistaminen epäonnistuu")
    void deleteShiftReturnsBadRequestTest() throws Exception {

        Mockito.when(service.deleteShift(anyLong())).thenReturn(false);

        mockMvc.perform(delete("/shifts/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isBadRequest(),
                content().string("Varattua vuoroa ei voi poistaa."));
    }

    private Shift createShift() {

        PostOffice postOffice = new PostOffice("00420", "Helsinki");
        Workplace workplace = new Workplace("Lähikauppa", "Kauppakatu 2", "12121212", postOffice);
    	LocalDateTime start1 = LocalDateTime.of(2034, 8, 14, 06, 0);
    	LocalDateTime start2 = LocalDateTime.of(2034, 8, 14, 12, 0);

        Shift shift = new Shift(start1, start2, "Hyllyttäjä", ShiftStatus.BOOKABLE, workplace);

        return shift;
    }
}
