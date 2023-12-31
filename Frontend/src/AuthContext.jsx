import React, { createContext, useState, useEffect} from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
// import Cookies from 'js-cookie';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [userID, setUserID] = useState("");
    const [token, setToken] = useState(""); //const a token 
    const navigate = useNavigate(); 
    const location = useLocation();
    let authenticationGraphqlEndpoint = "http://localhost:8085/graphql";

    const login = () => {
        setIsAuthenticated(true);
        navigate('/generation');
    };

    const logout=() =>{
        setIsAuthenticated(false);
        setUserID("");
        setToken("");
        localStorage.removeItem('user');
        navigate('/sigin');
    }

    useEffect(() => {
        if (localStorage.getItem('user')) {
            //authorization is true
            const bearerToken = localStorage.getItem('user');
            setToken(bearerToken);
            console.log("bearerToken", bearerToken);
            
            const query = JSON.stringify({
                query: `query(
                  $bearerToken: String!
                  ) {
                    currentUser(bearerToken: $bearerToken) {
                        userID
                        emailAddress
                    }
                }`,
                variables: {
                    bearerToken: bearerToken,
                }
            });
            console.log("query: ", query);
      
            fetch(authenticationGraphqlEndpoint, {
                method: 'POST',
                headers: {
                  'Content-Type': 'application/json'
                },
                body: query
            })
            .then(response => response.json()) // Convert the response to JSON
            .then(data => {
                console.log("data:", data);
                setIsAuthenticated(true);
                setUserID(data.currentUser); // Access the userID from the data
            })
            .catch(error => {
                console.log("error:", error);
                // check if it's an expiry error
                if (error.response && error.response.status === 400) {
                    setIsAuthenticated(false);
                    localStorage.removeItem('user'); 
                    setToken("");
                    alert("Your session has expired. Please log in again."); // Notify user
                    // save the current path to redirect back to it after login
                    sessionStorage.setItem('redirectAfterLogin', location.pathname);
                    navigate('/signin'); // Navigate to login page
                } else {
                    console.log(error);
                }
            });
        }
    }, [navigate,location]); // <-- Add navigate as a dependency

    useEffect(() => {
        // log the updated userID value
        // userID.json().then(data => console.log(data));
    }, [userID]);    

    return (
        <AuthContext.Provider value={{ isAuthenticated, login , logout, userID, token}}>
            {children}
        </AuthContext.Provider>
    );
};
