import React, { useState } from 'react';
import LoginPage from './LoginPage';
import ChatPage from './ChatPage';

const App = () => {
    const [userInfo, setUserInfo] = useState(null);

    return (
        <div>
            {userInfo ? (
                <ChatPage userInfo={userInfo} onLogout={() => setUserInfo(null)} />
            ) : (
                <LoginPage onLogin={setUserInfo} />
            )}
        </div>
    );
};

export default App;
