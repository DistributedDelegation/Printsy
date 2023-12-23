import { useState } from 'react';
import { useNavigate } from "react-router-dom";
import useMatchHeight from '../components/useMatchHeight';
import "./Secondary.css";

const Home = () => {
  const navigate = useNavigate();
  const { generateRef } = useMatchHeight('generateRef');
  const [deliveryName, setDeliveryName] = useState('');
  const [deliveryAddress, setDeliveryAddress] = useState('');
  const [deliveryEirCode, setDeliveryEirCode] = useState('');



  return (
    <div id="gridTemplate">
      <div id="bodySecondary" ref={generateRef}>
        <div className="header">
          <a href="/">
            <img src="/images/printsyLogo.svg" alt="Logo" className="logo" />
          </a>
          <div className="content content-secondary">
            <div className="content-left">
              <h2>Checkout</h2>
              <div className="delivery-address">
                <p>Delivery Address</p>
                <input
                  type="text"
                  placeholder="Name"
                  className="text-input"
                  value={deliveryName}
                  onChange={(e) => setDeliveryName(e.target.value)}
                />
                <input
                  type="text"
                  placeholder="Address"
                  className="text-input"
                  value={deliveryAddress}
                  onChange={(e) => setDeliveryAddress(e.target.value)}
                />
                <input
                  type="text"
                  placeholder="Eir Code"
                  className="text-input"
                  value={deliveryEirCode}
                  onChange={(e) => setDeliveryEirCode(e.target.value)}
                />
              </div>
            </div>
            <div className="content-right">
              <h2>Checkout</h2>
              <div className="divider"></div>
              {/* Add your placeholder elements here */}
              <div className="product-in-cart">
                <span className="product-name">PRODUCT NAME</span>
                <span className="product-option">OPTION</span>
                <span className="product-price">€20</span>
              </div>
              {/* Repeat for each product */}
              <div className="divider"></div>
              <div id="total-cart-container">
                <span id="total-cart">Total</span>
                <span className="total-price">€40</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;
