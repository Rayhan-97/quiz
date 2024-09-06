import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import ThemeWrapper from './components/ThemeWrapper';
import { AuthProvider } from './context/AuthContext';
import './index.css';
import reportWebVitals from './reportWebVitals';
import { ThemeProvider } from './context/ThemeContext';


const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

const queryClient = new QueryClient();
root.render(
  <React.StrictMode>
    <ThemeProvider>
      <AuthProvider>
        <QueryClientProvider client={queryClient}>
          <ThemeWrapper>
            <App />
          </ThemeWrapper>
        </QueryClientProvider>
      </AuthProvider>
    </ThemeProvider>
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
