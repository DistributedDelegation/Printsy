import React, { createContext, useState, useEffect} from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
// import Cookies from 'js-cookie';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [userID, setUserID] = useState(null);
    const [token, setToken] = useState(null); //const a token 
    const navigate = useNavigate(); 
    const location = useLocation();

    const login = () => {
        setIsAuthenticated(true);
    };

    const logout=() =>{
        setIsAuthenticated(false);
        setUserID(null);
        setToken(null);
        localStorage.removeItem('user');
        navigate('/');
    }

    useEffect(() => {
        if (localStorage.getItem('user')) {
            //authorization is true
            const user = localStorage.getItem('user');
            setToken(user); //
            axios.get(process.env.REACT_APP_BACKEND_URL + '/auth/current-user', {
                headers: {
                    'Authorization': `Bearer ${user}`,
                }
            })
            .then(respond => {
                // console.log(respond.data.userID);
                setIsAuthenticated(true);
                setUserID(respond.data.userID);
            })
            .catch(error => {
                console.log("error:", error);
                // check if it's an expiry error
                if (error.response && error.response.status === 400) {
                    setIsAuthenticated(false);
                    localStorage.removeItem('user'); 
                    setToken(null);
                    alert("Your session has expired. Please log in again."); // Notify user
                    // save the current path to redirect back to it after login
                    sessionStorage.setItem('redirectAfterLogin', location.pathname);
                    navigate('/login'); // Navigate to login page
                } else {
                    console.log(error);
                }
            });
        }
    }, [navigate,location]); // <-- Add navigate as a dependency

    useEffect(() => {
        // console.log(userID);  //log the updated userID value
    }, [userID]);
    

    return (
        <AuthContext.Provider value={{ isAuthenticated, login , logout, userID, token}}>
            {children}
        </AuthContext.Provider>
    );
};
