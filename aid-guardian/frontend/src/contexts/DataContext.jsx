import {createContext, useContext, useState} from "react";

export const DataContext = createContext();
export const useData = () => useContext(DataContext);

export const DataProvider = ({children}) => {
    const getUserFromLocalStorage = () => {
        try {
            const user = localStorage.getItem('user');
            return user ? JSON.parse(user) : null;
        } catch (error) {
            console.error("Error parsing user from localStorage:", error);
            return null;
        }
    };

    const getProfileFromLocalStorage = () => {
        try {
            const profile = localStorage.getItem('profile');
            return profile ? JSON.parse(profile) : null;
        } catch (error) {
            console.error("Error parsing profile from localStorage:", error);
            return null;
        }
    };

    const [user, setUser] = useState(getUserFromLocalStorage());
    const [profile, setProfile] = useState(getProfileFromLocalStorage());
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