import React, { useEffect, useState } from 'react';

function App() {
  const [message, setMessage] = useState('');

  useEffect(() => {
    fetch('http://localhost:8080/api/hello')
        .then(res => res.text())
        .then(setMessage)
        .catch(err => console.error('Error fetching:', err));
  }, []);

  return (
      <div style={{ padding: '2rem' }}>
        <h1>{message || 'Loading...'}</h1>
      </div>
  );
}

export default App;
