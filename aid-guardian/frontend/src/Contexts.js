import React, {createContext, useState} from "react";

export const AuthContext = createContext();
export const UserContext = createContext();

export const AuthProvider = ({children}) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [username, setUsername] = useState('');
    const [target, setTarget] = useState('');
    const [messages, setMessages] = useState([]);
    const [accessToken, setAccessToken] = useState('');

    return(
        <AuthContext.Provider value={{
            isAuthenticated,
            setIsAuthenticated,
            username,
            setUsername,
            target,
            setTarget,
            messages,
            setMessages,
            accessToken,
            setAccessToken
        }}>
            {children}
        </AuthContext.Provider>
    );
}

export const UserProvider = ({children}) => {
    const [user, setUser] = useState(null);
    const [profile, setProfile] = useState(null);

    return(
        <UserContext.Provider value={{
            user,
            setUser,
            profile,
            setProfile
        }}>
            {children}
        </UserContext.Provider>
    );
}