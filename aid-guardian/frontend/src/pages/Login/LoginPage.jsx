import {useState} from 'react';
import { useNavigate } from 'react-router-dom';
import './LoginPage.css';
import {useAuth} from "../../contexts/AuthContext.jsx";
import {useData} from "../../contexts/DataContext.jsx";
import {useSocket} from "../../contexts/ConnectionContext.jsx";


const LoginPage = () => {
    const [credential, setCredential] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const { setToken } = useAuth();
    const { setUser, setProfile } = useData();
    const { initializeConnection } = useSocket();

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
           if (response.status === 200) {
                return response.json();
            } else {
                alert('Invalid login credentials, please verify and try again.');
            }
        }).then(userData => {
            console.log(userData);

            const { authenticatedUser, accessToken } = userData;
            localStorage.setItem('user', JSON.stringify(authenticatedUser));
            localStorage.setItem('token', accessToken);

            setToken(userData.accessToken);
            setUser(authenticatedUser);
            initializeConnection();

            return authenticatedUser;
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
            navigate('/profile', {replace: true});
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