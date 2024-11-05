// ChatPage.js
import React, { useState } from 'react';
import { View, TextInput, Button, FlatList, Text, StyleSheet } from 'react-native';

const ChatPage = ({ route }) => {
  const { username } = route.params;
  const [messages, setMessages] = useState([]);
  const [message, setMessage] = useState('');

  const sendMessage = () => {
    if (message.trim()) {
      setMessages([...messages, { user: username, text: message }]);
      setMessage('');
    }
  };

  return (
    <View style={styles.container}>
      <FlatList
        data={messages}
        renderItem={({ item }) => (
          <Text style={styles.message}>{item.user}: {item.text}</Text>
        )}
        keyExtractor={(item, index) => index.toString()}
        style={styles.messageList}
      />
      <TextInput
        style={styles.input}
        placeholder="Type a message"
        value={message}
        onChangeText={setMessage}
      />
      <Button title="Send" onPress={sendMessage} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20 },
  messageList: { flex: 1 },
  message: { padding: 10, borderBottomWidth: 1 },
  input: { borderWidth: 1, marginBottom: 10, padding: 10 },
});

export default ChatPage;
