// App.js
import React, { useState } from 'react';
import { SafeAreaView } from 'react-native';
import LoginPage from './LoginPage';
import ChatPage from './ChatPage';

const App = () => {
  const [user, setUser] = useState(null);
  const [roomName, setRoomName] = useState('');

  const handleJoin = (username, room) => {
    setUser(username);
    setRoomName(room);
  };

  return (
    <SafeAreaView style={{ flex: 1 }}>
      {user ? (
        <ChatPage username={user} />
      ) : (
        <LoginPage onJoin={handleJoin} />
      )}
    </SafeAreaView>
  );
};

export default App;
