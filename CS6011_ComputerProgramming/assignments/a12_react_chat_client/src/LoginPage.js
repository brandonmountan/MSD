import React, { useState } from 'react';
import InputWidget from './InputWidget';

const LoginPage = ({ onLogin }) => {
    const [username, setUsername] = useState('');
    const [room, setRoom] = useState('');

    const handleJoinRoom = () => {
        if (username && room) {
            onLogin({ username, room });
        } else {
            alert('Please enter both a username and a room name.');
        }
    };

    return (
        <div>
            <h1>Login</h1>
            <InputWidget label="Name:" placeholder="Enter your name" value={username} onChange={setUsername} />
            <InputWidget label="Room:" placeholder="Enter room name" value={room} onChange={setRoom} />
            <button onClick={handleJoinRoom}>Join Room</button>
        </div>
    );
};

export default LoginPage;
