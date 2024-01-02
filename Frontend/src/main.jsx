import React from 'react'
//import ReactDOM from 'react-dom/client'
import App from './App.jsx'
//import './index.css'
import { BrowserRouter } from "react-router-dom";
import { createRoot } from "react-dom/client";
import { AuthProvider } from './AuthContext';


const container = document.getElementById("root");
const root = createRoot(container);

root.render(
    <BrowserRouter>
      <AuthProvider>
        <App />
      </AuthProvider>
    </BrowserRouter>
);
