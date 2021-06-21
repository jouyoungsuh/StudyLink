package com.studylink.account;

import com.studylink.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/signUp"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/signUp"))
                .andExpect(model().attributeExists("signUpForm"));
    }

    @Test
    void signUpSubmit_with_correct_input() throws Exception {
        mockMvc.perform(post("/signUp")
                .param("username", "helloWorld")
                .param("email", "helloWorld@email.com")
                .param("password", "789456123")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        Account account = accountRepository.findByEmail("helloWorld@email.com");
        assertNotNull(account);
        assertNotNull(account.getEmailVerificationToken());
        assertNotEquals(account.getPassword(), "789456123");
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/signUp")
                .param("username", "hello")
                .param("email", "thisIsNotEmailFormat")
                .param("password", "111")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/signUp"));
    }



}