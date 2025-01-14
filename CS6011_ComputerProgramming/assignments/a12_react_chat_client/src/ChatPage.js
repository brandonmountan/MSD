import React, { useState, useEffect, useRef } from 'react';

const ChatPage = ({ userInfo, onLogout }) => {
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const ws = useRef(null);

    useEffect(() => {
        ws.current = new WebSocket('ws://localhost:8080');
        
        ws.current.onopen = () => console.log('Connected to WebSocket');
        ws.current.onmessage = (event) => {
            const message = JSON.parse(event.data);
            setMessages((prev) => [...prev, message]);
        };
        ws.current.onclose = () => console.log('WebSocket disconnected');

        return () => {
            ws.current.close();
        };
    }, []);

    const handleSendMessage = () => {
        if (newMessage) {
            const message = { user: userInfo.username, text: newMessage };
            ws.current.send(JSON.stringify(message));
            setMessages((prev) => [...prev, message]);
            setNewMessage('');
        }
    };

    return (
        <div>
            <h1>Room: {userInfo.room}</h1>
            <button onClick={onLogout}>Logout</button>
            <div className="chat-box">
                {messages.map((msg, index) => (
                    <div key={index}>
                        <strong>{msg.user}:</strong> {msg.text}
                    </div>
                ))}
            </div>
            <input
                type="text"
                placeholder="Type a message"
                value={newMessage}
                onChange={(e) => setNewMessage(e.target.value)}
            />
            <button onClick={handleSendMessage}>Send</button>
        </div>
    );
};

export default ChatPage;
