window.onload = function() {
    const canvas = document.getElementById("gameCanvas");
    const ctx = canvas.getContext("2d");

    let mouseX = 0, mouseY = 0;
    let mainObj = {x: 100, y: 150};
    mainObj.img = new Image();
    mainObj.img.src = 'tsticker.png';

    // Wait for the main object image to load
    mainObj.img.onload = function() {
        // Start updating once the image is loaded
        update();
    };

    document.addEventListener("mousemove", function(event) {
        mouseX = event.clientX; // Use clientX for relative position
        mouseY = event.clientY; // Use clientY for relative position
    });

    let chasers = [];
    for (let i = 0; i < 5; i++) {
        let chaser = {
            x: Math.random() * canvas.width,
            y: Math.random() * canvas.height,
            img: new Image()
        };
        chaser.img.src = 'bsticker.png';

        // Wait for each chaser's image to load
        chaser.img.onload = function() {
            chasers.push(chaser);
        };
    }

    function update() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        // Draw the main object centered around the mouse
        ctx.drawImage(mainObj.img, mouseX - 20, mouseY - 25, 40, 50); // Adjusted to center the image

        chasers.forEach(chaser => {
            let angle = Math.atan2(mouseY - chaser.y, mouseX - chaser.x);
            chaser.x += Math.cos(angle) * 2; // Speed of chasers
            chaser.y += Math.sin(angle) * 2;

            // Draw chasers
            ctx.drawImage(chaser.img, chaser.x - 20, chaser.y - 25, 40, 50); // Adjusted to center the image
        });

        requestAnimationFrame(update);
    }
};
