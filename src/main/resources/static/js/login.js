document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('login-form');

    if(form) {
        form.addEventListener('submit', function(event) {
            event.preventDefault(); // Stop form from submitting immediately

            const errorContainer = document.getElementById('error-message');
            const captchaError = document.getElementById('captcha-error');

            if(captchaError) {
                captchaError.textContent = '';
            }

            // Validate CAPTCHA
            const captchaResponse = grecaptcha.getResponse();
            if (!captchaResponse) {
                if(captchaError) {
                    captchaError.textContent = 'Please complete the CAPTCHA';
                }
                return false; // Stop form submission
            }

            // If CAPTCHA is completed, submit the form
            form.submit();
        });
    }
});