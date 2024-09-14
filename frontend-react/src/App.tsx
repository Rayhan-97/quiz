import { BrowserRouter, Route, Routes } from 'react-router-dom';
import './App.css';
import Authenticated from './components/Authenticated';
import Layout from './components/Layout';
import Login from './components/Login';
import Register from './components/Register';
import Home from './components/Home';

function App() {
  return (
    <>
      <BrowserRouter>
        <Routes>

          <Route path={'/'} element={<Layout />} >
            <Route path={'/'} element={<Home />} />
            <Route path={'/register'} element={<Register />} />
            <Route path={'/login'} element={<Login />} />

            <Route path={'/'} element={<Authenticated />} >
              <Route path={'make'} element={<Make />} />
            </Route>
          </Route>

          <Route path={'*'} element={<NotFound />} />

        </Routes>
      </BrowserRouter>
    </>
  );
}



const NotFound = () => {
  return (
    <>
      <div>Not found page! Welcome! I guess</div>
    </>
  );
};

const Make = () => <div>Welcome, Logged in user, welcome!</div>;

export default App;
