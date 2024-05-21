import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import HomePage from "./HomePage";
import LoginPage from "./LoginPage";
import SignUpPage from "./SignUpPage";
import ProfilePage from "./ProfilePage";
import EmergencyPage from "./EmergencyPage";
import './App.css';
import {AuthProvider, UserProvider} from "./Contexts";
import PrivateRoute from "./PrivateRoute";

const App = () => {
        return (
            <div className="App">
                <AuthProvider>
                    <UserProvider>
                <Router>
                    <Routes>
                        <Route path="/" element={<HomePage/>}/>
                        <Route path="/login" element={<LoginPage/>}/>
                        <Route path="/signup" element={<SignUpPage/>}/>
                        <Route path="/profile" element={
                            <PrivateRoute>
                                <ProfilePage />
                            </PrivateRoute> }/>
                        <Route path="/report-emergency" element={
                            <PrivateRoute>
                                <EmergencyPage />
                            </PrivateRoute> }/>
                        {/*<Route path="/profile" element={<ProfilePage/>}/>*/}
                        {/*<Route path="/report-emergency" element={<EmergencyPage/>}/>*/}
                    </Routes>
                </Router>
                    </UserProvider>
                </AuthProvider>
            </div>
        );
}

export default App;
