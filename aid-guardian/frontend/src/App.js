import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import HomePage from "./HomePage";
import LoginPage from "./LoginPage";
import SignUpPage from "./SignUpPage";
import ProfilePage from "./ProfilePage";
import EmergencyPage from "./EmergencyPage";
import './App.css';
import {Component} from "react";
import PrivateRoute from "./PrivateRoute";

class App extends Component {
    constructor(props) {
        super(props)
        this.state = {
            isAuthenticated: false,
            username: '',
            password: '',
            target: '',
            messages: [],
            accessToken: ''
        }
    }

    setAuthenticated = (isAuthenticated) => {
        this.setState({isAuthenticated});
    };

    render() {
        const {isAuthenticated} = this.state;

        return (
            <div className="App">
                <Router>
                    <Routes>
                        <Route path="/" element={<HomePage/>}/>
                        {/*<Route path="/login" element={<LoginPage setAuthenticated={this.setAuthenticated}/>}/>*/}
                        <Route path="/login" element={<LoginPage/>}/>
                        <Route path="/signup" element={<SignUpPage/>}/>
                        <Route path="/profile" element={<ProfilePage/>}/>
                        <Route path="/report-emergency" element={<EmergencyPage/>}/>
                        {/*<Route path="/profile" element={*/}
                        {/*    <PrivateRoute isAuthenticated={isAuthenticated} element={<ProfilePage />}/>*/}
                        {/*}/>*/}
                        {/*<Route path="/report-emergency" element={*/}
                        {/*    <PrivateRoute isAuthenticated={isAuthenticated} element={<EmergencyPage />}/>*/}
                        {/*}/>*/}
                    </Routes>
                </Router>
            </div>
        );
    }
}

export default App;
