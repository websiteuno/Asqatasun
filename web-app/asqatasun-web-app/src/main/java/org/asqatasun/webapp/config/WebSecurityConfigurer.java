/*
 * Asqatasun - Automated webpage assessment
 * Copyright (C) 2008-2016  Asqatasun.org
 *
 * This file is part of Asqatasun.
 *
 * Asqatasun is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact us by mail: asqatasun AT asqatasun DOT org
 */
package org.asqatasun.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by meskoj on 25/05/16.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationProvider authenticationProvider;

//    @Autowired
//    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(authenticationProvider);
//    }

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("bill").password("abc123").roles("USER");
        auth.inMemoryAuthentication().withUser("admin").password("root123").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http
            .authorizeRequests()
            .antMatchers(
                    "/Images/**",
                    "/Css/**",
                    "/External-Css/**",
                    "/External-Css/**",
                    "/Font/**",
                    "/Js/**",
                    "/External-Js/**",
                    "/j_spring_security_check",
                    "/forgotten-password.html",
                    "/AppController/echo.html").permitAll()
            .antMatchers(
                    "/dispatch.html",
                    "/home.html",
                    "/home/*",
                    "/home/**/*").hasAnyRole("ROLE_USER","ROLE_ADMIN")
            .antMatchers(
                    "/admin*",
                    "/admin/*",
                    "/admin/**/*").hasAnyRole("ROLE_ADMIN")
//            .anyRequest().authenticated()
            .and()
            .formLogin()
                .usernameParameter("j_username") /* BY DEFAULT IS username!!! */
                .passwordParameter("j_password") /* BY DEFAULT IS password!!! */
                .loginProcessingUrl("/login")
                .loginPage("/login.html")
                .defaultSuccessUrl("/dispatch.html")
                .failureUrl("/login.html?error=errorOnLogin")
                .permitAll()
                .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.html")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(false)
                .permitAll()
                .and()
            .sessionManagement().maximumSessions(100).expiredUrl("/login.html");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
            .antMatchers("/static/**");
    }

}
