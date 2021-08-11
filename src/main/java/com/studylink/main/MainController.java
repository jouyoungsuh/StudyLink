package com.studylink.main;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.studylink.account.CurrentUser;
import com.studylink.domain.Account;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {
        // Depends on the type of the current user.
        // The status of the current user (if it is null or not) is defined in CurrentUser.java. If anonymousUser, returns null and else return account
        if (account != null) {
            model.addAttribute(account);
        }

        return "index";
    }

}
