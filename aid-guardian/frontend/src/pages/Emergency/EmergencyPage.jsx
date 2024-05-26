import {Link, useNavigate} from "react-router-dom";
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import icon from "leaflet/dist/images/marker-icon.png";
import iconShadow from "leaflet/dist/images/marker-shadow.png";
import {useState, useEffect, useRef } from "react";
import UserHeader from "../../components/UserHeader";
import {useData} from "../../contexts/DataContext.jsx";
import {useSocket} from "../../contexts/ConnectionContext.jsx";
import './EmergencyPage.css';

const EmergencyPage = () => {
    const navigate = useNavigate();

    const { user, profile } = useData();
    const {reportEmergency, subscribeToEmergencyResponded, unsubscribeFromEmergencyResponded } = useSocket();

    const [currentLocation, setCurrentLocation] = useState(null);
    const mapRef = useRef(null);


    const confirmLogout = (event) => {
        event.preventDefault();
        if (window.confirm("Are you sure you want to logout?")) {
            // TODO: Implement logout logic
            navigate("/");
        }
    };

    const handleReportEmergency = (event) => {
        event.preventDefault();
        const description = event.target['emergency-description'].value;

        console.log('Emergency Description:', description);
        console.log('Emergency Location:', currentLocation);

        const emergencyData = {
            "reporter": user.id,
            "latitude": currentLocation.latitude,
            "longitude": currentLocation.longitude,
            "description": description
        };
        reportEmergency(emergencyData);
    };

    const handleEmergencyResponded = (data) => {
        console.log("Emergency Responded:", data);
        alert("EMERGENCY RESPONDED");
    }

    useEffect(() => {
        // Subscribe to emergency response events
        subscribeToEmergencyResponded(handleEmergencyResponded);

        if (!mapRef.current) {
            mapRef.current = L.map('map').setView([51.505, -0.09], 13);

            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
            }).addTo(mapRef.current);

            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const { latitude, longitude } = position.coords;
                    setCurrentLocation({ latitude, longitude });

                    const markerIcon = L.icon({
                        iconUrl: icon,
                        shadowUrl: iconShadow,
                        iconAnchor: [12, 41]
                    })

                    L.marker([latitude, longitude], {icon: markerIcon}).addTo(mapRef.current);
                    mapRef.current.setView([latitude, longitude], 13);
                },
                (error) => {
                    console.error('Error getting location: ', error);
                    alert('Error getting location. Please allow location access.');
                }
            );
        }

        return () => {
            unsubscribeFromEmergencyResponded();
        }
    }, [subscribeToEmergencyResponded, unsubscribeFromEmergencyResponded]);

    if (!user || !profile) return null;

    return (
        <div>
            <UserHeader firstName={profile["firstName"]} lastName={profile["lastName"]} role={user["role"]}/>
            <nav className="main-nav">
                <ul className="nav-links">
                    <li><Link to="/profile">Home</Link></li>
                    <li><Link to="/guides">Guides</Link></li>
                    <li><Link to="/score">Your Score</Link></li>
                    <li><Link to="/settings">Settings</Link></li>
                    <li><Link to="/" onClick={confirmLogout}>Logout</Link></li>
                </ul>
            </nav>
            <main>
                <div className="emergency-report-container">
                    <h2>Report Emergency</h2>
                    <form className="emergency-report-form" onSubmit={handleReportEmergency}>
                        <div className="form-group">
                            <label htmlFor="emergency-description">Describe the emergency:</label>
                            <textarea id="emergency-description" name="emergency-description" rows="4"></textarea>
                        </div>
                        <div className="emergency-report-details">
                            <label>Emergency details</label>
                        </div>
                        <button type="submit" className="report-button">Report</button>
                    </form>
                </div>
                <div className="map-container">
                    <div id="map"></div>
                </div>
            </main>
            <footer>
                <p>Contact | Support</p>
            </footer>
        </div>
    );
};

export default EmergencyPage;