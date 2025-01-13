
console.log("1")
function addOrder() {
  const maxOrders = 3;
  const currentOrderCount = document.querySelectorAll('#orderSummary > div').length;
  if (currentOrderCount >= maxOrders) {
    return;}
  // Fetch item counts
  const itemCounts = {
    beer: parseInt(document.querySelector('#beer .order-count').textContent),
    wine: parseInt(document.querySelector('#wine .order-count').textContent),
    chips: parseInt(document.querySelector('#chips .order-count').textContent),
    coctail: parseInt(document.querySelector('#coctail .order-count').textContent),
    cafe: parseInt(document.querySelector('#cafe .order-count').textContent),
    sandwish: parseInt(document.querySelector('#sandwish .order-count').textContent),
    cake: parseInt(document.querySelector('#cake .order-count').textContent),
  };

  const orderSummary = document.getElementById('orderSummary');
  const purchaseHistory = document.getElementById('purchaseHistory');

  // Check if any item is selected
  const hasItems = Object.values(itemCounts).some((count) => count > 0);
  if (!hasItems) {
  alert("you must select at least one item to order")
    return;
  }

  // Get current timestamp
  const timestamp = new Date().toLocaleString();

  // Create order summary text
  let orderText = `Order at ${timestamp}: `;
  for (const [item, count] of Object.entries(itemCounts)) {
    if (count > 0) {
      orderText += `${item.charAt(0).toUpperCase() + item.slice(1)}: ${count} | `;
    }
  }

  // Remove trailing "| " and trim whitespace
  orderText = orderText.trim().replace(/\| $/, '');

  // Display order summary with delete button
  const newOrder = document.createElement('div');
  newOrder.id = 'invite';
  newOrder.textContent = orderText;

  // Create delete button
  const deleteButton = document.createElement('button');
  deleteButton.id = 'deleteButton';
  deleteButton.textContent = 'Delete';
  deleteButton.style.cursor = 'pointer';

  // Attach delete functionality
  deleteButton.addEventListener('click', () => {
    newOrder.remove();
    historyItem.remove();
  });
  const submitButton = document.createElement('button');
  submitButton.id = 'submitButton';
  submitButton.textContent = 'Submit';
  submitButton.style.cursor = 'pointer';

  // Attach submit functionality (log to console)
  submitButton.addEventListener('click', () => {
    console.log('Submitted Order: ', orderText);
    newOrder.remove();
  });

  newOrder.appendChild(deleteButton);
  orderSummary.appendChild(newOrder);
  newOrder.appendChild(submitButton);

  // Add order to purchase history with delete button
  const historyItem = document.createElement('li');
  historyItem.textContent = orderText;

  const historyDeleteButton = deleteButton.cloneNode(true);
  historyDeleteButton.addEventListener('click', () => {
    newOrder.remove();
    historyItem.remove();
  });

  historyItem.appendChild(historyDeleteButton);
  purchaseHistory.appendChild(historyItem);
}

// Order Quantity Adjustment
const minusButtons = document.querySelectorAll('.minus');
const plusButtons = document.querySelectorAll('.plus');

minusButtons.forEach((button) => {
  button.addEventListener('click', () => {
    const orderCount = button.nextElementSibling;
    let count = parseInt(orderCount.textContent);
    if (count > 0) {
      orderCount.textContent = count - 1;
    }
  });
});

plusButtons.forEach((button) => {
  button.addEventListener('click', () => {
    const orderCount = button.previousElementSibling;
    let count = parseInt(orderCount.textContent);
    orderCount.textContent = count + 1;
  });
  });