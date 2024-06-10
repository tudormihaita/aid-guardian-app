import {createContext, useContext, useEffect, useMemo, useState} from "react";
import axios from "axios";

export const AuthContext = createContext();
export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({children}) => {
    const [token, setToken_] = useState(localStorage.getItem('token'));
    const isAuthenticated = !!token;

    const setToken = (newToken) => {
        setToken_(newToken);
    }

    useEffect(() => {
        if(token) {
            axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
            localStorage.setItem('token', token);
        }
        else {
            delete axios.defaults.headers.common['Authorization'];
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            localStorage.removeItem('profile');
        }
    }, [token]);

    const contextValue = useMemo(() => ({
        token,
        isAuthenticated,
        setToken,
    }), [isAuthenticated, token]);

    return(
        <AuthContext.Provider value={contextValue}>
            {children}
        </AuthContext.Provider>
    );
}