// App.js
import React from 'react';
//import { createNativeStackNavigator } from '@react-navigation/native-stack';
import LoginPage from './LoginPage';
import ChatPage from './ChatPage';

const Stack = createNativeStackNavigator();

const App = () => {
  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName="Login">
        <Stack.Screen name="Login" component={LoginPage} />
        <Stack.Screen name="Chat" component={ChatPage} />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default App;
