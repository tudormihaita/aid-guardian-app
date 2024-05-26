import React, {createContext, useContext, useState} from "react";

export const DataContext = createContext();
export const useData = () => useContext(DataContext);

export const UserProvider = ({children}) => {
    const [user, setUser] = useState(null);
    const [profile, setProfile] = useState(null);

    return(
        <DataContext.Provider value={{
            user,
            setUser,
            profile,
            setProfile
        }}>
            {children}
        </DataContext.Provider>
    );
}