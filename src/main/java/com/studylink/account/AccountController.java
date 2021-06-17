package com.studylink.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/signUp")
    public String signUp(Model model) {
        model.addAttribute(new SignUpForm());
        return "account/signUp";
    }

}
