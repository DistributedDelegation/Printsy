import { useState, useEffect, useContext } from "react";
import { useNavigate } from "react-router-dom";
import useMatchHeight from "../components/useMatchHeight";
import CheckoutProducts from "../components/CheckoutProducts";
import "./Secondary.css";
import Timer from "../components/Timer";
import { AuthContext } from "../AuthContext";

const CartAndCheckout = () => {
  const navigate = useNavigate();
  const { generateRef } = useMatchHeight("generateRef");
  const authContext = useContext(AuthContext);
  const userId = authContext.userID;

  // States
  const [initialTime, setInitialTime] = useState(0);
  const [showTimer, setShowTimer] = useState(true);
  const [isCheckout, setIsCheckout] = useState(false); // State to toggle views
  const [refreshKey, setRefreshKey] = useState(0); // Key to refresh products
  const [deliveryName, setDeliveryName] = useState("");
  const [deliveryAddress, setDeliveryAddress] = useState("");
  const [deliveryEirCode, setDeliveryEirCode] = useState("");
  const [showPopup, setShowPopup] = useState(false);
  const [popupMessage, setPopupMessage] = useState("");

  // Fetch timer for the Cart
  const fetchRemainingTime = async (userId) => {
    const response = await fetch("http://localhost:8080/cart/graphql", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        query: `query($userId: ID!) { getRemainingCleanupTime(userId: $userId) }`,
        variables: { userId: userId },
      }),
    });
    const data = await response.json();
    const time = data.data.getRemainingCleanupTime;
    setInitialTime(time);
    setShowTimer(time > 0);
  };

  // Timer end logic
  const handleTimerEnd = () => {
    setShowTimer(false);
    fetchRemainingTime(userId).then(() => {
      setRefreshKey((oldKey) => oldKey + 1);
    });
  };

  // Effects
  useEffect(() => {
    fetchRemainingTime(userId);
  }, []);

  // Toggle to Checkout view
  const handleNavigateCheckout = () => {
    setIsCheckout(true);
  };

  // Close Popup
  const closePopup = () => {
    setShowPopup(false);
  };

  const handlePurchase = async () => {
    const mutation = `
      mutation {
        completePurchase(userId: "${userId}")
      }
    `;

    try {
      const response = await fetch("http://localhost:8080/cart/graphql", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          query: mutation,
        }),
      });
      const data = await response.json();
      if (data.data.completePurchase) {
        console.log("Purchase completed successfully.");
        handleTimerEnd();
        setIsCheckout(false);
        setPopupMessage(
          "Your purchase was successful! Thank you and enjoy your artworks!"
        );
        setShowPopup(true);
      } else {
        console.log("Purchase was not completed.");
        setPopupMessage(
          "Your purchase was not successful. Try again or contact us."
        );
        setShowPopup(true);
      }
    } catch (error) {
      console.error("Error completing purchase:", error);
      setPopupMessage(
        "Your purchase was not successful. Try again or contact us."
      );
      setShowPopup(true);
    }
  };

  // Return Conditionally Based on isCheckout state
  return (
    <div id="gridTemplate">
      <div id="bodySecondary" ref={generateRef}>
        <div className="header">
          <a href="/">
            <img src="/images/printsyLogo.svg" alt="Logo" className="logo" />
          </a>
          <div className="content content-secondary">
            <div className="content-left">
              <h2>{isCheckout ? "Selected Products" : "Cart"}</h2>
              <div className="divider"></div>
              <CheckoutProducts refreshKey={refreshKey} time={initialTime} />
            </div>
            <div className="content-right">
              {isCheckout ? (
                // Checkout View
                <>
                  <div className="checkout-title-and-timer">
                    <h2>Checkout</h2>
                    {showTimer && (
                      <Timer
                        initialTime={initialTime}
                        onTimerEnd={handleTimerEnd}
                      />
                    )}
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
                  <button className="purchase-button" onClick={handlePurchase}>
                    Complete Purchase
                  </button>
                </>
              ) : (
                // Cart View
                <>
                  <div className="checkout-title-and-timer">
                    <h2>To Checkout</h2>
                    {showTimer && (
                      <Timer
                        initialTime={initialTime}
                        onTimerEnd={handleTimerEnd}
                      />
                    )}
                  </div>
                  <div className="delivery-address">
                    <p> </p>
                  </div>
                  <button
                    className="purchase-button"
                    onClick={handleNavigateCheckout}
                  >
                    Proceed with Purchase
                  </button>
                </>
              )}
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

export default CartAndCheckout;
