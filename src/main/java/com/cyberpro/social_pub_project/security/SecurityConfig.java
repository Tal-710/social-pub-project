package com.cyberpro.social_pub_project.security;

import com.cyberpro.social_pub_project.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;


@Configuration
public class SecurityConfig {


    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource){
        return new JdbcUserDetailsManager(dataSource);
    }

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/register/**").permitAll()
                        .requestMatchers("/home").permitAll()
                        .requestMatchers("/api/authorities/**").permitAll()
                        .requestMatchers("/bartender").hasRole("BARTENDER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/showMyLoginPage")
                        .loginProcessingUrl("/authenticateTheUser")
                        .successHandler((request, response, authentication) -> {
                            response.sendRedirect("/confirmation");
                        })
                        .permitAll()
                )
                .logout(logout -> logout.permitAll())
                .exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"));

        return http.build();
    }

}



