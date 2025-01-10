window.onload = function() {
    const calculateBtn = document.getElementById('calculateBtn');
    const resultDiv = document.getElementById('result');

    calculateBtn.addEventListener('click', function() {
        const x = document.getElementById('x').value;
        const y = document.getElementById('y').value;

        // Create XMLHttpRequest for the calculator
        const xhr = new XMLHttpRequest();
        xhr.open('GET', `/calculate?x=${x}&y=${y}`, true);

        xhr.onload = function() {
            if (xhr.status === 200) {
                const result = JSON.parse(xhr.responseText);
                resultDiv.innerHTML = `Result: ${result}`;
            } else {
                resultDiv.innerHTML = 'Error: Could not calculate.';
            }
        };

        xhr.onerror = function() {
            resultDiv.innerHTML = 'Error: Request failed.';
        };

        xhr.send();
    });

    // WebSocket functionality
    const ws = new WebSocket('ws://localhost:8080'); // Adjust if your server uses a different port
    const wsCalculateBtn = document.getElementById('wsCalculateBtn');
    const wsResultDiv = document.getElementById('wsResult');

    wsCalculateBtn.addEventListener('click', function() {
        const wsX = document.getElementById('wsX').value;
        const wsY = document.getElementById('wsY').value;

        // Send numbers via WebSocket
        ws.send(`${wsX} ${wsY}`);
    });

    // Listen for messages from the server
    ws.onmessage = function(event) {
        wsResultDiv.innerHTML = `WebSocket Result: ${event.data}`;
    };

    ws.onerror = function(event) {
        wsResultDiv.innerHTML = 'WebSocket error: ' + event.message;
    };
};
