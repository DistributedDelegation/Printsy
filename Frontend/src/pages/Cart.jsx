import { useState, useEffect } from 'react';
import { useNavigate } from "react-router-dom";
import useMatchHeight from '../components/useMatchHeight';
import CheckoutProducts from '../components/CheckoutProducts';
import "./Secondary.css";
import Timer from '../components/Timer';

const Cart = () => {
  const navigate = useNavigate();
  const { generateRef } = useMatchHeight('generateRef');
  const [initialTime, setInitialTime] = useState(0);
  const [showTimer, setShowTimer] = useState(false);

   // ------ Timer ------
   const fetchRemainingTime = async (userId) => {
    const response = await fetch('http://localhost:8080/cart/graphql', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ 
          query: `query($userId: ID!) { getRemainingCleanupTime(userId: $userId) }`,
          variables: { userId: 1 } 
      }),
    });
    const data = await response.json();
    console.log(data);
    const time = data.data.getRemainingCleanupTime;
    setInitialTime(time);
    setShowTimer(time > 0);
};


  const handleTimerEnd = () => {
    // Logic when timer ends
    setShowTimer(false);
    fetchRemainingTime(1).then(() => {
      window.location.reload();
    });
  };

  useEffect(() => {
    fetchRemainingTime(1);
  }, []);

  const handleNavigateCheckout = () => {
    navigate('/Checkout');
  };

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
            <div className="content-right">
              <div className='checkout-title-and-timer'>
                <h2>To Checkout</h2>
                {showTimer && <Timer initialTime={initialTime} onTimerEnd={handleTimerEnd} />}
              </div>
              <div className="delivery-address">
                <p> </p>
              </div>
              <button className="purchase-button" onClick={handleNavigateCheckout}>Proceed with Purchase</button>
              
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Cart;
