import React, {useEffect, useRef, useState} from "react";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import icon from "leaflet/dist/images/marker-icon.png";
import iconShadow from "leaflet/dist/images/marker-shadow.png";
import {Link, useNavigate} from "react-router-dom";
import "./ProfilePage.css";
import {useSocket, useUserData} from "../../Contexts";
import UserHeader from "../../components/UserHeader";


const ProfilePage = () => {
    const navigate = useNavigate();

    const { user, setUser, profile } = useUserData();
    const { reportEmergency, subscribeToEmergencyReported } = useSocket();

    const mapRef = useRef(null);
    const [location, setLocation] = useState(null);
    const [isOnDuty, setIsOnDuty] = useState(!!(user && user.onDuty));


    const toggleDutyStatus = async () => {
        try {
            const response = await fetch(`http://localhost:8080/aid-guardian/users/${user.id}?isOnDuty=${!isOnDuty}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if(response.ok) {
                const updatedUser = await response.json();
                setUser(updatedUser);
                setIsOnDuty(!isOnDuty);
                console.log(updatedUser);
                console.log(`Duty status updated to ${isOnDuty ? 'inactive' : 'active'}`);
            }
        } catch (error) {
            console.error('An error occurred while trying to update your duty status:', error);
        }
    };

    const confirmLogout = (event) => {
        event.preventDefault();
        if (window.confirm("Are you sure you want to logout?")) {
            if (isOnDuty) {
                toggleDutyStatus().then(r => {
                    console.log('Duty status set to inactive before logging out');
                })
            }
            // TODO: Implement logout logic
            navigate("/");
        }
    };

    const handleEmergencyReported = (data) => {
        console.log("Emergency reported:", data);
        alert("EMERGENCY REPORTED");
    }

    const handleReportEmergency = () => {
        const emergencyData = {
            "userId": user.id,
            "latitude": location.getCenter().lat,
            "longitude": location.getCenter().lng
        };
        reportEmergency(emergencyData);
    };

    useEffect(() => {
        // Assure user is logged in
        if(!user) {
            navigate("/login");
            return;
        }

        // Subscribe to emergency reported events
        subscribeToEmergencyReported(handleEmergencyReported);

        // Initialize map location
        if (location) return;

        let container = L.DomUtil.get('map');
        if(container != null){
            container._leaflet_id = null;
        }

        const map = L.map(mapRef.current).setView([51.505, -0.09], 13);
        L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}', {
            attribution: '&copy; <a href="https://www.mapbox.com/about/maps/">Mapbox</a>',
            maxZoom: 18,
            id: 'mapbox/streets-v11',
            accessToken: 'pk.eyJ1Ijoicm9ja3kxMzNwYSIsImEiOiJjbHcyY3prYXEwbTVzMmxweXh2cjg1bmVtIn0.apvKY9k9AwrpEUWZ6w1sPw'
        }).addTo(map);

        const options = {
            enableHighAccuracy: true,
            timeout: 5000,
            maximumAge: 0
        };

        let marker, circle, zoomed;
        navigator.geolocation.watchPosition((pos) => {
            const {latitude: lat, longitude: lng, accuracy} = pos.coords;

            if(marker) {
                map.removeLayer(marker);
                map.removeLayer(circle);
            }

            let defaultIcon = L.icon({
                iconUrl: icon,
                shadowUrl: iconShadow,
                iconAnchor: [12, 41]
            });

            marker = L.marker([lat, lng], {icon: defaultIcon}).addTo(map);
            circle = L.circle([lat, lng], {
                color: 'blue',
                fillColor: '#3186cc',
                fillOpacity: 0.5,
                radius: accuracy
            }).addTo(map);

            if (!zoomed) {
                zoomed = map.fitBounds(circle.getBounds());
            }

            // Optional: Adapt marker position based on map zoom
            // map.on('zoomend', () => {
            //     marker.setLatLng(map.getCenter());
            // });

            map.setView([lat, lng]);
        }, (err) => {
            if (err.code === 1) {
                alert("Please allow access to your current location:");
            }
            else {
                alert("Location access revoked, please enable location tracking to use the application emergency features.");
            }
        }, options);

        setLocation(map);
    }, [location, navigate, user]);

    if (!user || !profile) return null;

    return (
        <div>
            <UserHeader firstName={profile["firstName"]} lastName={profile["lastName"]} role={user["role"]}/>
            <nav className="main-nav">
                <ul className="nav-links">
                    <li><Link to="/profile">Home</Link></li>
                    <li><Link to="/guides">Guides</Link></li>
                    <li><Link to="/score">Your Score</Link></li>
                    <li><Link to="/settngs">Settings</Link></li>
                    <li><Link to="/" onClick={confirmLogout}>Logout</Link></li>
                </ul>
            </nav>
            <main>
                <div className="map-positioning">
                    <div id="map" ref={mapRef} className="emergency-map"></div>
                </div>
                <div className="emergency-buttons">
                    <button id="report-button" onClick={handleReportEmergency
                        //() => navigate("/report-emergency" )
                        }>
                        Report Emergency
                    </button>
                    <button>Respond to Emergency</button>
                </div>
                <div className="emergency-info">
                { user.role === "FIRST_RESPONDER" && (
                <div className="emergency-duty">
                    <div className="status-icon">
                        <div className={`indicator ${isOnDuty ? 'active' : 'inactive'}`}></div>
                    </div>
                    <div className="status-text">
                        <h3>{isOnDuty ? 'Active' : 'Inactive'}</h3>
                        <p>{isOnDuty ? 'You can receive an incident and respond' : 'You are not available to respond to emergencies'}</p>
                    </div>
                    <div className="toggle-duty-container">
                        <label className="switch">
                            <input type="checkbox" checked={isOnDuty} onChange={toggleDutyStatus}/>
                            <span className="slider round"></span>
                        </label>
                    </div>
                </div> )}
                </div>
            </main>
            <footer>
                <p>Contact | Support</p>
            </footer>
        </div>
    );
}

export default ProfilePage;