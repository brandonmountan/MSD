import React, { useState } from 'react';
import ToDoList from './ToDoList';
import InputArea from './InputArea';
import './index.css'; // Import the CSS file

function App() {
  const [items, setItems] = useState([]);

  const addItem = (item) => {
    setItems([...items, item]);
  };

  const deleteItem = (index) => {
    const newItems = items.filter((_, i) => i !== index);
    setItems(newItems);
  };

  const testSomething = () => {
    addItem("Test item");
  };

  return (
    <div className="App">
      <h1 className="lightText">To-Do List</h1>
      <div id="toDoListDiv">
        <ToDoList items={items} deleteItem={deleteItem} />
      </div>
      <div id="inputDiv">
        <InputArea addItem={addItem} />
        <button onClick={testSomething}>Test</button>
      </div>
    </div>
  );
}

export default App;
