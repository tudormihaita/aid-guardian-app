import {createContext, useContext, useState} from "react";

export const DataContext = createContext();
export const useData = () => useContext(DataContext);

export const DataProvider = ({children}) => {
    const [user, setUser] = useState(null);
    const [profile, setProfile] = useState(null);
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