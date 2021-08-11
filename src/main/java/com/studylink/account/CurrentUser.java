package com.studylink.account;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

// Retention because CurrentUser should be available throughout the runtime
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)

// Being anonymousUser means not getting the authorization defined in SecurityConfig
// 'account' below refers to the account in UserAccount
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
public @interface CurrentUser {
}

