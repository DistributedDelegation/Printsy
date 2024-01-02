import { useState, useEffect } from 'react';
import { useNavigate } from "react-router-dom";
import useMatchHeight from '../components/useMatchHeight';
import "./Secondary.css";
import CheckoutProducts from '../components/CheckoutProducts';
import Timer from '../components/Timer';

const Home = () => {
  const navigate = useNavigate();
  const { generateRef } = useMatchHeight('generateRef');
  const [deliveryName, setDeliveryName] = useState('');
  const [deliveryAddress, setDeliveryAddress] = useState('');
  const [deliveryEirCode, setDeliveryEirCode] = useState('');
  const [initialTime, setInitialTime] = useState(0);
  const [showTimer, setShowTimer] = useState(false);
  const [showPopup, setShowPopup] = useState(false);
  const [popupMessage, setPopupMessage] = useState('');

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
    fetchRemainingTime().then(() => {
      window.location.reload();
    });
  };

  useEffect(() => {
    fetchRemainingTime();
  }, []);

  const closePopup = () => {
    setShowPopup(false);
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
              <h2>Selected Products</h2>
              <div className="divider"></div>
              <CheckoutProducts/>
            </div>
            <div className="content-right">
            <div className='checkout-title-and-timer'>
              <h2>Checkout</h2>
              {showTimer && <Timer initialTime={initialTime} onTimerEnd={handleTimerEnd} />}
              </div>
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
              <button className="purchase-button" /*onClick={handlePurchase}*/>Complete Purchase</button>
              
            </div>

            {showPopup && (
              <div className="popup">
                  <p className="popup-text">{popupMessage}</p>
                  <button onClick={closePopup}>Close</button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;
