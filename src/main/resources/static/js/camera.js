const toggleButton = document.getElementById("toggleButton");
const cameraFeed = document.getElementById("cameraFeed");
const qrCanvas = document.getElementById("qrCanvas");
const qrContext = qrCanvas.getContext("2d");
const qrResultDiv = document.getElementById("qrResult");
const closeCameraButton = document.getElementById("closeCamera");
const buttonContainer = document.getElementById("buttonContainer");
const errorAlertCamera = document.getElementById("errorAlertCamera");
const closeCameraAlert = document.getElementById("closeCameraAlert");
let cameraStream = null;
let qrScanInterval = null;
let cameraActive = false;

async function toggleCamera() {
  if (!cameraActive) {
    try {
     if (typeof window.handleUserScan === "function") {
          window.clearUserData();
     }
      cameraStream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: "environment" },
      });
      cameraFeed.srcObject = cameraStream;
      cameraFeed.style.display = "block";
      closeCameraButton.style.display = "block";
      toggleButton.textContent = "Close Camera";
      buttonContainer.style.flexDirection = "column";
      cameraActive = true;
      startQRScan();
    } catch (error) {
      // alert("Unable to access camera. Please check your permissions.");
      errorAlertCamera.style.display = "flex";
      closeCameraAlert.addEventListener("click", () => {
        errorAlertCamera.style.display = "none";
      });
    }
  } else {
    closeCamera();
  }
}

function startQRScan() {
  qrScanInterval = setInterval(() => {
    qrCanvas.width = cameraFeed.videoWidth;
    qrCanvas.height = cameraFeed.videoHeight;
    qrContext.drawImage(cameraFeed, 0, 0, qrCanvas.width, qrCanvas.height);
    const imageData = qrContext.getImageData(
      0,
      0,
      qrCanvas.width,
      qrCanvas.height
    );
    const qrCode = jsQR(imageData.data, imageData.width, imageData.height);

    if (qrCode) {
      qrResultDiv.textContent = "QR Code Detected: " + qrCode.data;
      fetchUserDetails(qrCode.data);
      closeCamera();
    } else {
      qrResultDiv.textContent = "";
    }
  }, 500);
}

function closeCamera() {
  if (cameraStream) {
    const tracks = cameraStream.getTracks();
    tracks.forEach((track) => track.stop());
    cameraFeed.style.display = "none";
    closeCameraButton.style.display = "none";
    toggleButton.textContent = "Open Camera";
    qrResultDiv.textContent = "";
    buttonContainer.style.flexDirection = "row";
  }
  cameraActive = false;
  clearInterval(qrScanInterval);
}

async function fetchUserDetails(userId) {
  try {
    const response = await fetch(userId);

    if (response.ok) {
      const user = await response.json();
      displayUserDetails(user);

      if (typeof window.handleUserScan === "function") {
        window.handleUserScan(user);
      }
    } else {
      qrResultDiv.textContent = "User not found.";
    }
  } catch (error) {
    qrResultDiv.textContent = "Error fetching user details.";
  }
}

function displayUserDetails(user) {
  if (!user.profilePicture) {
    qrResultDiv.innerHTML = `
      <h3>User Details</h3>
      <div class="user-info-container">
        <div class="profile-pic-container">
          <img src="/images/default-profile.png"
               alt="Profile Picture"
               class="profile-pic">
        </div>
        <div class="user-details">
          <p>ID: ${user.idNumber}</p>
          <p>Name: ${user.firstName} ${user.lastName}</p>
        </div>
      </div>
    `;
    return;
  }

  fetch(`/users/profile-picture/${user.profilePicture}`)
    .then(response => response.text())
    .then(profilePictureUrl => {
      const uniqueAttr = `data-img-${Date.now()}`;

      qrResultDiv.innerHTML = `
        <h3>User Details</h3>
        <div class="user-info-container">
          <div class="profile-pic-container">
            <img ${uniqueAttr}="${profilePictureUrl}"
                 src="${profilePictureUrl}"
                 alt="Profile Picture"
                 class="profile-pic"
                 onload="this.setAttribute('src', this.getAttribute('${uniqueAttr}'))"
                 onerror="this.src='/images/default-profile.png'">
          </div>
          <div class="user-details">
            <p>ID: ${user.idNumber}</p>
            <p>Name: ${user.firstName} ${user.lastName}</p>
          </div>
        </div>
      `;
    })
    .catch(() => {
      qrResultDiv.innerHTML = `
        <h3>User Details</h3>
        <div class="user-info-container">
          <div class="profile-pic-container">
            <img src="/images/default-profile.png"
                 alt="Profile Picture"
                 class="profile-pic">
          </div>
          <div class="user-details">
            <p>ID: ${user.idNumber}</p>
            <p>Name: ${user.firstName} ${user.lastName}</p>
          </div>
        </div>
      `;
    });
}