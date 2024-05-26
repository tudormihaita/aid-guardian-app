import './App.css'
import {AuthProvider} from "./contexts/AuthContext.jsx";
import {DataProvider} from "./contexts/DataContext.jsx";
import {SocketProvider} from "./contexts/ConnectionContext.jsx";
import {BrowserRouter, Route,  Routes} from "react-router-dom";
import PrivateRoute from "./components/PrivateRoute.jsx";
import HomePage from "./pages/Home/HomePage.jsx";
import LoginPage from "./pages/Login/LoginPage.jsx";
import SignUpPage from "./pages/Register/SignUpPage.jsx";
import ProfilePage from "./pages/Profile/ProfilePage.jsx";
import EmergencyPage from "./pages/Emergency/EmergencyPage.jsx";

function App() {
    return (
        <div className="App">
            <AuthProvider>
                <DataProvider>
                    <SocketProvider>
                        <BrowserRouter>
                            <Routes>
                                <Route path="/" element={<HomePage/>}/>
                                <Route path="/login" element={<LoginPage/>}/>
                                <Route path="/signup" element={<SignUpPage/>}/>
                                <Route path="/profile" element={ <PrivateRoute> <ProfilePage /> </PrivateRoute> }/>
                                <Route path="/report-emergency" element={ <PrivateRoute> <EmergencyPage /> </PrivateRoute> }/>

                                {/*<Route path="/profile" element={<ProfilePage/>}/>*/}
                                {/*<Route path="/report-emergency" element={<EmergencyPage/>}/>*/}
                            </Routes>
                        </BrowserRouter>
                    </SocketProvider>
                </DataProvider>
            </AuthProvider>
        </div>
    );
}

export default App;
