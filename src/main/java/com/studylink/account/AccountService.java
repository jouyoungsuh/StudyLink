package com.studylink.account;

import com.studylink.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    // Saves the new account, and generate corresponding verification token, and send it to the user by email.
    @Transactional
    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailVerificationToken();
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    // Method that saves the information of new user account
    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .username(signUpForm.getUsername())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build();
        Account newAccount = accountRepository.save(account);
        return newAccount;
    }

    // Method that sends sign up confirmation email. TODO: Yet done only locally.
    public void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setTo(newAccount.getEmail());
        smm.setSubject("Registration verified");
        smm.setText("/verify-email-token?token=" + newAccount.getEmailVerificationToken() +
                "&email=" + newAccount.getEmail());
        javaMailSender.send(smm);
    }

    // The principal: 'currently logged user' is the first parameter of UPAT
    // Allocated UserAccount (account below is 'account' defined in UserAccount.java) as principal
    public void login(Account account) {
        UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(upat);
    }
}
