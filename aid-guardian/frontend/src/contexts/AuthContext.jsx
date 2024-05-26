import {createContext, useContext, useState} from "react";

export const AuthContext = createContext();
export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({children}) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [username, setUsername] = useState('');
    const [accessToken, setAccessToken] = useState('');

    return(
        <AuthContext.Provider value={{
            isAuthenticated,
            setIsAuthenticated,
            username,
            setUsername,
            accessToken,
            setAccessToken
        }}>
            {children}
        </AuthContext.Provider>
    );
}