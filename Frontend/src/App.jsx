import { useState, useEffect, useContext } from "react";
import {
  Routes,
  Route,
  useNavigationType,
  useLocation,
} from "react-router-dom";
//import reactLogo from './assets/react.svg'
//import viteLogo from '/vite.svg'
import "./App.css";
import Gallery from "./components/Gallery";
//import Generation from './components/Generation'
import { AuthContext } from "./AuthContext";
import Generation from "./pages/Generation";
import SelectedImage from "./pages/SelectedImage";
import SignIn from "./pages/SignIn";
import SignUp from "./pages/SignUp";
import CartAndCheckout from "./pages/CartAndCheckout";

function App() {
  const action = useNavigationType();
  const location = useLocation();
  const pathname = location.pathname;
  const authContext = useContext(AuthContext);

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
      case "/generation":
        title = "Printsy - New Image";
        metaDescription = "";
        break;
      case "/cart":
        title = "Printsy - Cart";
        metaDescription = "";
        break;
      case "/selected-image":
        title = "Printsy";
        metaDescription = "";
        break;
      case "/signin":
        title = "Printsy - Sign in";
        metaDescription = "";
        break;
      case "/signup":
        title = "Printsy - Sign Up";
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
      {authContext.isAuthenticated ? (
        <Route path="/" element={<Generation />} />
      ) : (
        <Route path="/" element={<SignIn />} />
      )}
      <Route path="/generation" element={<Generation />} />
      <Route path="/cart" element={<CartAndCheckout />} />
      <Route path="/selected-image" element={<SelectedImage />} />
      <Route path="/signin" element={<SignIn />} />
      <Route path="/signup" element={<SignUp />} />
    </Routes>
  );
}
export default App;
