
    const button = document.getElementById('showImageButton');
    const image = document.getElementById('image');

    button.addEventListener('click', () => {
      if (image.style.display === 'none' || image.style.display === '') {
        image.style.display = 'block';
      } else {
        image.style.display = 'none';
      }
    });