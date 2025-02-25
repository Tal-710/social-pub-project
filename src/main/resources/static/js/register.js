const MAX_WIDTH = 800;
const MAX_HEIGHT = 800;
const MAX_FILE_SIZE = 5 * 1024 * 1024;

let stream = null;
const profilePicFile = document.getElementById("profile-pic-file");
const cameraButton = document.getElementById("camera-button");
const cameraFeed = document.getElementById("camera-feed");
const photoCanvas = document.getElementById("photo-canvas");
const profilePreview = document.getElementById("profile-preview");
const profilePicData = document.getElementById("profile-pic-data");
const cameraClose = document.getElementById("camera-close");
const errorAlertCamera = document.getElementById("errorAlertCamera");
const closeCameraAlert = document.getElementById("closeCameraAlert");

let cameraStream = null;
function resizeImage(file) {
  return new Promise((resolve, reject) => {
    if (file.size > MAX_FILE_SIZE) {
      reject(new Error("File size too large. Maximum size is 5MB."));
      return;
    }

    const img = new Image();
    img.src = URL.createObjectURL(file);

    img.onload = () => {
      URL.revokeObjectURL(img.src);

      let width = img.width;
      let height = img.height;

      if (width > MAX_WIDTH) {
        height = Math.round((height * MAX_WIDTH) / width);
        width = MAX_WIDTH;
      }
      if (height > MAX_HEIGHT) {
        width = Math.round((width * MAX_HEIGHT) / height);
        height = MAX_HEIGHT;
      }

      const canvas = document.createElement("canvas");
      canvas.width = width;
      canvas.height = height;

      const ctx = canvas.getContext("2d");
      ctx.drawImage(img, 0, 0, width, height);

      const resizedDataUrl = canvas.toDataURL("image/jpeg", 0.8);
      resolve(resizedDataUrl);
    };

    img.onerror = () => {
      reject(new Error("Failed to load image"));
    };
  });
}

profilePicFile.addEventListener("change", async function (e) {
  const file = e.target.files[0];
  if (file) {
    try {
      const errorContainer = document.getElementById("error-message");
      errorContainer.textContent = "";

      if (!file.type.startsWith("image/")) {
        throw new Error("Please select an image file");
      }

      const resizedDataUrl = await resizeImage(file);

      profilePreview.src = resizedDataUrl;
      profilePreview.style.display = "block";
      profilePicData.value = resizedDataUrl;

      stopCamera();
    } catch (error) {
      const errorContainer = document.getElementById("error-message");
      errorContainer.textContent = error.message;
      this.value = "";
      profilePreview.style.display = "none";
      profilePicData.value = "";
    }
  }
});

cameraButton.addEventListener("click", async function () {
  if (cameraFeed.style.display === "none") {
    try {
      stream = await navigator.mediaDevices.getUserMedia({
        video: {
          width: { max: MAX_WIDTH },
          height: { max: MAX_HEIGHT },
        },
      });
      cameraFeed.srcObject = stream;
      cameraFeed.style.display = "block";
      cameraFeed.play();
      cameraButton.textContent = "Capture Photo";
      cameraClose.style.display = "block";
      cameraClose.addEventListener("click", async function () {
        stopCamera();
        cameraClose.style.display = "none";
      });
    } catch (err) {
      console.error("Error accessing camera:", err);
      // alert(
      //   "Could not access camera. Please ensure camera permissions are granted."
      // );
      errorAlertCamera.style.display = "flex";
      closeCameraAlert.addEventListener("click", () => {
        errorAlertCamera.style.display = "none";
      });
    }
  } else {
    const canvas = document.createElement("canvas");
    canvas.width = cameraFeed.videoWidth;
    canvas.height = cameraFeed.videoHeight;

    let width = cameraFeed.videoWidth;
    let height = cameraFeed.videoHeight;

    if (width > MAX_WIDTH) {
      height = Math.round((height * MAX_WIDTH) / width);
      width = MAX_WIDTH;
    }
    if (height > MAX_HEIGHT) {
      width = Math.round((width * MAX_HEIGHT) / height);
      height = MAX_HEIGHT;
    }

    const context = canvas.getContext("2d");
    context.drawImage(cameraFeed, 0, 0, width, height);
    const photoData = canvas.toDataURL("image/jpeg", 0.8);

    profilePreview.src = photoData;
    profilePreview.style.display = "block";
    profilePicData.value = photoData;
    stopCamera();
    cameraButton.textContent = "Take Photo";
  }
});

function stopCamera() {
  if (stream) {
    stream.getTracks().forEach((track) => track.stop());
    stream = null;
  }
  cameraFeed.style.display = "none";
  cameraButton.textContent = "Take Photo";
}

document
  .getElementById("register-form")
  .addEventListener("submit", function (event) {
    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const idNumber = document.getElementById("idNumber").value;
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirm-password").value;

    const errorContainer = document.getElementById("error-message");
    const captchaError = document.getElementById("captcha-error");
    errorContainer.textContent = "";
    captchaError.textContent = "";

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

document.getElementById("idNumber").addEventListener("input", function (event) {
  const idRegex = /^\d*$/;
  if (!idRegex.test(event.target.value)) {
    event.target.value = event.target.value.replace(/[^\d]/g, "");
  }
});
