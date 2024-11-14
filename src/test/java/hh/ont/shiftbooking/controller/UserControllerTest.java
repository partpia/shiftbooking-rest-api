package hh.ont.shiftbooking.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import hh.ont.shiftbooking.exception.DatabaseException;
import hh.ont.shiftbooking.exception.PasswordMatchException;
import hh.ont.shiftbooking.exception.UsernameExistsException;
import hh.ont.shiftbooking.model.User;
import hh.ont.shiftbooking.service.ShiftService;
import hh.ont.shiftbooking.service.UserService;
import hh.ont.shiftbooking.service.WorkplaceService;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userServiceMock;

    @MockBean
    private WorkplaceService workServiceMock;

    @MockBean
    private ShiftService shiftServiceMock;

    private String JSON_STRING = "{\"firstName\":\"Testietunimi\",\"lastName\":\"Testisukunimi\",\"email\":\"testiposti@testiposti.fi\",\"tel\":\"Testipuh\",\"username\":\"Testitunnus24\",\"password\":\"TESt/i24\",\"passwordCheck\":\"TESt/i24\",\"role\":\"EMPLOYEE\"}";

    @Autowired
    private ObjectMapper mapper;

    @Disabled
    @Test
    @DisplayName("Testaa tallennuksen rajapintaa, tallennus onnistuu")
    void createNewUserReturnsCreatedTest() throws Exception {

        User created = getUserEntity();

        Mockito.when(userServiceMock.saveNewUser(Mockito.any())).thenReturn(created);

        MvcResult result= mvc.perform(post("/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JSON_STRING)).andReturn();
        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        assertEquals("1", result.getResponse().getHeader("Location"));
    }

    @Disabled
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
    
    @Disabled
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

    @Disabled
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

    @Disabled
    @Test
    @DisplayName("Testaa haun rajapintaa, käyttäjätietoja ei löydy")
    void getUserDetailsNotFoundThrowsException() throws Exception {

        Mockito.when(userServiceMock.getAccountDetails(Mockito.any())).thenThrow(new DatabaseException("Tietojen haku epäonnistui."));

        mvc.perform(get("/accounts/1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Tietojen haku epäonnistui."));
    }

    @Disabled
    @Test
    @DisplayName("Testaa käyttäjätilin poiston rajapintaa, poisto onnistuu")
    void deleteAccountReturnsOkTest() throws Exception {
        
        Mockito.when(userServiceMock.deleteAccount(Mockito.anyLong())).thenReturn(true);

        mvc.perform(delete("/accounts/1"))
            .andExpectAll(
                status().isOk(),
                content().string("Käyttätili poistettu."));
    }

    @Disabled
    @Test
    @DisplayName("Testaa käyttäjätilin poiston rajapintaa, poisto epäonnistuu")
    void deleteAccountReturnsBadRequestTest() throws Exception {
        
        Mockito.when(userServiceMock.deleteAccount(Mockito.anyLong())).thenReturn(false);

        mvc.perform(delete("/accounts/1"))
            .andExpectAll(
                status().isBadRequest(),
                content().string("Käyttäjätilin poisto epäonnistui."));
    }

    private User getUserEntity() throws Exception {

        File file = new File("src\\test\\java\\hh\\ont\\shiftbooking\\resources\\UserEntityTest.json");
        return mapper.readValue(file, new TypeReference<User>(){});
    }
}
