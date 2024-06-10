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
import SettingsPage from "./pages/Settings/SettingsPage.jsx";
import TrainingPage from "./pages/Training/TrainingPage.jsx";
import BleedingGuide from "./pages/Training/Guides/BleedingGuide.jsx";
import ScorePage from "./pages/Score/ScorePage.jsx";
import HistoryPage from "./pages/History/HistoryPage.jsx";
import ChokingGuide from "./pages/Training/Guides/ChokingGuide.jsx";
import CPRGuide from "./pages/Training/Guides/CPRGuide.jsx";
import BurnsGuide from "./pages/Training/Guides/BurnsGuide.jsx";
import FracturesGuide from "./pages/Training/Guides/FracturesGuide.jsx";

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
                                <Route path="/score" element={ <ProtectedRoute> <ScorePage /> </ProtectedRoute> }/>
                                <Route path="/settings" element={ <ProtectedRoute> <SettingsPage /> </ProtectedRoute> }/>
                                <Route path="/training" element={ <ProtectedRoute> <TrainingPage /> </ProtectedRoute> }/>
                                <Route path="/history" element={ <ProtectedRoute> <HistoryPage /> </ProtectedRoute> }/>

                                <Route path="/training/cpr" element={ <ProtectedRoute> <CPRGuide /> </ProtectedRoute> }/>
                                <Route path="/training/choking" element={ <ProtectedRoute> <ChokingGuide /> </ProtectedRoute>}/>
                                <Route path="/training/bleeding" element={ <ProtectedRoute> <BleedingGuide /> </ProtectedRoute> }/>
                                <Route path="/training/burns" element={ <ProtectedRoute> <BurnsGuide /> </ProtectedRoute> }/>
                                <Route path="/training/fractures" element={ <ProtectedRoute> <FracturesGuide /> </ProtectedRoute> }/>
                            </Routes>
                        </BrowserRouter>
                    </SocketProvider>
                </DataProvider>
            </AuthProvider>
        </div>
    );
}

export default App;
