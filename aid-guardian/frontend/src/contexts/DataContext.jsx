import {createContext, useContext, useState} from "react";

export const DataContext = createContext();
export const useData = () => useContext(DataContext);

export const DataProvider = ({children}) => {
    const [user, setUser] = useState(localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : null);
    const [profile, setProfile] = useState(localStorage.getItem('profile') ? JSON.parse(localStorage.getItem('profile')) : null);
    const [isOnDuty, setIsOnDuty] = useState(!!(user && user.onDuty));

    return(
        <DataContext.Provider value={{
            user,
            setUser,
            profile,
            setProfile,
            isOnDuty,
            setIsOnDuty
        }}>
            {children}
        </DataContext.Provider>
    );
}