document.addEventListener('DOMContentLoaded', function() {
    const button = document.getElementById('showImageButton');
    const image = document.getElementById('image');
    let qrCodeFetched = false;

    const showHistoryButton = document.getElementById('showHistoryButton');
    const purchaseHistoryContainer = document.getElementById('purchase-history-container');
    const purchaseList = document.querySelector('.purchase-list');
     let historyFetched = false;
     let cachedProducts = null;

    function restoreQRCode() {
        const savedQRCode = sessionStorage.getItem('userQRCode');
        if (savedQRCode) {
            image.src = savedQRCode;
            qrCodeFetched = true;
        }
    }

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
                        // No content
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

    image.style.display = 'none';
    restoreQRCode();
});