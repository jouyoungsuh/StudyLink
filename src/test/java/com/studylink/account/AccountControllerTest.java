package com.studylink.account;

import com.studylink.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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

    @DisplayName("Check registration page")
    @Test
    void registrationForm() throws Exception {
        // The signUp page should come up (get) as intended, with attributes
        mockMvc.perform(get("/signUp"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/signUp"))
                .andExpect(model().attributeExists("signUpForm"));
    }

    @DisplayName("Check correct input parameters in sign up form")
    @Test
    void registrationSubmitted_with_correct_input() throws Exception {
        // Check if post functionality with correct input parameters works as intended.
        // check if page redirects as intended.
        // EDIT: Need to allow with csrf
        mockMvc.perform(post("/signUp")
                .param("username", "helloWorld")
                .param("email", "helloWorld@email.com")
                .param("password", "789456123")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        // EDIT: Need to check the verification token is not null.
        Account account = accountRepository.findByEmail("helloWorld@email.com");
        assertNotNull(account);
        assertNotNull(account.getEmailVerificationToken());
        assertNotEquals(account.getPassword(), "789456123");
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }

    @DisplayName("Check incorrect inputs are blocked as intended")
    @Test
    void registrationSubmitted_with_wrong_input() throws Exception {
        // Check if the incorrect input causes block and no redirection
        mockMvc.perform(post("/signUp")
                .param("username", "hello")
                .param("email", "thisIsNotEmailFormat")
                .param("password", "111")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/signUp"));
    }

    @DisplayName("Check email verification token works as intended when given correct input")
    @Test
    void verifyEmailToken_with_correct_input() throws Exception {
        // Account is saved with correct input
        Account account = Account.builder()
                .email("helloWorld@email.com")
                .password("789456123")
                .username("hello")
                .build();
        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailVerificationToken();

        // There should be email, token, and shouldn't have any errors.
        mockMvc.perform(get("/verify-email-token")
                .param("token", newAccount.getEmailVerificationToken())
                .param("email", newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("username"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/verified-email"));
    }

    @DisplayName("Check email verification webpage does not work if there's no input / incorrect token")
    @Test
    void verifyEmailToken_with_wrong_input() throws Exception {
        // There's no email saved, which means there will be no verification token, and therefore cause error
        mockMvc.perform(get("/verify-email-token")
                .param("token", "justMeaningLessString")
                .param("email", "hello@email.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/verified-email"));
    }



}