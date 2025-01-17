const toggleButton = document.getElementById("toggleButton");
const cameraFeed = document.getElementById("cameraFeed");
const qrCanvas = document.getElementById("qrCanvas");
const qrContext = qrCanvas.getContext("2d");
const qrResultDiv = document.getElementById("qrResult");
const closeCameraButton = document.getElementById("closeCamera");
const buttonContainer = document.getElementById("buttonContainer");
let cameraStream = null;
let qrScanInterval = null;
let cameraActive = false;

async function toggleCamera() {
  if (!cameraActive) {
    try {
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
      console.error("Error accessing camera:", error);
      alert("Unable to access camera. Please check your permissions.");
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
      console.log(qrCode)
      fetchUserDetails(qrCode.data);
      closeCamera();
    } else {
      qrResultDiv.textContent = "";
    }
  }, 300);
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
    const response = await fetch(`${userId}`); // Adjust API endpoint if needed
    if (response.ok) {
      const user = await response.json();
      displayUserDetails(user);

      // Call the global function from order.js to enable the submit button
      if (typeof window.handleUserScan === "function") {
        window.handleUserScan(user);
      }
    } else {
      qrResultDiv.textContent = "User not found.";
    }
  } catch (error) {
    console.error("Error fetching user details:", error);
    qrResultDiv.textContent = "Error fetching user details.";
  }
}


//async function fetchUserDetails(userId) {
//  try {
//    const response = await fetch(`${userId}`);
//    if (response.ok) {
//      const user = await response.json();
//      displayUserDetails(user);
//    } else {
//      qrResultDiv.textContent = "User not found.";
//    }
//  } catch (error) {
//    console.error("Error fetching user details:", error);
//    qrResultDiv.textContent = "Error fetching user details.";
//  }
//}


function displayUserDetails(user) {
  qrResultDiv.innerHTML = `
    <h3>User Details</h3>
    <p>ID: ${user.idNumber}</p>
    <p>Name: ${user.firstName + ' ' + user.lastName} </p>
  `;
}