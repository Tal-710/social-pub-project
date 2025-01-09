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
      // Styling the container
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

// Start QR Scan
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
       // Fetch user details using QR code data
      closeCamera();
    } else {
      qrResultDiv.textContent = ""; // Clear previous result if no QR code
    }
  }, 300);
}

// Stop Camera
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

// Fetch user details from the server
async function fetchUserDetails(userId) {
  try {
    const response = await fetch(`${userId}`);
    if (response.ok) {
      const user = await response.json();
      displayUserDetails(user);
    } else {
      qrResultDiv.textContent = "User not found.";
    }
  } catch (error) {
    console.error("Error fetching user details:", error);
    qrResultDiv.textContent = "Error fetching user details.";
  }
}

// Display user details
function displayUserDetails(user) {
  qrResultDiv.innerHTML = `
    <h3>User Details</h3>
    <p>ID: ${user.idNumber}</p>
    <p>Name: ${user.firstName + ' ' + user.lastName} </p>
  `;
}




//const toggleButton = document.getElementById("toggleButton");
//const cameraFeed = document.getElementById("cameraFeed");
//const qrCanvas = document.getElementById("qrCanvas");
//const qrContext = qrCanvas.getContext("2d");
//const qrResultDiv = document.getElementById("qrResult");
//const closeCameraButton = document.getElementById("closeCamera");
//const buttonContainer = document.getElementById('buttonContainer')
//let cameraStream = null;
//let qrScanInterval = null;
//let cameraActive = false;
//
//
//async function toggleCamera() {
//  if (!cameraActive) {
//    try {
//      cameraStream = await navigator.mediaDevices.getUserMedia({
//        video: { facingMode: "environment" },
//      });
//      cameraFeed.srcObject = cameraStream;
//      cameraFeed.style.display = "block";
//      closeCameraButton.style.display = "block";
//      toggleButton.textContent = "Close Camera";
//      //styling the container
//      buttonContainer.style.flexDirection = 'column'
//      cameraActive = true
//      startQRScan();
//    } catch (error) {
//      console.error("Error accessing camera:", error);
//      alert("Unable to access camera. Please check your permissions.");
//    }
//  } else {
//    closeCamera();
//  }
////   cameraActive = !cameraActive;
//}
//
//// Start QR Scan
//function startQRScan() {
//  qrScanInterval = setInterval(() => {
//    qrCanvas.width = cameraFeed.videoWidth;
//    qrCanvas.height = cameraFeed.videoHeight;
//    qrContext.drawImage(cameraFeed, 0, 0, qrCanvas.width, qrCanvas.height);
//
//    const imageData = qrContext.getImageData(
//      0,
//      0,
//      qrCanvas.width,
//      qrCanvas.height
//    );
//    const qrCode = jsQR(imageData.data, imageData.width, imageData.height);
//
//    if (qrCode) {
//      qrResultDiv.textContent = "QR is available: " + qrCode.data;
//      myFunction(qrCode.data);
//      closeCamera();
//    } else {
//      qrResultDiv.textContent = ""; // Clear previous result if no QR code
//    }
//  }, 300);
//}
//
//// Stop Camera
//function closeCamera() {
//  if (cameraStream) {
//    const tracks = cameraStream.getTracks();
//    tracks.forEach((track) => track.stop());
//    cameraFeed.style.display = "none";
//    closeCameraButton.style.display = "none";
//    toggleButton.textContent = "Open Camera";
//    qrResultDiv.textContent = "";
//    buttonContainer.style.flexDirection = 'row'
//  }
//  cameraActive = false;
//  clearInterval(qrScanInterval);
//}
//
//function myFunction(qrLink) {
//  window.open(qrLink);
//}