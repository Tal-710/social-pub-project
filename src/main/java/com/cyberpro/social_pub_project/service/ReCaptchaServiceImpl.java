package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.dto.ReCaptchaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class ReCaptchaServiceImpl implements ReCaptchaService {

    private static final Logger logger = LoggerFactory.getLogger(ReCaptchaServiceImpl.class);

    @Value("${google.recaptcha.secret}")
    private String recaptchaSecret;

    private static final String GOOGLE_RECAPTCHA_VERIFY_URL =
            "https://www.google.com/recaptcha/api/siteverify";

    private final RestTemplate restTemplate;

    public ReCaptchaServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public boolean validateCaptcha(String captchaResponse) {
        try {
            String params = String.format("?secret=%s&response=%s",
                    recaptchaSecret, captchaResponse);

            ReCaptchaResponse response = restTemplate.postForObject(
                    GOOGLE_RECAPTCHA_VERIFY_URL + params,
                    null,
                    ReCaptchaResponse.class);

            return response != null && response.isSuccess();

        } catch (Exception e) {
            logger.error("Error validating reCAPTCHA: ", e);
            return false;
        }
    }
}
