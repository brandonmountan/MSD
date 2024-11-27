import React, { useRef } from 'react';

function InputArea({ addItem }) {
  const inputTextArea = useRef();

  const handleAddItem = () => {
    const itemText = inputTextArea.current.value.trim();
    if (itemText) {
      addItem(itemText);
      inputTextArea.current.value = ''; // Clear the input after adding
    }
  };

  return (
    <div>
      <textarea ref={inputTextArea} placeholder="Add a new item..." />
      <button onClick={handleAddItem}>Add</button>
    </div>
  );
}

export default InputArea;
