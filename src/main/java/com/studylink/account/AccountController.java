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
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    //
    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/signUp")
    public String signUp(Model model) {
        model.addAttribute(new SignUpForm());
        return "account/signUp";
    }

    @PostMapping("/signUp")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) {
        if (errors.hasErrors()) {
            return "account/signUp";
        }

        signUpFormValidator.validate(signUpForm, errors);
        if (errors.hasErrors()) {
            return "account/signUp";
        }

        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .username(signUpForm.getUsername())
                .password(signUpForm.getPassword())  // TODO: Will be encoded
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build();
        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("Registration verified");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        javaMailSender.send(mailMessage);

        return "redirect:/";
    }

}
