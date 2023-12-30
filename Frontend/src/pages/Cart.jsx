import { useState } from 'react';
import { useNavigate } from "react-router-dom";
import useMatchHeight from '../components/useMatchHeight';
import CheckoutProducts from '../components/CheckoutProducts';
import "./Secondary.css";

const Cart = () => {
  const navigate = useNavigate();
  const { generateRef } = useMatchHeight('generateRef');

  return (
    <div id="gridTemplate">
      <div id="bodySecondary" ref={generateRef}>
        <div className="header">
          <a href="/">
            <img src="/images/printsyLogo.svg" alt="Logo" className="logo" />
          </a>
          <div className="content content-secondary">
            <div className="content-left">
              <h2>Cart</h2>
              <div className="divider"></div>
              <CheckoutProducts />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Cart;
