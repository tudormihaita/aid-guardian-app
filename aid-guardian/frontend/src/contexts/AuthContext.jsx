import {createContext, useContext, useEffect, useMemo, useState} from "react";
import axios from "axios";

export const AuthContext = createContext();
export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({children}) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [token, setToken_] = useState(sessionStorage.getItem("token"));

    const setToken = (newToken) => {
        setToken_(newToken);
    }

    useEffect(() => {
        if(token) {
            setIsAuthenticated(true);
            axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
            sessionStorage.setItem('token', token);
        }
        else {
            setIsAuthenticated(false);
            delete axios.defaults.headers.common['Authorization'];
            sessionStorage.removeItem('token');
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