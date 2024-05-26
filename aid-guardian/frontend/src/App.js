import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import './App.css';
import HomePage from "./pages/Home/HomePage";
import LoginPage from "./pages/Login/LoginPage";
import SignUpPage from "./pages/SignUp/SignUpPage";
import EmergencyPage from "./pages/Emergency/EmergencyPage";
import ProfilePage from "./pages/Profile/ProfilePage";
import PrivateRoute from "./components/PrivateRoute";
import {AuthProvider} from "./contexts/AuthContext";
import {UserProvider} from "./contexts/DataContext";
import {SocketProvider} from "./contexts/ConnectionContext";

const App = () => {
        return (
            <div className="App">
                <AuthProvider>
                    <UserProvider>
                        <SocketProvider>
                <Router>
                    <Routes>
                        <Route path="/" element={<HomePage/>}/>
                        <Route path="/login" element={<LoginPage/>}/>
                        <Route path="/signup" element={<SignUpPage/>}/>
                        <Route path="/profile" element={ <PrivateRoute> <ProfilePage /> </PrivateRoute> }/>
                        <Route path="/report-emergency" element={ <PrivateRoute> <EmergencyPage /> </PrivateRoute> }/>

                        {/*<Route path="/profile" element={<ProfilePage/>}/>*/}
                        {/*<Route path="/report-emergency" element={<EmergencyPage/>}/>*/}
                    </Routes>
                </Router>
                        </SocketProvider>
                    </UserProvider>
                </AuthProvider>
            </div>
        );
}

export default App;
