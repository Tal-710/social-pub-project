package com.cyberpro.social_pub_project.security;

import com.cyberpro.social_pub_project.repository.UserRepository;
import com.cyberpro.social_pub_project.service.ReCaptchaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;


@Configuration
public class SecurityConfig {

    private final ReCaptchaService reCaptchaService;

    public SecurityConfig(ReCaptchaService reCaptchaService) {
        this.reCaptchaService = reCaptchaService;
    }


    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource){
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            CaptchaAuthenticationFilter captchaFilter = new CaptchaAuthenticationFilter(reCaptchaService);
            captchaFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
            captchaFilter.setAuthenticationFailureHandler((request, response, exception) -> {
                String redirectUrl = "/login?error";
                response.sendRedirect(redirectUrl);
            });

            http
                    .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                            .requestMatchers("/register/**").permitAll()
                            .requestMatchers("/home").permitAll()
                            .requestMatchers("/bartender").hasAnyRole("BARTENDER","ADMIN")
                            .requestMatchers("/users/update-profile-picture").permitAll()
                            .requestMatchers("/users/current").permitAll()
                            .requestMatchers("/users/*").hasAnyRole("BARTENDER","ADMIN")
                            .requestMatchers("/error/**").permitAll()
                            .requestMatchers("/admin/****").hasRole("ADMIN")
                            .requestMatchers("/orders/***").hasAnyRole("BARTENDER","ADMIN")
                            .requestMatchers("/products/*").hasAnyRole("BARTENDER","ADMIN")
                            .anyRequest().authenticated()
                    )
                    .formLogin(form -> form
                            .loginPage("/login")
                            .loginProcessingUrl("/authenticateTheUser")
                            .failureUrl("/login?error")
                            .successHandler((request, response, authentication) -> {
                                response.sendRedirect("/confirmation");
                            })
                            .permitAll()
                    )
                    .logout(logout -> logout
                            .logoutSuccessUrl("/home")
                            .permitAll()
                    )
                    .exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"));

            return http.build();
        }
    }



