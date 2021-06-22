package com.studylink.account;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
//@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Autowired
    public SignUpFormValidator(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(SignUpForm.class);
    }

    // Error handling depending on the situations, such as wrong email/username input
    @Override
    public void validate(Object object, Errors errors) {
        SignUpForm signUpForm = (SignUpForm)object;
        if (accountRepository.existsByEmail(signUpForm.getEmail())) {
            errors.rejectValue("email", "invalid.email", new Object[]{signUpForm.getEmail()}, "Duplicated email.");
        }

        if (accountRepository.existsByUsername(signUpForm.getUsername())) {
            errors.rejectValue("username", "invalid.username", new Object[]{signUpForm.getEmail()}, "Duplicated username.");
        }
    }
}