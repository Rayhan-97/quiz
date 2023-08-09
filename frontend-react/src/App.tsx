import React from 'react';
import logo from './logo.svg';
import './App.css';
import Register from './components/register';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Layout from './components/layout';

function App() {
  return (
    <>
      {/* <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.tsx</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div> */}
      <BrowserRouter>
        <Routes>
          
          <Route path="/" element={<Layout />} >
            <Route path="/" element={<Register />} />
            <Route path="/register" element={<Register />} />
            <Route path="/login" element={<Login />} />
          </Route>
          
          <Route path="*" element={<NotFound />} />

        </Routes>
      </BrowserRouter>
    </>
  );
}

const Login = () => {
  return (
    <>
      <div>Login page! Welcome!</div>
    </>
  )
}

const NotFound = () => {
  return (
    <>
      <div>Not found page! Welcome! I guess</div>
    </>
  )
}

export default App;
