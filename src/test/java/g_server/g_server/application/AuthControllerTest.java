package g_server.g_server.application;

import g_server.g_server.application.query.request.AuthorizationForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    private MockMvc mockMvc;

    @Autowired
    public void setMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void firstAuthControllerTest() throws Exception {
        mockMvc.perform(
                post("/authorization")
                .param("email", "Delamart1683@yandex.ru")
                .param("password", "3Bq0050eQxSx")
                .flashAttr("authorizationForm", new AuthorizationForm()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("fio").value("Лисовой Андрей Анатольевич"))
                .andExpect(jsonPath("userRole").value("student")
        );
    }

    @Test
    void secondAuthControllerTest() throws Exception {
        mockMvc.perform(
                post("/authorization")
                .param("email", "vkgrig49@mail.ru")
                .param("password", "s4VQ5LtVVZ6g")
                .flashAttr("authorizationForm", new AuthorizationForm()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("fio").value("Григорьев Виктор Карлович"))
                .andExpect(jsonPath("userRole").value("scientific_advisor")
        );
    }

    @Test
    void thirdAuthControllerTest() throws Exception {
        mockMvc.perform(
                post("/authorization")
                .param("email", "sgolovin@itstandard.ru2")
                .param("password", "123456")
                .flashAttr("authorizationForm", new AuthorizationForm()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("fio").value("Головин Сергей Анатольевич"))
                .andExpect(jsonPath("userRole").value("head_of_cathedra")
        );
    }

    @Test
    void fourthAuthControllerTest() throws Exception {
        mockMvc.perform(
                post("/authorization")
                .param("email", "Alamart1683@gmail.com")
                .param("password", "77887788")
                .flashAttr("authorizationForm", new AuthorizationForm()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("fio").value("Лисовой Андрей Анатольевич"))
                .andExpect(jsonPath("userRole").value("root")
        );
    }

    @Test
    void fifthAuthControllerTest() throws Exception {
        mockMvc.perform(
                post("/authorization")
                .param("email", "notExistUser")
                .param("password", "notFoundPassword")
                .flashAttr("authorizationForm", new AuthorizationForm()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("accessIssueDate").value(0))
                .andExpect(jsonPath("refreshIssueDate").value(0)
         );
    }
}


