// let and const provide Block Scope in javascript.
// Variables declared inside a block using let or const are only
// accessible within that block and any nested blocks.
// let can be redefined, const cannot
let socket;
const messagesDiv = document.getElementById('messages');
const usernameInput = document.getElementById('username');
const roomNameInput = document.getElementById('roomName');
const messageInput = document.getElementById('message');
const joinButton = document.getElementById('join-btn');
const sendButton = document.getElementById('send-btn');

// element.addEventListener('event', 'function', 'useCapture'(optional))
// first parameter - 'click' or 'mousedown' etc.
// second parameter - function to be called when event occurs
// third parameter - boolean for event capturing or event bubbling(in->out propagation)
    // default is false which uses bubbling
joinButton.addEventListener('click', () => {
    const username = usernameInput.value.trim();
    const roomName = roomNameInput.value.trim();

    // Validate room name
    // or convert to lowercase
    const roomNamePattern = /^[a-z]+$/; // lowercase letters only
    if (!roomNamePattern.test(roomName)) {
        alert('Room name must contain only lowercase letters.');
        return;
    }

    socket = new WebSocket("ws://localhost:8080");

    // addEventListener("open", (event) => {});
    socket.onopen = () => {
        socket.send(`join ${username} ${roomName}`);
        messageInput.disabled = false;
        sendButton.disabled = false;
    };

    // addEventListener("message", (event) => {});
    socket.onmessage = (event) => {
        // JSON.parse(text) - static method parses JSON string constructing javascript value or object
        const data = JSON.parse(event.data);
        // if message type is text, then this field is a string
        if (data.type === "message") {
            // template literals, template strings
            addMessage(`${data.user}: ${data.message}`);
        } else if (data.type === "join") {
            addMessage(`${data.user} joined the room.`);
        } else if (data.type === "leave") {
            addMessage(`${data.user} left the room.`);
        }
    };

    // addEventListener("error", (event) => {});
    socket.onclose = () => {
        messageInput.disabled = true;
        sendButton.disabled = true;
        addMessage("Disconnected from server.");
    };
});

sendButton.addEventListener('click', () => {
    // trim() removes whitespace from both ends of string.
    // does not change original string
    const message = messageInput.value.trim();
    if (message) {
        socket.send(`message ${message}`);
        messageInput.value = '';
    }
});

function addMessage(msg) {
    const msgDiv = document.createElement('div');
    msgDiv.textContent = msg;
    messagesDiv.appendChild(msgDiv);
    messagesDiv.scrollTop = messagesDiv.scrollHeight; // Scroll to bottom
}
