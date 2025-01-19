document.getElementById("register-form").addEventListener("submit", function(event) {
    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const idNumber = document.getElementById("idNumber").value;
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirm-password").value;

    const errorContainer = document.getElementById("error-message");
    const captchaError = document.getElementById("captcha-error");
    errorContainer.textContent = "";
    captchaError.textContent = "";

    // Validate CAPTCHA
    const captchaResponse = grecaptcha.getResponse();
    if (!captchaResponse) {
           captchaError.textContent = "Please complete the CAPTCHA";
           event.preventDefault();
           return;
    }

    const nameRegex = /^[A-Za-zא-ת]+$/;
    if (!nameRegex.test(firstName)) {
        errorContainer.textContent = "First name must contain only letters.";
        event.preventDefault();
        return;
    }

    if (!nameRegex.test(lastName)) {
        errorContainer.textContent = "Last name must contain only letters.";
        event.preventDefault();
        return;
    }


    const idRegex = /^\d{9}$/;
    if (!idRegex.test(idNumber)) {
        errorContainer.textContent = "ID Number must contain exactly 9 digits.";
        event.preventDefault();
        return;
    }

    if (password !== confirmPassword) {
        errorContainer.textContent = "Passwords do not match. Please try again.";
        event.preventDefault();
        return;
    }
});


document.getElementById("idNumber").addEventListener("input", function(event) {
    const idRegex = /^\d*$/;
    if (!idRegex.test(event.target.value)) {
        event.target.value = event.target.value.replace(/[^\d]/g, "");
    }
});