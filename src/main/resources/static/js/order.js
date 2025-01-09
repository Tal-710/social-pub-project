console.log("asfa");
function addOrder() {
    // Fetch the counts for beer, wine, and chips
    const beerCount = parseInt(document.querySelector('#beer .order-count').textContent);
    const wineCount = parseInt(document.querySelector('#wine .order-count').textContent);
    const chipsCount = parseInt(document.querySelector('#chips .order-count').textContent);
    const coctailCount = parseInt(document.querySelector('#coctail .order-count').textContent);
    const cafeCount = parseInt(document.querySelector('#cafe .order-count').textContent);
    const sandwishCount = parseInt(document.querySelector('#sandwish .order-count').textContent);
    const cakeCount = parseInt(document.querySelector('#cake .order-count').textContent);

    const orderSummary = document.getElementById('orderSummary');
    const purchaseHistory = document.getElementById('purchaseHistory');

    // Check if there are any items in the order
    if (
      beerCount === 0 &&
      wineCount === 0 &&
      chipsCount === 0 &&
      coctailCount === 0 &&
      cafeCount === 0 &&
      sandwishCount === 0 &&
      cakeCount === 0
    ) {
      alert('Please add some items to your order.');
      return;
    }

    // Get the current timestamp
    const timestamp = new Date().toLocaleString();

    // Create the order summary text dynamically
    let orderText = `Order at ${timestamp}: `;
    if (beerCount > 0) orderText += `Beer: ${beerCount} | `;
    if (wineCount > 0) orderText += `Wine: ${wineCount} | `;
    if (chipsCount > 0) orderText += `Chips: ${chipsCount} | `;
    if (coctailCount > 0) orderText += `Coctail: ${coctailCount} | `;
    if (cafeCount > 0) orderText += `Cafe: ${cafeCount} | `;
    if (sandwishCount > 0) orderText += `Sandwish: ${sandwishCount} | `;
    if (cakeCount > 0) orderText += `Cake: ${cakeCount}`;

    // Remove the trailing "| " if present
    orderText = orderText.trim();
    if (orderText.endsWith('|')) {
      orderText = orderText.slice(0, -1);
    }

    // Display the order summary
    const newOrder = document.createElement('div');
    newOrder.id = 'invite';
    newOrder.textContent = orderText;
    orderSummary.appendChild(newOrder);

    // Add the order to purchase history
    const historyItem = document.createElement('li');
    historyItem.textContent = orderText;
    purchaseHistory.appendChild(historyItem);
  }



  // Function to add a new user
  function addUser() {
    const newUser = document.getElementById('newUser').value;
    if (newUser) {
      const userList = document.getElementById('userList');
      const userItem = document.createElement('li');
      userItem.textContent = newUser;
      // Create a delete button
      const deleteButton = document.createElement('button');
      deleteButton.textContent = 'Delete';
      deleteButton.onclick = function() {
        userList.removeChild(userItem);
      };

      // Append the delete button to the product item
      userItem.appendChild(deleteButton);
      userList.appendChild(userItem);
      document.getElementById('newUser').value = '';
    }
  }

// Function to add a new product
function addProduct() {
    const newProduct = document.getElementById('newProduct').value;
    if (newProduct) {
      const productList = document.getElementById('productList');

      // Create a list item for the new product
      const productItem = document.createElement('li');
      productItem.textContent = newProduct;

      // Create a delete button
      const deleteButton = document.createElement('button');
      deleteButton.textContent = 'Delete';
      deleteButton.onclick = function() {
        productList.removeChild(productItem);
      };

      // Append the delete button to the product item
      productItem.appendChild(deleteButton);

      // Append the product item to the list
      productList.appendChild(productItem);

      // Clear the input field
      document.getElementById('newProduct').value = '';
    }
  }


  const carouselInner = document.querySelector('.carousel-inner');
const prevButton = document.querySelector('.carousel-btn.prev');
const nextButton = document.querySelector('.carousel-btn.next');
const items = document.querySelectorAll('.carousel-item');

let currentIndex = 0;

function updateCarousel() {
    const offset = -currentIndex * 100; // Calculate the transform offset
    carouselInner.style.transform = translateX(`${offset}%`);
}

nextButton.addEventListener('click', () => {
    currentIndex = (currentIndex + 1) % items.length; // Loop back to first slide
    updateCarousel();
});

prevButton.addEventListener('click', () => {
    currentIndex = (currentIndex - 1 + items.length) % items.length; // Loop to last slide
    updateCarousel();
});

// Select all the + and - buttons
const minusButtons = document.querySelectorAll('.minus');
const plusButtons = document.querySelectorAll('.plus');

// Function to handle the button click events
minusButtons.forEach((button, index) => {
  button.addEventListener('click', () => {
    const orderCount = button.nextElementSibling;
    let count = parseInt(orderCount.textContent);
    if (count > 0) {
      orderCount.textContent = count - 1;
    }
  });
});

plusButtons.forEach((button, index) => {
  button.addEventListener('click', () => {
    const orderCount = button.previousElementSibling;
    let count = parseInt(orderCount.textContent);
    orderCount.textContent = count + 1;
  });
});