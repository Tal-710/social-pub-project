document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('login-form');

    if(form) {
        form.addEventListener('submit', function(event) {
            event.preventDefault();

            const errorContainer = document.getElementById('error-message');
            const captchaError = document.getElementById('captcha-error');

            if(captchaError) {
                captchaError.textContent = '';
            }

            const captchaResponse = grecaptcha.getResponse();
            if (!captchaResponse) {
                if(captchaError) {
                    captchaError.textContent = 'Please complete the CAPTCHA';
                }
                return false;
            }


            form.submit();
        });
    }
});