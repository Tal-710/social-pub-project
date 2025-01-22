document.addEventListener('DOMContentLoaded', () => {
  const productDropdown = document.getElementById('product-dropdown');
  const quantityInput = document.getElementById('quantity-input');
  const addItemButton = document.getElementById('add-item-button');
  const orderList = document.getElementById('order-list');
  const submitOrderButton = document.getElementById('submit-order-button');
  const qrResultDiv = document.getElementById('qrResult');
  const totalAmountSpan = document.getElementById('total-amount');


  const token = document.querySelector('meta[name="_csrf"]').content;
  const header = document.querySelector('meta[name="_csrf_header"]').content;

  let order = [];
  let scannedUser = null;

  document.querySelectorAll('a').forEach(link => {
    if (link.getAttribute('href') && link.getAttribute('href') !== '#') {
      link.addEventListener('click', () => {
        sessionStorage.removeItem('scannedUser');
      });
    }
  });


  function updateTotal() {
    const total = order.reduce((sum, item) => sum + item.totalPrice, 0);
    totalAmountSpan.textContent = total.toFixed(2);
  }
  window.clearUserData = () => {
     scannedUser = null;
     sessionStorage.removeItem('scannedUser');
     submitOrderButton.disabled = true;
     qrResultDiv.innerHTML = '';
  };

  function restoreSessionData() {
    const savedOrder = sessionStorage.getItem('currentOrder');

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
      updateTotal();
    }
  }


  submitOrderButton.disabled = true;


window.handleUserScan = (user) => {
  scannedUser = user;
  sessionStorage.setItem('scannedUser', JSON.stringify(user));

  if (user.profilePicture) {
    fetch(`/users/profile-picture/${user.profilePicture}`)
      .then(response => response.text())
      .then(profilePictureUrl => {
        qrResultDiv.innerHTML = `
          <h3>User Details</h3>
          <div class="user-info-container">
            <div class="profile-pic-container">
              <img src="${profilePictureUrl}"
                   alt="Profile Picture"
                   class="profile-pic"
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
  } else {
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
  }

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
      sessionStorage.setItem('currentOrder', JSON.stringify(order));
      listItem.remove();
      updateTotal();
    });

    listItem.appendChild(removeButton);
    orderList.appendChild(listItem);
    updateTotal();

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
        updateTotal();
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