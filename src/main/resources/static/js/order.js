document.addEventListener('DOMContentLoaded', () => {
  const productDropdown = document.getElementById('product-dropdown');
  const quantityInput = document.getElementById('quantity-input');
  const addItemButton = document.getElementById('add-item-button');
  const orderList = document.getElementById('order-list');
  const submitOrderButton = document.getElementById('submit-order-button');
  const qrResultDiv = document.getElementById('qrResult');


  const token = document.querySelector('meta[name="_csrf"]').content;
  const header = document.querySelector('meta[name="_csrf_header"]').content;

  let order = [];
  let scannedUser = null;

  function restoreSessionData() {
    const savedOrder = sessionStorage.getItem('currentOrder');
    const savedUser = sessionStorage.getItem('scannedUser');

    if (savedOrder) {
      order = JSON.parse(savedOrder);
      orderList.innerHTML = '';
      order.forEach(item => {
        const listItem = document.createElement('li');
        listItem.textContent = `${item.productName} (x${item.quantity}) - $${item.totalPrice.toFixed(2)}`;

        const removeButton = document.createElement('button');
        removeButton.textContent = 'Remove';
        removeButton.className = 'remove-button';
        removeButton.addEventListener('click', () => {
          order = order.filter(o => o.productId !== item.productId);
          sessionStorage.setItem('currentOrder', JSON.stringify(order));
          listItem.remove();
        });

        listItem.appendChild(removeButton);
        orderList.appendChild(listItem);
      });
    }

    if (savedUser) {
      scannedUser = JSON.parse(savedUser);
      window.handleUserScan(scannedUser);
    }
  }


  submitOrderButton.disabled = true;


  window.handleUserScan = (user) => {
    scannedUser = user;

    sessionStorage.setItem('scannedUser', JSON.stringify(user));

    qrResultDiv.innerHTML = `
      <h3>User Details</h3>
      <p>ID: ${user.idNumber}</p>
      <p>Name: ${user.firstName} ${user.lastName}</p>
    `;
    submitOrderButton.disabled = false;
  };

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

    sessionStorage.setItem('currentOrder', JSON.stringify(order));

    const listItem = document.createElement('li');
    listItem.textContent = `${productName} (x${quantity}) - $${totalPrice.toFixed(2)}`;

    const removeButton = document.createElement('button');
    removeButton.textContent = 'Remove';
    removeButton.className = 'remove-button';
    removeButton.addEventListener('click', () => {
      order = order.filter(o => o.productId !== productId);
      // Update session storage on remove
      sessionStorage.setItem('currentOrder', JSON.stringify(order));
      listItem.remove();
    });

    listItem.appendChild(removeButton);
    orderList.appendChild(listItem);

    productDropdown.value = '';
    quantityInput.value = 1;
  });

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
      const response = await fetch('/orders/', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          [header]: token
        },
        credentials: 'include',
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        alert('Order submitted successfully!');
        sessionStorage.removeItem('currentOrder');
        sessionStorage.removeItem('scannedUser');

        order = [];
        orderList.innerHTML = '';
        scannedUser = null;
        qrResultDiv.innerHTML = '';
        submitOrderButton.disabled = true;
      } else {
        alert(`Failed to submit order. Status: ${response.status}`);
      }
    } catch (error) {
      console.error('Error submitting order:', error);
      alert('Error submitting order. Please try again.');
    }
  });

  restoreSessionData();
});