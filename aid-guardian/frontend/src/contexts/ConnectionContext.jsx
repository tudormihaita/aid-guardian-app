import {createContext, useContext, useEffect, useState} from "react";
import io from "socket.io-client";

const SERVER_URL = 'http://localhost:4040';
export const SocketContext = createContext();
export const useSocket = () => useContext(SocketContext);

export const SocketProvider = ({children}) => {
    const [socket, setSocket] = useState(null);
    const [isConnected, setIsConnected] = useState(socket ? socket.connected : false);

    const closeConnection = () => {
        if(socket) {
            socket.disconnect();
        }
    };

    useEffect(() => {
        let newSocket;

        if(!socket) {
            newSocket = io(SERVER_URL, {
                autoConnect: false,
                transports: ['websocket', 'polling', 'flashsocket'],
                upgrade: false,
            });


            newSocket.on('connect', () => setIsConnected(true));
            newSocket.on('reconnect', () => setIsConnected(true));
            newSocket.on('disconnect', () => setIsConnected(false));

            setSocket(newSocket);
        }

        return () => {
            closeConnection();
        }
    }, [socket]);

    const reportEmergency = (emergencyData) => {
        if (socket && isConnected) {
            socket.emit('report-emergency', emergencyData);
        }
        else {
            console.error('Unable to emit event, socket is not initialized.');
        }
    }

    const subscribeToEmergencyReported = (callback) => {
        if (socket && isConnected) {
            socket.on('emergency-reported', callback);
        }
        else {
            console.error('Unable to subscribe to event, socket is not initialized.');
        }
    }

    const unsubscribeFromEmergencyReported = () => {
        if (socket && isConnected) {
            socket.off('emergency-reported');
        }
        else {
            console.error('Unable to unsubscribe from event, socket is not initialized.');
        }
    }

    const respondToEmergency = (emergencyData) => {
        if (socket && isConnected) {
            socket.emit('respond-to-emergency', emergencyData);
        }
        else {
            console.error('Unable to emit event, socket is not initialized.');
        }
    }

    const subscribeToEmergencyResponded = (callback) => {
        if (socket && isConnected) {
            socket.on('emergency-responded', callback);
        }
        else {
            console.error('Unable to subscribe to event, socket is not initialized.');
        }
    }

    const unsubscribeFromEmergencyResponded = () => {
        if (socket && isConnected) {
            socket.off('emergency-responded');
        }
        else {
            console.error('Unable to unsubscribe from event, socket is not initialized.');
        }
    }

    const initializeConnection = () => {
        if( socket && !isConnected) {
            socket.connect();
        }
    };

    return (
        <SocketContext.Provider value={{
            socket, isConnected, initializeConnection, closeConnection,
            reportEmergency, subscribeToEmergencyReported, unsubscribeFromEmergencyReported,
            respondToEmergency, subscribeToEmergencyResponded, unsubscribeFromEmergencyResponded
        }}>
            {children}
        </SocketContext.Provider>
    );
};