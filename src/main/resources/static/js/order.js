document.addEventListener('DOMContentLoaded', () => {
  const productDropdown = document.getElementById('product-dropdown');
  const quantityInput = document.getElementById('quantity-input');
  const addItemButton = document.getElementById('add-item-button');
  const orderList = document.getElementById('order-list');
  const submitOrderButton = document.getElementById('submit-order-button');
  const qrResultDiv = document.getElementById('qrResult');

  // Get CSRF token from meta tags
  const token = document.querySelector('meta[name="_csrf"]').content;
  const header = document.querySelector('meta[name="_csrf_header"]').content;

  let order = [];
  let scannedUser = null;

  // Disable submit button until user is scanned
  submitOrderButton.disabled = true;

  // Handle QR result integration
  window.handleUserScan = (user) => {
    scannedUser = user;
    qrResultDiv.innerHTML = `
      <h3>User Details</h3>
      <p>ID: ${user.idNumber}</p>
      <p>Name: ${user.firstName} ${user.lastName}</p>
    `;
    submitOrderButton.disabled = false;
  };

  // Add item to order
  addItemButton.addEventListener('click', () => {
    const productId = productDropdown.value;
    const productName = productDropdown.selectedOptions[0].textContent.split(' - $')[0];
    const productPrice = parseFloat(productDropdown.selectedOptions[0].getAttribute('data-price'));
    const quantity = parseInt(quantityInput.value, 10);

    if (!productId || quantity <= 0) {
      alert('Please select a product and enter a valid quantity.');
      return;
    }

    const totalPrice = productPrice * quantity;
    const item = { productId, productName, quantity, totalPrice };
    order.push(item);

    const listItem = document.createElement('li');
    listItem.textContent = `${productName} (x${quantity}) - $${totalPrice.toFixed(2)}`;

    const removeButton = document.createElement('button');
    removeButton.textContent = 'Remove';
    removeButton.className = 'remove-button';
    removeButton.addEventListener('click', () => {
      order = order.filter(o => o.productId !== productId);
      listItem.remove();
    });

    listItem.appendChild(removeButton);
    orderList.appendChild(listItem);

    productDropdown.value = '';
    quantityInput.value = 1;
  });

  // Submit order with CSRF
  submitOrderButton.addEventListener('click', async () => {
    if (!scannedUser) {
      alert('Please scan a user before submitting the order.');
      return;
    }

    if (order.length === 0) {
      alert('Order is empty!');
      return;
    }

    const payload = {
      userId: scannedUser.idNumber,
      totalPrice: order.reduce((sum, item) => sum + item.totalPrice, 0),
      orderDetails: order.map(item => ({
        productId: item.productId,
        quantity: item.quantity,
        unitPrice: item.totalPrice / item.quantity,
        totalPrice: item.totalPrice
      }))
    };

    try {
      console.log('Payload:', JSON.stringify(payload));

      const response = await fetch('/orders/', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          [header]: token  // Add CSRF token
        },
        credentials: 'include',  // Important for CSRF
        body: JSON.stringify(payload),
      });

      console.log('Response status:', response.status);
      console.log('Response headers:', response.headers);

      const responseText = await response.text();
      console.log('Response body:', responseText);

      if (response.ok) {
        alert('Order submitted successfully!');
        // Clear the form after successful submission
        order = [];
        orderList.innerHTML = '';
        scannedUser = null;
        qrResultDiv.innerHTML = '';
        submitOrderButton.disabled = true;
      } else {
        alert(`Failed to submit order. Status: ${response.status}, Body: ${responseText}`);
      }
    } catch (error) {
      console.error('Error submitting order:', error);
      alert('Error submitting order. Please try again.');
    }
  });
});