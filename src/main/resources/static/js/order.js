function addOrder() {
  const maxOrders = 3;
  const currentOrderCount = document.querySelectorAll('#orderSummary > div').length;
  if (currentOrderCount >= maxOrders) {
    return;}

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


  const hasItems = Object.values(itemCounts).some((count) => count > 0);
  if (!hasItems) {
  alert("you must select at least one item to order")
    return;
  }


  const timestamp = new Date().toLocaleString();

  let orderText = `Order at ${timestamp}: `;
  for (const [item, count] of Object.entries(itemCounts)) {
    if (count > 0) {
      orderText += `${item.charAt(0).toUpperCase() + item.slice(1)}: ${count} | `;
    }
  }


  orderText = orderText.trim().replace(/\| $/, '');

  const newOrder = document.createElement('div');
  newOrder.id = 'invite';
  newOrder.textContent = orderText;


  const deleteButton = document.createElement('button');
  deleteButton.id = 'deleteButton';
  deleteButton.textContent = 'Delete';
  deleteButton.style.cursor = 'pointer';


  deleteButton.addEventListener('click', () => {
    newOrder.remove();
    historyItem.remove();
  });
  const submitButton = document.createElement('button');
  submitButton.id = 'submitButton';
  submitButton.textContent = 'Submit';
  submitButton.style.cursor = 'pointer';


  submitButton.addEventListener('click', () => {
    console.log('Submitted Order: ', orderText);
    newOrder.remove();
  });

  newOrder.appendChild(deleteButton);
  orderSummary.appendChild(newOrder);
  newOrder.appendChild(submitButton);


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
      if (count < 10) {
          orderCount.textContent = count + 1;
        }
  });
  });