package com.studylink.account;

import com.studylink.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/signUp")
    public String signUpForm(Model model) {
        model.addAttribute(new SignUpForm());
        return "account/signUp";
    }

    @PostMapping("/signUp")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) {
        if (errors.hasErrors()) {
            return "account/signUp";
        }

        accountService.processNewAccount(signUpForm);
        return "redirect:/";
    }

    @GetMapping("/verify-email-token")
    public String verifyEmailToken(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        String view = "account/verified-email";
        if (account == null) {
            model.addAttribute("error", "wrong.email");
            return "account/verified-email";
        }

        if (!account.getEmailVerificationToken().equals(token)) {
            model.addAttribute("error", "wrong.token");
            return "account/verified-email";
        }

        account.setEmailVerified(true);
        account.setCreatedAt(LocalDateTime.now());
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("username", account.getUsername());
        return view;
    }

}
