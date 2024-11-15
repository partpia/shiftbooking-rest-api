package hh.ont.shiftbooking.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.File;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import hh.ont.shiftbooking.dto.WorkplaceResponseDto;
import hh.ont.shiftbooking.model.Workplace;
import hh.ont.shiftbooking.service.WorkplaceService;

@WebMvcTest(WorkplaceController.class)
@ActiveProfiles("test")
public class WorkplaceControllerTest {
    
    @MockBean
    private WorkplaceService serviceMock;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Disabled
    @Test
    @DisplayName("Testaa työpaikan tallentamisen rajapintaa, tallentaminen onnistuu")
    void saveWorkplaceReturnsCreatedTest() throws Exception {

        Workplace w = getWorkplace();
        WorkplaceResponseDto dto = getWorkplaceDto();

        Mockito.when(serviceMock.saveWorkplace(any(Workplace.class))).thenReturn(dto);

        mockMvc.perform(post("/workplaces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(w)))
                .andExpect(status().isCreated());
    }

    @Disabled
    @Test
    @DisplayName("Testaa työpaikan tallentamisen rajapintaa, pakollinen tieto null, tulee poikkeus")
    void saveWorkplaceWithNullValueReturnsBadRequestTest() throws Exception {

        Workplace w = getWorkplace();
        w.setTitle(null);

        mockMvc.perform(post("/workplaces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(w)))
                .andExpectAll(
                    result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException),
                    status().isBadRequest(),
                    jsonPath("$.message").value("Virheellinen pyyntö."));
    }

    @Disabled
    @Test
    @DisplayName("Testaa työpaikan tietojen päivittämisen rajapintaa, päivitys onnistuu")
    void updateWorkplaceReturnsOkTest() throws Exception {

        Workplace w = getWorkplace();

        Mockito.when(serviceMock.updateWorkplaceDetails(any(Workplace.class))).thenReturn(true);

        mockMvc.perform(put("/workplaces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(w)))
                .andExpectAll(
                    status().isOk(),
                    content().string("Työpaikan tiedot päivitetty."));
    }

    @Disabled
    @Test
    @DisplayName("Testaa työpaikan tietojen päivittämisen rajapintaa, päivitys epäonnistuu")
    void updateWorkplaceReturnsBadRequestTest() throws Exception {

        Workplace w = getWorkplace();

        Mockito.when(serviceMock.updateWorkplaceDetails(any(Workplace.class))).thenReturn(false);

        mockMvc.perform(put("/workplaces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(w)))
                .andExpectAll(
                    status().isBadRequest(),
                    content().string("Työpaikan tietojen päivitys epäonnistui."));
    }

    @Disabled
    @Test
    @DisplayName("Testaa työpaikan päivittämisen rajapintaa, pakollinen tieto null, tulee poikkeus")
    void updateWorkplaceWithNullValueReturnsBadRequestTest() throws Exception {

        Workplace w = getWorkplace();
        w.setTitle(null);

        mockMvc.perform(put("/workplaces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(w)))
                .andExpectAll(
                    result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException),
                    status().isBadRequest(),
                    jsonPath("$.message").value("Virheellinen pyyntö."));
    }

    @Disabled
    @Test
    @DisplayName("Testaa työpaikan poistamisen rajapintaa, poisto onnistuu")
    void deleteWorkplaceReturnsOkTest() throws Exception {

        Mockito.when(serviceMock.deleteWorkplace(Mockito.anyLong())).thenReturn(true);

        mockMvc.perform(delete("/workplaces/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isOk(),
                content().string("Työpaikan tiedot poistettu."));
    }

    @Disabled
    @Test
    @DisplayName("Testaa työpaikan poistamisen rajapintaa, poisto epäonnistuu")
    void deleteWorkplaceReturnsBadRequestTest() throws Exception {

        Mockito.when(serviceMock.deleteWorkplace(Mockito.anyLong())).thenReturn(false);

        mockMvc.perform(delete("/workplaces/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isBadRequest(),
                content().string("Työpaikan tietojen poisto epäonnistui."));
    }

    private Workplace getWorkplace() throws Exception {

        File file = new File("src\\test\\java\\hh\\ont\\shiftbooking\\resources\\CreateWorkplaceTest.json");
        return mapper.readValue(file, new TypeReference<Workplace>(){});
    }

    private WorkplaceResponseDto getWorkplaceDto() throws Exception {

        File file = new File("src\\test\\java\\hh\\ont\\shiftbooking\\resources\\WorkplaceDtoTest.json");
        return mapper.readValue(file, new TypeReference<WorkplaceResponseDto>(){});
    }
}
