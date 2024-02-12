// App.jsx
import React from 'react';
import Sidebar from './Components/Sidebar/Sidebar';
import FindingsTable from './Components/FindingsTable/FindingsTable';
import './App.css';

const App = () => {
  return (
    <div className="app-container">
      <div className="sidebar">
        <Sidebar />
      </div>
      <div className="content-container">
        <h1>Findings Page</h1>
        <FindingsTable />
      </div>
    </div>
  );
};

export default App;
