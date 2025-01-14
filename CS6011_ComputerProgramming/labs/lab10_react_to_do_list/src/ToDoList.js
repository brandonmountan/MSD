import React from 'react';

function ToDoList({ items, deleteItem }) {
  return (
    <div>
      {items.map((item, index) => (
        <div className="item" key={index} onDoubleClick={() => deleteItem(index)}>
          {item}
        </div>
      ))}
    </div>
  );
}

export default ToDoList;
