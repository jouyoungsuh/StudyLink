package com.studylink.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configurable
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // If this configuration is not implemented, then no one could enter the webpage except for the web master.
        // Allows none-members to enter the webpage and generate account.
        // But should not allow none-members to enter other webpages
        http.authorizeRequests()
                .mvcMatchers("/", "/login", "/signUp", "/verify-email-token",
                        "/email-login", "/verify-email-login", "/login-link").permitAll()
                .mvcMatchers(HttpMethod.GET, "/profiles/*").permitAll()
                .anyRequest().authenticated();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .mvcMatchers("/node_modules/**");
    }

}