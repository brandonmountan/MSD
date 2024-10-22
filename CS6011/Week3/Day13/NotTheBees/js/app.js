const canvas = document.getElementById('gameCanvas');
const ctx = canvas.getContext('2d');

canvas.width = window.innerWidth;
canvas.height = window.innerHeight;

let mouseX = canvas.width / 2;
let mouseY = canvas.height / 2;

const chaserImage = new Image();
chaserImage.src = './img/tsticker.png';

const chaserRadius = 30;
const followerCount = 5;
const followers = [];

class Follower {
  constructor(x, y) {
    this.x = x;
    this.y = y;
    this.speed = 1.5 + Math.random() * 2; // Random speed for each follower
  }

  update() {
    const dx = mouseX - this.x;
    const dy = mouseY - this.y;
    const distance = Math.sqrt(dx * dx + dy * dy);

    if (distance > chaserRadius) {
      this.x += (dx / distance) * this.speed;
      this.y += (dy / distance) * this.speed;
    }
  }

  draw() {
    ctx.drawImage(chaserImage, this.x - chaserRadius / 2, this.y - chaserRadius / 2, chaserRadius, chaserRadius);
  }
}

for (let i = 0; i < followerCount; i++) {
  const follower = new Follower(Math.random() * canvas.width, Math.random() * canvas.height);
  followers.push(follower);
}

function update() {
  for (const follower of followers) {
    follower.update();
  }
}

function draw() {
  ctx.clearRect(0, 0, canvas.width, canvas.height);
  ctx.drawImage(chaserImage, mouseX - chaserRadius / 2, mouseY - chaserRadius / 2, chaserRadius, chaserRadius);

  for (const follower of followers) {
    follower.draw();
  }
}

function gameLoop() {
  update();
  draw();
  requestAnimationFrame(gameLoop);
}

document.addEventListener('mousemove', (event) => {
  mouseX = event.clientX;
  mouseY = event.clientY;
});

window.onload = () => {
  chaserImage.onload = () => {
    gameLoop();
  };
};
