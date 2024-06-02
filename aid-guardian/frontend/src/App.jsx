import './App.css'
import {AuthProvider} from "./contexts/AuthContext.jsx";
import {DataProvider} from "./contexts/DataContext.jsx";
import {SocketProvider} from "./contexts/ConnectionContext.jsx";
import {BrowserRouter, Route,  Routes} from "react-router-dom";
import ProtectedRoute from "./components/ProtectedRoute.jsx";
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

                                <Route path="/profile" element={ <ProtectedRoute> <ProfilePage /> </ProtectedRoute> }/>
                                <Route path="/report-emergency" element={ <ProtectedRoute> <EmergencyPage /> </ProtectedRoute> }/>

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
