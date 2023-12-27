import { useState, useEffect } from 'react'
import {
  Routes,
  Route,
  useNavigationType,
  useLocation,
} from "react-router-dom";
//import reactLogo from './assets/react.svg'
//import viteLogo from '/vite.svg'
import './App.css'
import Gallery from './components/Gallery'
//import Generation from './components/Generation'
import Home from "./pages/Home";
import SelectedImage from './pages/SelectedImage';
import Cart from './pages/Cart';
import Checkout from './pages/Checkout'

function App() {
  const action = useNavigationType();
  const location = useLocation();
  const pathname = location.pathname;

  useEffect(() => {
    if (action !== "POP") {
      window.scrollTo(0, 0);
    }
  }, [action, pathname]);

  useEffect(() => {
    let title = "";
    let metaDescription = "";

    switch (pathname) {
      case "/":
        title = "Printsy";
        metaDescription = "";
        break;
      case "/cart":
        title = "";
        metaDescription = "";
        break;
      case "/selected-image":
        title = "";
        metaDescription = "";
        break;
      case "/checkout":
        title = "";
        metaDescription = "";
        break;
    }

    if (title) {
      document.title = title;
    }

    if (metaDescription) {
      const metaDescriptionTag = document.querySelector(
        'head > meta[name="description"]'
      );
      if (metaDescriptionTag) {
        metaDescriptionTag.content = metaDescription;
      }
    }
  }, [pathname]);

  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/cart" element={<Cart />} />
      <Route path="/selected-image" element={<SelectedImage />} />
      <Route path="/checkout" element={<Checkout />} />
    </Routes>
  );
}
export default App;
