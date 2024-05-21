import './EmergencyPage.css'
import {Link, useNavigate} from "react-router-dom";
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import icon from "leaflet/dist/images/marker-icon.png";
import iconShadow from "leaflet/dist/images/marker-shadow.png";
import React, {useState, useEffect, useRef, useContext} from "react";
import UserHeader from "./UserHeader";
import {UserContext} from "./Contexts";

const EmergencyPage = () => {
    const { user, profile } = useContext(UserContext);
    const navigate = useNavigate();
    const [currentLocation, setCurrentLocation] = useState(null);
    const [useCurrentLocation, setUseCurrentLocation] = useState(true);
    const mapRef = useRef(null);

    const confirmLogout = (event) => {
        event.preventDefault();
        if (window.confirm("Are you sure you want to logout?")) {
            // TODO: Implement logout logic
            navigate("/");
        }
    };

    const handleLocationChange = (event) => {
        setUseCurrentLocation(event.target.value === 'current');
    }

    useEffect(() => {
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
    }, []);

    const handleSubmit = (event) => {
        event.preventDefault();
        const description = event.target['emergency-description'].value;
        const location = useCurrentLocation
            ? `Lat: ${currentLocation.latitude}, Lng: ${currentLocation.longitude}`
            : event.target.elements['emergency-location'].value;

        console.log('Emergency Description:', description);
        console.log('Emergency Location:', location);

        // TODO: implement form submission logic here
    }

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
                    <form className="emergency-report-form" onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="emergency-description">Describe the emergency:</label>
                            <textarea id="emergency-description" name="emergency-description" rows="4"></textarea>
                        </div>
                        <div className="form-group-radio">
                            <label>What is the location of the emergency?</label>
                            <div>
                                <input
                                    type="radio"
                                    id="current-location"
                                    name="location"
                                    value="current"
                                    checked={useCurrentLocation}
                                    onChange={handleLocationChange}
                                />
                                <label htmlFor="current-location">Use my current location</label>
                            </div>
                            <div>
                                <input
                                    type="radio"
                                    id="other-location"
                                    name="location"
                                    value="other"
                                    checked={!useCurrentLocation}
                                    onChange={handleLocationChange}
                                />
                                <label htmlFor="other-location">Use another location</label>
                            </div>
                        </div>
                        {!useCurrentLocation && (
                            <div className="form-group" id="other-location-details">
                                <label htmlFor="emergency-location">Enter location details:</label>
                                <textarea id="emergency-location" name="emergency-location" rows="2"></textarea>
                            </div>
                        )}
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
}

export default EmergencyPage;