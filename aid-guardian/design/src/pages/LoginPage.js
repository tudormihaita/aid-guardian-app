import React, {useContext, useState} from 'react';
import { useNavigate } from 'react-router-dom';
import '../../../frontend/src/pages/Login/LoginPage.css';
import {AuthContext, UserContext} from "../../../frontend/src/Contexts";

const LoginPage = () => {
    const [credential, setCredential] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const {setIsAuthenticated, setUsername, setAccessToken } = useContext(AuthContext);
    const {setUser, setProfile } = useContext(UserContext);

    const handleLogin = () => {
        if (credential === '' || password === '') {
            alert('Please fill in all login credentials!');
            return;
        }

        // Send login request
        fetch("http://localhost:8080/aid-guardian/users/login", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                "username": credential,
                "email": credential,
                "password": password
            })
        }).then(response => {
            if (response.status === 401) {
                alert('Invalid login credentials!');
            } else if (response.status === 200) {
                alert('Login successful!');
                return response.json();
            } else {
                alert('An error occurred while trying to log in your account, please try again later.');
            }
        }).then(data => {
            localStorage.setItem('user', JSON.stringify(data));
            setIsAuthenticated(true);
            setUsername(data.username);
            setAccessToken(data.accessToken);
            setUser(data);
            return data;
        }).then(data => {
            return fetch("http://localhost:8080/aid-guardian/user-profiles/" + data.id, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                }
            }).then(response => {
                if (response.status === 401) {
                    alert('Unable to retrieve user information.');
                } else {
                    return response.json();
                }
            })
        }).then(profileData => {
            console.log(profileData);
            localStorage.setItem('profile', JSON.stringify(profileData));
            setProfile(profileData);
            navigate('/profile');
        }).catch(error => {
            console.log("Error on fetching login request: " + error);
        });
    }

    return (
        <div className="login-container">
            <div className="login-header">
                <div className="logo">
                    <img src="assets/shield.png" alt="Aid Guardian Logo" />
                </div>
                <h2>Login to Aid Guardian</h2>
            </div>
            <form className="login-form">
                <div className="form-group">
                    <label htmlFor="credential">Username or Email:</label>
                    <input type="text" id="credential" name="Username or email address" required onChange={(e) => setCredential(e.target.value)} />
                </div>
                <div className="form-group">
                    <label htmlFor="password">Password:</label>
                    <input type="password" id="password" name="password" required onChange={(e) => setPassword(e.target.value)} />
                </div>
                <button type="button" className="login-button" onClick={handleLogin}>Login</button>
            </form>
        </div>
    );

}
export default LoginPage;