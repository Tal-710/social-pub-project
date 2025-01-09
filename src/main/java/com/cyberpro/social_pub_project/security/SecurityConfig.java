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
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());

        authProvider.setUserDetailsService(username -> {
            return userRepository.findByUsername(username)
                    .map(user -> {
                        System.out.println("Successful login: " + username);
                        // Determine roles based on username
                        String role = username.contains("Bartender") ? "BARTENDER" : "EMPLOYEE";
                        return org.springframework.security.core.userdetails.User.builder()
                                .username(user.getUsername())
                                .password(user.getPassword())
                                .roles(role) // Assign role based on username
                                .build();
                    })
                    .orElseThrow(() -> {
                        System.err.println("Failed login attempt for username: " + username);
                        return new UsernameNotFoundException("User not found: " + username);
                    });
        });

        return authProvider;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/register/**").permitAll()
                        .requestMatchers("/home").permitAll()
                        .requestMatchers("/").hasRole("EMPLOYEE")
                        .requestMatchers("bartender").hasRole("BARTENDER")
                        .requestMatchers("/leaders/**").hasRole("MANAGER")
                        .requestMatchers("/systems/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/showMyLoginPage")
                        .loginProcessingUrl("/authenticateTheUser")
                        .successHandler((request, response, authentication) -> {
                            // Redirect based on roles or other logic
                            response.sendRedirect("/showMyLoginPage");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // Endpoint to trigger logout
                        .logoutSuccessUrl("/showMyLoginPage?logout") // Redirect to login page after logout
                        .invalidateHttpSession(true) // Invalidate session
                        .deleteCookies("JSESSIONID") // Delete session cookie
                        .permitAll()
                )
                .exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"));

        return http.build();
    }

}



