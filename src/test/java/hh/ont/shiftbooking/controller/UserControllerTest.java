package hh.ont.shiftbooking.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import hh.ont.shiftbooking.exception.DatabaseException;
import hh.ont.shiftbooking.exception.PasswordMatchException;
import hh.ont.shiftbooking.exception.UsernameExistsException;
import hh.ont.shiftbooking.service.UserDetailService;


@WebMvcTest(UserController.class)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserDetailService userServiceMock;

    private String JSON_STRING = "{\"firstName\":\"Testietunimi\",\"lastName\":\"Testisukunimi\",\"email\":\"testiposti@testiposti.fi\",\"tel\":\"Testipuh\",\"username\":\"Testitunnus24\",\"password\":\"TESt/i24\",\"passwordCheck\":\"TESt/i24\",\"role\":\"EMPLOYEE\"}";

    @Test
    @DisplayName("Testaa tallennuksen rajapintaa, tallennus onnistuu")
    void createNewUserReturnsCreatedTest() throws Exception {

        Mockito.when(userServiceMock.saveNewUser(Mockito.any())).thenReturn(true);

        mvc.perform(post("/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSON_STRING)).andExpectAll(
                status().isCreated(),
                content().string("Tili luotu käyttäjätunnuksella Testitunnus24"));
    }

    @Test
    @DisplayName("Testaa tallennuksen rajapintaa, tallennus epäonnistuu")
    void createNewUserReturnsBadRequestTest() throws Exception {

        Mockito.when(userServiceMock.saveNewUser(Mockito.any())).thenReturn(false);

        mvc.perform(post("/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSON_STRING)).andExpectAll(
                status().isInternalServerError(),
                content().string("Tilin luonti epäonnistui"));
    }

    @Test
    @DisplayName("Testaa tallennuksen rajapintaa, salasanat eivät täsmää")
    void passwordsDoNotMatchBadRequestTest() throws Exception {

        String json = "{\"firstName\":\"Testietunimi\",\"lastName\":\"Testisukunimi\",\"email\":\"testiposti@testiposti.fi\",\"tel\":\"Testipuh\",\"username\":\"Testitunnus24\",\"password\":\"TESt/i24\",\"passwordCheck\":\"TESt/i25\",\"role\":\"EMPLOYEE\"}";

        mvc.perform(post("/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)).andExpectAll(
                result -> assertTrue(result.getResolvedException() instanceof PasswordMatchException),
                status().isBadRequest(),
                jsonPath("$.message").value("Salasanat eivät täsmää"));
    }
    
    @Test
    @DisplayName("Testaa tallennuksen rajapintaa, käyttäjätunnus varattu")
    void usernameTakenThrowsExceptionTest() throws Exception {
        
        Mockito.when(userServiceMock.saveNewUser(Mockito.any())).thenThrow(new UsernameExistsException("Valitse toinen käyttäjätunnus"));

        mvc.perform(post("/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSON_STRING)).andExpectAll(
                result -> assertTrue(result.getResolvedException() instanceof UsernameExistsException),
                status().isBadRequest(),
                jsonPath("$.message").value("Valitse toinen käyttäjätunnus"));
    }

    @Test
    @DisplayName("Testaa tallennuksen rajapintaa, salasana liian lyhyt")
    void passwordTooShortBadRequest() throws Exception {

        String json = "{\"firstName\":\"Testietunimi\",\"lastName\":\"Testisukunimi\",\"email\":\"testiposti@testiposti.fi\",\"tel\":\"Testipuh\",\"username\":\"Testitunnus24\",\"password\":\"Lyhyt\",\"passwordCheck\":\"Lyhyt\",\"role\":\"EMPLOYEE\"}";

        mvc.perform(post("/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)).andExpectAll(
                result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException),
                status().isBadRequest(),
                jsonPath("$.message").value("Virheellinen pyyntö."));
    }

    @Test
    @DisplayName("Testaa haun rajapintaa, käyttäjätietoja ei löydy")
    void getUserDetailsNotFoundThrowsException() throws Exception {

        Mockito.when(userServiceMock.getAccountDetails(Mockito.any())).thenThrow(new DatabaseException("Tietojen haku epäonnistui."));

        mvc.perform(get("/accounts/1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Tietojen haku epäonnistui."));
    }
}
