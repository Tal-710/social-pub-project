package com.cyberpro.social_pub_project.security;

import com.cyberpro.social_pub_project.service.ReCaptchaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class CaptchaAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ReCaptchaService reCaptchaService;

    public CaptchaAuthenticationFilter(ReCaptchaService reCaptchaService) {
        this.reCaptchaService = reCaptchaService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {

        String captchaResponse = request.getParameter("g-recaptcha-response");
        if (!reCaptchaService.validateCaptcha(captchaResponse)) {
            throw new AuthenticationServiceException("Invalid Captcha");
        }

        return super.attemptAuthentication(request, response);
    }
}
