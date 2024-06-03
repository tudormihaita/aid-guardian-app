import './SettingsPage.css';
import {useEffect, useRef, useState} from "react";
import UserHeader from "../../components/UserHeader.jsx";
import UserNavbar from "../../components/UserNavbar.jsx";
import {useData} from "../../contexts/DataContext.jsx";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../../contexts/AuthContext.jsx";
import UserFooter from "../../components/UserFooter.jsx";

const SettingsPage = () => {
    const navigate = useNavigate();

    const { user, profile } = useData();
    const { setToken } = useAuth();

    const [activeTab, setActiveTab] = useState('account');
    const tabsRef = useRef();
    const tabContentsRef = useRef();

    useEffect(() => {
        const tabContents = tabContentsRef.current.children;

        for (let content of tabContents) {
            if (content.id === activeTab) {
                content.style.display = 'block';
                content.classList.add('active');
            } else {
                content.style.display = 'none';
                content.classList.remove('active');
            }
        }
    }, []);

    const handleTabClick = (event) => {
        const tabId = event.target.getAttribute('data-tab');

        const tabs = tabsRef.current.children;
        const tabContents = tabContentsRef.current.children;

        for (let tab of tabs) {
            tab.classList.remove('active');
        }
        for (let content of tabContents) {
            content.classList.remove('active');
            content.style.display = 'none';
        }

        event.target.classList.add('active');
        document.getElementById(tabId).classList.add('active');
        document.getElementById(tabId).style.display = 'block';

        setActiveTab(event.target.dataset.tab);
    };
    const confirmLogout = (event) => {
        event.preventDefault();
        if (window.confirm("Are you sure you want to logout?")) {
            setToken(null);
            navigate("/", { replace: true });
        }
    };

    if (!user || !profile) return null;

    return (
        <div>
            <UserHeader firstName={profile["firstName"]} lastName={profile["lastName"]} role={user["role"]}/>
            <UserNavbar handleLogout={confirmLogout} />
            <main>
                <div className="container">
                    <div className="account-settings">
                        <div className="header-tabs">
                            <h2>Account settings</h2>
                        </div>
                        <div className="tabs" ref={tabsRef}>
                            <button className={`tab ${activeTab === 'account' ? 'active' : ''}`} onClick={(event) => handleTabClick(event)} data-tab="account">Account
                            </button>
                            <button className={`tab ${activeTab === 'privacy' ? 'active' : ''}`} onClick={(event) => handleTabClick(event)} data-tab="privacy">Privacy
                            </button>
                        </div>
                        <div ref={tabContentsRef}>
                            <div className={`tab-content ${activeTab === 'account' ? 'active' : ''}`}
                                 style={{display: activeTab === 'account' ? 'block' : 'none'}} id="account">
                                <div className="form-group">
                                    <label htmlFor="email">Email:</label>
                                    <input className="disabled" type="email" id="email" name="email"
                                           defaultValue={user.email}/>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="username">Username:</label>
                                    <input className="disabled" type="text" id="username" name="username"
                                           defaultValue={user.username} disabled/>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="password">Password:</label>
                                    <input className="disabled" type="password" id="password" name="password"
                                           defaultValue=''/>
                                </div>
                                <button type="submit" className="save-button">Save Changes</button>
                            </div>
                            <div className={`tab-content ${activeTab === 'account' ? 'active' : ''}`}
                                 style={{display: activeTab === 'privacy' ? 'block' : 'none'}} id="privacy">
                                <div className="form-group">
                                    <label htmlFor="first-name">First Name:</label>
                                    <input type="text" id="first-name" name="first-name"
                                           defaultValue={profile.firstName}/>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="last-name">Last Name:</label>
                                    <input type="text" id="last-name" name="last-name" defaultValue={profile.lastName}/>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="gender">Gender:</label>
                                    <select className="disabled" id="gender" name="gender" defaultValue={profile.gender.toLowerCase()}>
                                        <option value="female">Female</option>
                                        <option value="male">Male</option>
                                        <option value="other">Other</option>
                                    </select>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="dob">Date of Birth:</label>
                                    <input className="disabled" type="date" id="dob" name="dob" defaultValue={profile.birthDate}
                                           disabled/>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="height">Height (cm):</label>
                                    <input type="number" id="height" name="height" defaultValue={profile.height.toFixed(3) * 100}/>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="weight">Weight (kg):</label>
                                    <input type="number" id="weight" name="weight" defaultValue={profile.weight}/>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="blood-group">Blood Group:</label>
                                    <select className="disabled" id="blood-group" name="blood-group" disabled defaultValue={profile.bloodGroup}>
                                        <option value="A+">A+</option>
                                        <option value="A-">A-</option>
                                        <option value="B+">B+</option>
                                        <option value="B-">B-</option>
                                        <option value="AB+">AB+</option>
                                        <option value="AB-">AB-</option>
                                        <option value="O+">O+</option>
                                        <option value="O-">O-</option>
                                    </select>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="medical-history">Medical History:</label>
                                    <textarea id="medical-history" name="medical-history" rows="4" defaultValue={profile.medicalHistory}></textarea>
                                </div>
                                <div className="form-group-checkbox">
                                    <input type="checkbox" id="certified" name="certified" defaultChecked={user.role === "FIRST_RESPONDER"}  disabled/>
                                    <label htmlFor="certified">I am certified in medicine or first aid:</label>
                                </div>
                                <button type="submit" className="save-button">Save Changes</button>
                            </div>
                    </div>
                    </div>
                </div>
            </main>
            <UserFooter/>
        </div>
    );
}

export default SettingsPage;