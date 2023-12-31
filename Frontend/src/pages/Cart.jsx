import { useState, useEffect } from 'react';
import { useNavigate } from "react-router-dom";
import useMatchHeight from '../components/useMatchHeight';
import CheckoutProducts from '../components/CheckoutProducts';
import "./Secondary.css";
import Timer from '../components/Timer';

const Home = () => {
  const navigate = useNavigate();
  const { generateRef } = useMatchHeight('generateRef');
  const [signInEmail, setSignInEmail] = useState('');
  const [signInPassword, setSignInPassword] = useState('');
  const [signUpEmail, setSignUpEmail] = useState('');
  const [signUpPassword, setSignUpPassword] = useState('');
  const [isSigningIn, setIsSigningIn] = useState(false);
  const [isSigningUp, setIsSigningUp] = useState(false);
  const [initialTime, setInitialTime] = useState(0);
  const [showTimer, setShowTimer] = useState(false);

  const handleSignIn = async () => {
    setIsSigningIn(true);

    const query = `
        mutation($input: UserCredentialInput!) {
          authenticate(userCredentialInput: $input)
        }
      `;

    const variables = {
      input: {
        emailAddress: email,
        password: password,
      },
    };

    try {
      const response = await fetch('http://localhost:8085/graphql', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ query, variables }),
      });
      const data = await response.json();
      // Handle the JWT token received from the API
      // For example, storing it in localStorage/sessionStorage
      setIsSigningIn(false);
    } catch (error) {
      // Handle error
      console.error('Error during sign in:', error);
      setIsSigningIn(false);
    }
  };

  const handleSignUp = async () => {
    setIsSigningUp(true);
    const query = `
        mutation($input: UserCredentialInput!) {
          register(userCredentialInput: $input)
        }
      `;

    const variables = {
      input: {
        emailAddress: signUpEmail,
        password: signUpPassword,
      },
    };

    try {
      const response = await fetch('http://localhost:8085/graphql', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ query, variables }),
      });
      const data = await response.json();
      // Handle the confirmation message from the API
      setIsSigningUp(false);
    } catch (error) {
      // Handle error
      console.error('Error during sign up:', error);
      setIsSigningUp(false);
    }
  };

   // ------ Timer ------
   const fetchRemainingTime = async () => {
    const response = await fetch('http://localhost:8086/graphql', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ query: "{ getRemainingCleanupTime }" }),
    });
    const data = await response.json();
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
              <h2>To Checkout</h2>
              {showTimer && <Timer initialTime={initialTime} onTimerEnd={handleTimerEnd} />}
              <button className="purchase-button" onClick={handleNavigateCheckout}>Proceed with Purchase</button>
              {/* <div className="signIn">
                <p>If you already have an account:</p>
                <input
                  type="email"
                  placeholder="E-Mail Address"
                  className="text-input"
                  value={signInEmail}
                  onChange={(e) => setSignInEmail(e.target.value)}
                  required
                />
                <input
                  type="password"
                  placeholder="Password"
                  className="text-input"
                  value={signInPassword}
                  onChange={(e) => setSignInPassword(e.target.value)}
                  required
                />
                {isSigningIn ? (
                  <button className="primary-button" disabled>Loading..</button>
                ) : (
                  <button className="primary-button" onClick={handleSignIn}>Sign-In</button>
                )}
              </div>
              <div className="divider"></div>
              <div className="signUp">
                <p>If you are new here:</p>
                <input
                  type="email"
                  placeholder="E-Mail Address"
                  className="text-input"
                  value={signUpEmail}
                  onChange={(e) => setSignUpEmail(e.target.value)}
                  required
                />
                <input
                  type="password"
                  placeholder="Password"
                  className="text-input"
                  value={signUpPassword}
                  onChange={(e) => setSignUpPassword(e.target.value)}
                  required
                />
                {isSigningUp ? (
                  <button className="secondary-button" disabled>Loading..</button>
                ) : (
                  <button className="secondary-button" onClick={handleSignUp}>Sign-Up</button>
                )} 
              </div> */}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;