document.addEventListener('DOMContentLoaded', () => {
  const table = document.getElementById('multiplication-table');
  let currentClickedCell = null;
  let intervalId;
  let isAnimating = false;

  // Create the multiplication table
  for (let i = 1; i <= 10; i++) {
    const row = document.createElement('tr');
    for (let j = 1; j <= 10; j++) {
      const cell = document.createElement('td');
      cell.textContent = i * j;

      // Add mouseover and mouseout events for highlighting
      cell.addEventListener('mouseover', () => {
        cell.classList.add('highlighted');
      });
      cell.addEventListener('mouseout', () => {
        cell.classList.remove('highlighted');
      });

      // Add click event for toggling the clicked style
      cell.addEventListener('click', () => {
        if (currentClickedCell) {
          currentClickedCell.classList.remove('clicked');
        }
        currentClickedCell = cell;
        currentClickedCell.classList.add('clicked');
      });

      row.appendChild(cell);
    }
    table.appendChild(row);
  }

  // Background color animation
  const toggleBackground = document.getElementById('toggle-background');
  toggleBackground.addEventListener('click', () => {
    if (isAnimating) {
      clearInterval(intervalId);
      isAnimating = false;
    } else {
      isAnimating = true;
      animateBackground();
    }
  });

  function animateBackground() {
    let colorStep = 0;
    const colors = ['blue', 'purple', 'red'];

    intervalId = setInterval(() => {
      document.body.style.backgroundColor = colors[colorStep];
      colorStep = (colorStep + 1) % colors.length;
    }, 1000);
  }
});
