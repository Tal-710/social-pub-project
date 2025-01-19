document.addEventListener('DOMContentLoaded', function() {
    // Constants for image processing
    const MAX_WIDTH = 800;
    const MAX_HEIGHT = 800;
    const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    // Existing elements
    const button = document.getElementById('showImageButton');
    const image = document.getElementById('image');
    let qrCodeFetched = false;

    const showHistoryButton = document.getElementById('showHistoryButton');
    const purchaseHistoryContainer = document.getElementById('purchase-history-container');
    const purchaseList = document.querySelector('.purchase-list');
    let historyFetched = false;
    let cachedProducts = null;

    // Profile picture elements
    const profileImage = document.getElementById('profileImage');
    const profilePicContainer = document.querySelector('.profile-pic-container');
    const floatingEditor = document.getElementById('floatingEditor');
    const closeBtn = floatingEditor.querySelector('.close-floating');
    const fileInput = document.getElementById('fileInput');
    const uploadBtn = document.getElementById('uploadBtn');
    const cameraBtn = document.getElementById('cameraBtn');
    const cameraContainer = document.querySelector('.camera-container');
    const cameraFeed = document.getElementById('cameraFeed');
    const captureBtn = document.getElementById('captureBtn');
    const previewContainer = document.querySelector('.preview-container');
    const previewImage = document.getElementById('previewImage');
    const saveBtn = document.getElementById('saveBtn');
    const cancelBtn = document.getElementById('cancelBtn');

    let stream = null;

    // Profile Picture Functions
    function loadProfilePicture() {
        fetch('/users/current')
            .then(response => response.json())
            .then(user => {
                console.log('User data:', user); // Debug log
                if (user.profilePicture) {
                    console.log('Profile picture URL:', user.profilePicture); // Debug log
                    profileImage.src = user.profilePicture;
                } else {
                    console.log('No profile picture found for user'); // Debug log
                }
            })
            .catch(error => console.error('Error loading profile picture:', error));
    }

    function resizeImage(file) {
        return new Promise((resolve, reject) => {
            if (file.size > MAX_FILE_SIZE) {
                reject(new Error('File size too large. Maximum size is 5MB.'));
                return;
            }

            const reader = new FileReader();
            reader.onload = (e) => {
                const img = new Image();
                img.onload = () => {
                    const canvas = document.createElement('canvas');
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
                    const ctx = canvas.getContext('2d');
                    ctx.drawImage(img, 0, 0, width, height);
                    resolve(canvas.toDataURL('image/jpeg', 0.8));
                };
                img.onerror = () => reject(new Error('Failed to load image'));
                img.src = e.target.result;
            };
            reader.onerror = () => reject(new Error('Failed to read file'));
            reader.readAsDataURL(file);
        });
    }

    function stopCamera() {
        if (stream) {
            stream.getTracks().forEach(track => track.stop());
            stream = null;
        }
        cameraContainer.style.display = 'none';
    }

    function closeModal() {
        floatingEditor.style.display = 'none';
        stopCamera();
        resetModal();
    }

    function resetModal() {
        cameraContainer.style.display = 'none';
        previewContainer.style.display = 'none';
        if (fileInput) fileInput.value = '';
        stopCamera();
    }

    function showPreview(imageData) {
        previewImage.src = imageData;
        previewContainer.style.display = 'block';
        cameraContainer.style.display = 'none';
    }

    // Event Listeners for Profile Picture
    profilePicContainer?.addEventListener('click', () => {
        floatingEditor.style.display = 'block';
    });

    closeBtn?.addEventListener('click', closeModal);

    uploadBtn?.addEventListener('click', () => {
        fileInput.click();
    });

    fileInput?.addEventListener('change', async (e) => {
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

cameraBtn?.addEventListener('click', async () => {
    try {
        stream = await navigator.mediaDevices.getUserMedia({
            video: {
                facingMode: 'user',
                width: { ideal: 1280 },
                height: { ideal: 720 }
            }
        });
        cameraFeed.srcObject = stream;
        cameraContainer.style.display = 'block';
        previewContainer.style.display = 'none';

        // Create and add close button to camera container
        const closeCameraBtn = document.createElement('button');
        closeCameraBtn.textContent = 'Close Camera';
        closeCameraBtn.classList.add('action-btn', 'close-camera-btn');
        closeCameraBtn.addEventListener('click', () => {
            stopCamera();
            cameraContainer.style.display = 'none';
        });

        // Remove any existing close buttons first
        const existingCloseBtn = cameraContainer.querySelector('.close-camera-btn');
        if (existingCloseBtn) {
            existingCloseBtn.remove();
        }

        // Add close button after the video and capture button
        cameraContainer.appendChild(closeCameraBtn);

        await cameraFeed.play();
    } catch (error) {
        console.error('Camera error:', error);
        alert('Could not access camera. Please ensure camera permissions are granted.');
    }
});

    captureBtn?.addEventListener('click', () => {
        try {
            const canvas = document.createElement('canvas');
            canvas.width = cameraFeed.videoWidth;
            canvas.height = cameraFeed.videoHeight;
            const ctx = canvas.getContext('2d');
            ctx.drawImage(cameraFeed, 0, 0);
            const imageData = canvas.toDataURL('image/jpeg', 0.8);
            showPreview(imageData);
            stopCamera();
        } catch (error) {
            console.error('Capture error:', error);
            alert('Failed to capture photo. Please try again.');
        }
    });

    saveBtn?.addEventListener('click', async () => {
        if (!previewImage.src || previewContainer.style.display === 'none') {
            alert('Please select or capture an image first');
            return;
        }

        try {
            const token = document.querySelector('meta[name="_csrf"]')?.content;
            const header = document.querySelector('meta[name="_csrf_header"]')?.content;

            if (!token || !header) {
                throw new Error('CSRF tokens not found');
            }

            const response = await fetch('/users/update-profile-picture', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [header]: token
                },
                body: JSON.stringify({ profilePicture: previewImage.src })
            });

            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || 'Failed to update profile picture');
            }

            profileImage.src = previewImage.src;
            closeModal();
        } catch (error) {
            console.error('Save error:', error);
            alert('Error updating profile picture: ' + error.message);
        }
    });

    cancelBtn?.addEventListener('click', closeModal);

    // Existing QR Code Functions
    function restoreQRCode() {
        const savedQRCode = sessionStorage.getItem('userQRCode');
        if (savedQRCode) {
            image.src = savedQRCode;
            qrCodeFetched = true;
        }
    }

    // Existing Event Listeners
    button.addEventListener('click', function() {
        if (!qrCodeFetched) {
            fetch('/users/current')
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        throw new Error('Error fetching user details');
                    }
                })
                .then(user => {
                    if (user.qrCode) {
                        image.src = user.qrCode;
                        sessionStorage.setItem('userQRCode', user.qrCode);
                        qrCodeFetched = true;
                        image.style.display = 'block';
                    } else {
                        console.error('No QR code found for the user');
                        image.style.display = 'none';
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    image.style.display = 'none';
                });
        } else {
            if (image.style.display === 'none' || image.style.display === '') {
                image.style.display = 'block';
            } else {
                image.style.display = 'none';
            }
        }
    });

    showHistoryButton.addEventListener('click', function() {
        if (purchaseHistoryContainer.style.display === 'none') {
            purchaseHistoryContainer.style.display = 'block';
            showHistoryButton.textContent = 'Hide History';
            if (!historyFetched) {
                loadPurchaseHistory();
            }
        } else {
            purchaseHistoryContainer.style.display = 'none';
            showHistoryButton.textContent = 'Order History';
        }
    });

    function loadPurchaseHistory() {
        if (cachedProducts) {
            displayProducts(cachedProducts);
            return;
        }

        fetch('/users/current')
            .then(response => response.json())
            .then(user => {
                return fetch(`/orders/user/${user.id}/recent-products`);
            })
            .then(response => {
                if (response.status === 204) {
                    displayNoOrders();
                    return null;
                }
                if (!response.ok) {
                    throw new Error('Failed to load orders');
                }
                return response.json();
            })
            .then(products => {
                if (products) {
                    cachedProducts = products;
                    displayProducts(products);
                }
                historyFetched = true;
            })
            .catch(error => {
                displayError(error.message);
            });
    }

    function displayProducts(products) {
        purchaseList.innerHTML = '';
        if (products.length === 0) {
            displayNoOrders();
            return;
        }
        products.forEach(product => {
            const productElement = document.createElement('div');
            productElement.className = 'purchase-item';
            productElement.innerHTML = `
                <span class="product-name">${product.name}</span>
                <span class="product-price">$${product.price.toFixed(2)}</span>
            `;
            purchaseList.appendChild(productElement);
        });
    }

    function displayNoOrders() {
        purchaseList.innerHTML = `
            <div class="purchase-item empty-message">
                No orders found. Visit our bar to start ordering!
            </div>`;
    }

    function displayError(message) {
        purchaseList.innerHTML = `
            <div class="purchase-item error-message">
                ${message}
            </div>`;
    }

    // Initial setup
    image.style.display = 'none';
    restoreQRCode();
    loadProfilePicture();
});