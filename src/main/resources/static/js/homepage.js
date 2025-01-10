const carousel = document.querySelector('.carousel-inner');
const prevButton = document.querySelector('.carousel-btn.prev');
const nextButton = document.querySelector('.carousel-btn.next');
const items = document.querySelectorAll('.carousel-item');
let currentIndex = 0;

function showImage(index) {
    if (index >= items.length) {
        currentIndex = 0;
    } else if (index < 0) {
        currentIndex = items.length - 1;
    }
    const offset = -currentIndex * 100;
    carousel.style.transform = `translateX(${offset}%)`;
}

prevButton.addEventListener('click', function() {
    currentIndex--;
    showImage(currentIndex);
});

nextButton.addEventListener('click', function() {
    currentIndex++;
    showImage(currentIndex);
});

setInterval(function() {
    currentIndex++;
    showImage(currentIndex);
}, 3000);