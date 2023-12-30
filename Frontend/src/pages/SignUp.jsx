import { useState } from 'react';
import { useNavigate } from "react-router-dom";
import useMatchHeight from '../components/useMatchHeight';
import "./Secondary.css";

const SignUp = () => {
  const navigate = useNavigate();
  const { generateRef } = useMatchHeight('generateRef');
  const [signUpEmail, setSignUpEmail] = useState('');
  const [signUpPassword, setSignUpPassword] = useState('');
  const [isSigningUp, setIsSigningUp] = useState(false);

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
      const data = response.data;
      setSignUpEmail('');  // reset email
      setSignUpPassword('');  // reset password
      setIsSigningUp(false);
      setTimeout(() => {
        navigate('/login');
        }, 3000);
      // Handle the confirmation message from the API
    } catch (error) {
      // Handle error
      console.error('Error during sign up:', error);
      setIsSigningUp(false);
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
                  <button className="secondary-button" disabled>Loading...</button>
                ) : (
                  <button className="secondary-button" onClick={handleSignUp}>Sign-Up</button>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SignUp;