import React, {createContext, useContext, useEffect, useState} from "react";
import io from "socket.io-client";

export const AuthContext = createContext();
export const useAuth = () => useContext(AuthContext);
export const UserContext = createContext();
export const useUserData = () => useContext(UserContext);
export const SocketContext = createContext();
export const useSocket = () => useContext(SocketContext);

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

export const SocketProvider = ({children}) => {
    const [socket, setSocket] = useState(null);
    const [isConnected, setIsConnected] = useState(false);

    useEffect(() => {
        if (!socket) {
            const URL = 'http://localhost:4040';
            const socket = io(URL, {
                autoConnect: false,
                transports: ['websocket', 'polling', 'flashsocket']
            });

            const onConnect = () => {
                setIsConnected(true);
            }

            const onDisconnect = () => {
                setIsConnected(false);
            }

            socket.on('connect', onConnect);
            socket.on('disconnect', onDisconnect);
            setSocket(socket);

            return () => {
                socket.off('connect', onConnect);
                socket.off('disconnect', onDisconnect);
            };
        }
    }, [socket]);

    const reportEmergency = (emergencyData) => {
        if (socket) {
            socket.emit('report-emergency', emergencyData);
        }
        else {
            console.error('Socket is not initialized.');
        }
    }

    const subscribeToEmergencyReported = (callback) => {
        if (socket) {
            socket.on('emergency-reported', callback);
        }
    }

    const initializeConnection = () => {
        if( socket && !socket.connected) {
            socket.connect();
        }
    };

    return (
        <SocketContext.Provider value={{
            socket,
            isConnected,
            initializeConnection,
            reportEmergency,
            subscribeToEmergencyReported
        }}>
            {children}
        </SocketContext.Provider>
    );
};