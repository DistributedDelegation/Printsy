import { useState } from 'react';
import { useNavigate } from "react-router-dom";
import useMatchHeight from '../components/useMatchHeight';
import "./Secondary.css";
import Cookies from 'js-cookie';

const SignIn = () => {
  const navigate = useNavigate();
  const { generateRef } = useMatchHeight('generateRef');
  const [signInEmail, setSignInEmail] = useState('');
  const [signInPassword, setSignInPassword] = useState('');
  const [isSigningIn, setIsSigningIn] = useState(false);

  const handleSignupClick = () => {
    navigate('/signup');
  };

  const handleSignIn = async () => {
    const signInUsingAccount = async () => {
      setIsSigningIn(true);

      const query = `
          mutation($input: UserCredentialInput!) {
            authenticate(userCredentialInput: $input)
          }
        `;

      const variables = {
        input: {
          emailAddress: signInEmail,
          password: signInPassword,
        },
      };

      try {
        const response = await fetch('http://localhost:8080/auth/graphql', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ query, variables }),
        });
        const data = await response.json();
        const token = data.data.authenticate;
        localStorage.setItem('user', token);
        Cookies.set('user', token, { expires: 7, secure: true }); // Store the cookies
        console.log("saved cookies when signin in", Cookies.get('user'));
        setIsSigningIn(false);

        setTimeout(() => {
          // get the redirect path stored in session storage
          const redirectPath = sessionStorage.getItem('redirectAfterLogin');
          // if it exists, navigate to it, otherwise navigate to '/generation'
          navigate(redirectPath || '/generation');
          // then remove the redirect path from session storage
          sessionStorage.removeItem('redirectAfterLogin');
        }, 1000);

      } catch (error) {
        // Handle error
        console.error('Error during sign in:', error);
        setIsSigningIn(false);
      }
    }

    const token = localStorage.getItem('user');
      if (!token) {
          signInUsingAccount();
      }
  };

  return (
    <div id="gridTemplate">
      <div id="bodySecondary" ref={generateRef}>
        <div className="header">
          <a href="/">
            <img src="/images/printsyLogo.svg" alt="Logo" className="logo" />
          </a>
          <div className="content content-secondary">
            <div className="content-right">
              <h2>To Checkout</h2>
              <div className="signIn">
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
                <p style={{paddingTop: "20px", color: "red"}}> 🗝️ Don't have an account?
                  <span onClick={handleSignupClick}>Register now</span>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SignIn;