document.addEventListener("DOMContentLoaded", function () {
  function clearUserSessionData() {
    sessionStorage.removeItem("userData");
    sessionStorage.removeItem("userQRCode");

    const profileImage = document.getElementById("profileImage");
    const qrImage = document.getElementById("image");

    if (profileImage) {
      profileImage.src = "/images/default-profile.png";
    }
    if (qrImage) {
      qrImage.style.display = "none";
    }

    qrCodeFetched = false;
  }

  const logoutForm = document.querySelector('form[action="/logout"]');
  if (logoutForm) {
    logoutForm.addEventListener("submit", function () {
      clearUserSessionData();
    });
  }

  const MAX_WIDTH = 800;
  const MAX_HEIGHT = 800;
  const MAX_FILE_SIZE = 5 * 1024 * 1024;

  const QRbutton = document.getElementById("showQRButton");
  const image = document.getElementById("image");
  let qrCodeFetched = false;

  const profileImage = document.getElementById("profileImage");
  const profilePicContainer = document.querySelector(".profile-pic-container");
  const floatingEditor = document.getElementById("floatingEditor");
  const closeBtn = floatingEditor.querySelector(".close-floating");
  const fileInput = document.getElementById("fileInput");
  const uploadBtn = document.getElementById("uploadBtn");
  const cameraBtn = document.getElementById("cameraBtn");
  const cameraContainer = document.querySelector(".camera-container");
  const cameraFeed = document.getElementById("cameraFeed");
  const captureBtn = document.getElementById("captureBtn");
  const previewContainer = document.querySelector(".preview-container");
  const previewImage = document.getElementById("previewImage");
  const saveBtn = document.getElementById("saveBtn");
  const cancelBtn = document.getElementById("cancelBtn");
  const errorAlertCamera = document.getElementById("errorAlertCamera");
  const closeCameraAlert = document.getElementById("closeCameraAlert");

  let stream = null;

  function updateUserData(newData) {
    const cachedUser = sessionStorage.getItem("userData");
    let userData = cachedUser ? JSON.parse(cachedUser) : {};

    userData = {
      ...userData,
      ...newData,
    };

    sessionStorage.setItem("userData", JSON.stringify(userData));
    return userData;
  }

  function fetchAndStoreUserData() {
    return fetch("/users/current")
      .then((response) => response.json())
      .then((user) => {
        return updateUserData({
          id: user.id,
          username: user.username,
          profilePicture: user.profilePicture,
          qrCode: user.qrCode,
        });
      });
  }

  function loadProfilePicture() {
    console.log("Loading profile picture...");

    const currentUsername = document.querySelector(
      '[sec\\:authentication="principal.username"]'
    )?.textContent;
    const cachedUser = sessionStorage.getItem("userData");

    if (cachedUser) {
      const user = JSON.parse(cachedUser);
      if (user.username !== currentUsername) {
        clearUserSessionData();
      }
    }

    if (cachedUser) {
      const user = JSON.parse(cachedUser);
      if (user.profilePicture) {
        fetch(`/users/profile-picture/${user.profilePicture}`)
          .then((response) => response.text())
          .then((profilePictureUrl) => {
            profileImage.src = profilePictureUrl;
            profileImage.onload = () => {
              profileImage.style.display = "block";
            };
          })
          .catch((error) => {
            console.error("Failed to load profile picture:", error);
            profileImage.src = "/images/default-profile.png";
            profileImage.style.display = "block";
          });
        return;
      }
    }

    fetchAndStoreUserData()
      .then((user) => {
        if (user.profilePicture) {
          return fetch(`/users/profile-picture/${user.profilePicture}`)
            .then((response) => response.text())
            .then((profilePictureUrl) => {
              profileImage.src = profilePictureUrl;
              profileImage.onload = () => {
                profileImage.style.display = "block";
              };
            });
        } else {
          profileImage.src = "/images/default-profile.png";
          profileImage.style.display = "block";
        }
      })
      .catch((error) => {
        console.error("Failed to load profile picture:", error);
        profileImage.src = "/images/default-profile.png";
        profileImage.style.display = "block";
      });
  }

  function resizeImage(file) {
    return new Promise((resolve, reject) => {
      if (file.size > MAX_FILE_SIZE) {
        reject(new Error("File size too large. Maximum size is 5MB."));
        return;
      }

      const reader = new FileReader();
      reader.onload = (e) => {
        const img = new Image();
        img.onload = () => {
          const canvas = document.createElement("canvas");
          let width = img.width;
          let height = img.height;

          if (width > height) {
            if (width > MAX_WIDTH) {
              height = Math.round((height * MAX_WIDTH) / width);
              width = MAX_WIDTH;
            }
          } else {
            if (height > MAX_HEIGHT) {
              width = Math.round((width * MAX_HEIGHT) / height);
              height = MAX_HEIGHT;
            }
          }

          canvas.width = width;
          canvas.height = height;
          const ctx = canvas.getContext("2d");
          ctx.drawImage(img, 0, 0, width, height);
          resolve(canvas.toDataURL("image/jpeg", 0.8));
        };
        img.onerror = () => reject(new Error("Failed to load image"));
        img.src = e.target.result;
      };
      reader.onerror = () => reject(new Error("Failed to read file"));
      reader.readAsDataURL(file);
    });
  }

  function stopCamera() {
    if (stream) {
      stream.getTracks().forEach((track) => track.stop());
      stream = null;
    }
    cameraContainer.style.display = "none";
  }

  function closeModal() {
    floatingEditor.style.display = "none";
    stopCamera();
    resetModal();
  }

  function resetModal() {
    cameraContainer.style.display = "none";
    previewContainer.style.display = "none";
    if (fileInput) fileInput.value = "";
    stopCamera();
  }

  function showPreview(imageData) {
    previewImage.src = imageData;
    previewContainer.style.display = "block";
    cameraContainer.style.display = "none";
  }

  profilePicContainer.addEventListener("click", () => {
    floatingEditor.style.display = "block";
  });

  closeBtn.addEventListener("click", closeModal);

  uploadBtn.addEventListener("click", () => {
    fileInput.click();
  });

  fileInput.addEventListener("change", async (e) => {
    const file = e.target.files[0];
    if (file) {
      try {
        const resizedImage = await resizeImage(file);
        showPreview(resizedImage);
      } catch (error) {
        alert(error.message);
      }
    }
  });

  cameraBtn.addEventListener("click", async () => {
    try {
      stream = await navigator.mediaDevices.getUserMedia({
        video: {
          facingMode: "user",
          width: { ideal: 1280 },
          height: { ideal: 720 },
        },
      });
      cameraFeed.srcObject = stream;
      cameraContainer.style.display = "block";
      previewContainer.style.display = "none";

      const closeCameraBtn = document.createElement("button");
      closeCameraBtn.textContent = "Close Camera";
      closeCameraBtn.classList.add("action-btn", "close-camera-btn");
      closeCameraBtn.addEventListener("click", () => {
        stopCamera();
        cameraContainer.style.display = "none";
      });

      const existingCloseBtn =
        cameraContainer.querySelector(".close-camera-btn");
      if (existingCloseBtn) {
        existingCloseBtn.remove();
      }

      cameraContainer.appendChild(closeCameraBtn);

      await cameraFeed.play();
    } catch (error) {
      // alert('Could not access camera. Please ensure camera permissions are granted.');
      errorAlertCamera.style.display = "flex";
      closeCameraAlert.addEventListener("click", () => {
        errorAlertCamera.style.display = "none";
      });
    }
  });

  captureBtn.addEventListener("click", () => {
    try {
      const canvas = document.createElement("canvas");
      canvas.width = cameraFeed.videoWidth;
      canvas.height = cameraFeed.videoHeight;
      const ctx = canvas.getContext("2d");
      ctx.drawImage(cameraFeed, 0, 0);
      const imageData = canvas.toDataURL("image/jpeg", 0.8);
      showPreview(imageData);
      stopCamera();
    } catch (error) {
      alert("Failed to capture photo. Please try again.");
    }
  });

  saveBtn.addEventListener("click", async () => {
    if (!previewImage.src || previewContainer.style.display === "none") {
      alert("Please select or capture an image first");
      return;
    }

    try {
      saveBtn.disabled = true;
      saveBtn.textContent = "Saving...";

      const token = document.querySelector('meta[name="_csrf"]')?.content;
      const header = document.querySelector(
        'meta[name="_csrf_header"]'
      )?.content;

      if (!token || !header) {
        throw new Error("CSRF tokens not found");
      }

      console.log(
        "Sending image data:",
        previewImage.src.substring(0, 100) + "..."
      );

      const response = await fetch("/users/update-profile-picture", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          [header]: token,
        },
        body: JSON.stringify({
          profilePicture: previewImage.src,
        }),
        credentials: "same-origin",
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Failed to update profile picture");
      }

      const result = await response.json();
      console.log("Server response:", result);

      if (!result.fileName) {
        throw new Error("No filename received from server");
      }

      const urlResponse = await fetch(
        `/users/profile-picture/${result.fileName}`
      );
      const profilePictureUrl = await urlResponse.text();

      profileImage.src = profilePictureUrl;
      updateUserData({ profilePicture: result.fileName });

      sessionStorage.removeItem("userData");
      await fetchAndStoreUserData();
      closeModal();
    } catch (error) {
      console.error("Error details:", error);
      alert("Error updating profile picture: " + error.message);
    } finally {
      saveBtn.disabled = false;
      saveBtn.textContent = "Save";
    }
  });

  cancelBtn.addEventListener("click", closeModal);

  function restoreQRCode() {
    const savedQRCode = sessionStorage.getItem("userQRCode");
    if (savedQRCode) {
      image.src = savedQRCode;
      qrCodeFetched = true;
    }
  }

  QRbutton.addEventListener("click", function () {
    if (!qrCodeFetched) {
      const cachedUser = sessionStorage.getItem("userData");
      if (cachedUser) {
        const user = JSON.parse(cachedUser);
        if (user.qrCode) {
          fetch(`/users/qr-code/${user.qrCode}`)
            .then((response) => response.text())
            .then((qrCodeUrl) => {
              image.src = qrCodeUrl;
              sessionStorage.setItem("userQRCode", qrCodeUrl);
              qrCodeFetched = true;
              image.style.display = "block";
            });
        } else {
          image.style.display = "none";
          fetchAndStoreUserData();
        }
        return;
      }

      fetchAndStoreUserData()
        .then((user) => {
          if (user.qrCode) {
            return fetch(`/users/qr-code/${user.qrCode}`)
              .then((response) => response.text())
              .then((qrCodeUrl) => {
                image.src = qrCodeUrl;
                sessionStorage.setItem("userQRCode", qrCodeUrl);
                qrCodeFetched = true;
                image.style.display = "block";
              });
          } else {
            image.style.display = "none";
          }
        })
        .catch(() => {
          image.style.display = "none";
        });
    } else {
      if (image.style.display === "none" || image.style.display === "") {
        image.style.display = "block";
      } else {
        image.style.display = "none";
      }
    }
  });

  loadProfilePicture();
});
