// LoginPage.js
import React, { useState } from 'react';
import { View, TextInput, Button, Text, StyleSheet } from 'react-native';

const LoginPage = ({ onJoin }) => {
  const [username, setUsername] = useState('');
  const [roomName, setRoomName] = useState('');
  const [error, setError] = useState('');

  const handleJoin = () => {
    if (!username || !roomName) {
      setError('Please fill in both fields.');
    } else {
      onJoin(username, roomName);
    }
  };

  return (
    <View style={styles.container}>
      <TextInput
        style={styles.input}
        placeholder="Username"
        value={username}
        onChangeText={setUsername}
      />
      <TextInput
        style={styles.input}
        placeholder="Room Name"
        value={roomName}
        onChangeText={setRoomName}
      />
      {error ? <Text style={styles.error}>{error}</Text> : null}
      <Button title="Join Room" onPress={handleJoin} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', padding: 20 },
  input: { borderWidth: 1, marginBottom: 10, padding: 10 },
  error: { color: 'red', marginBottom: 10 },
});

export default LoginPage;
