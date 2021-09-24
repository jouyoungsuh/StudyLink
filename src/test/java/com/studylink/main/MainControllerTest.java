package com.studylink.main;

import com.studylink.account.AccountRepository;
import com.studylink.account.AccountService;
import com.studylink.account.SignUpForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setUsername("yourUsername");
        signUpForm.setEmail("yourEmail@email.com");
        signUpForm.setPassword("password12345");
        accountService.processNewAccount(signUpForm);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @DisplayName("Successful case - login through email")
    @Test
    void login_with_email() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "yourEmail@email.com")
                .param("password", "password12345")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("yourUsername"));
    }

    @DisplayName("Sucessful case - login through username")
    @Test
    void login_with_nickname() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "yourUsername")
                .param("password", "password12345")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("yourUsername"));
    }

    @DisplayName("Failure case - login through username")
    @Test
    void login_fail() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "46797123")
                .param("password", "848468")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @WithMockUser
    @DisplayName("Sucessful case - log out")
    @Test
    void logout() throws Exception {
        mockMvc.perform(post("/logout")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }
}