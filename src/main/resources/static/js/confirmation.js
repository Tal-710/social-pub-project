document.addEventListener('DOMContentLoaded', function() {
    var button = document.getElementById('showImageButton');
    var image = document.getElementById('image');
    var qrCodeFetched = false;

    function restoreQRCode() {
        const savedQRCode = sessionStorage.getItem('userQRCode');
        if (savedQRCode) {
            image.src = savedQRCode;
            image.style.display = 'block';
            qrCodeFetched = true;
        }
    }

    button.addEventListener('click', function() {
        if (!qrCodeFetched) {
            fetch('/users/current')
                .then(function(response) {
                    if (response.ok) {
                        return response.json();
                    } else {
                        throw new Error('Error fetching user details');
                    }
                })
                .then(function(user) {
                    if (user.qrCode) {
                        // Set the image source to the QR code URL
                        image.src = user.qrCode;

                        sessionStorage.setItem('userQRCode', user.qrCode);

                        qrCodeFetched = true;

                        image.style.display = 'block';
                    } else {
                        console.error('No QR code found for the user');
                        image.style.display = 'none';
                    }
                })
                .catch(function(error) {
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

    image.style.display = 'none';

    restoreQRCode();
});