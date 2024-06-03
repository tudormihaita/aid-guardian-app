import {useNavigate} from "react-router-dom";
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import icon from "leaflet/dist/images/marker-icon.png";
import iconShadow from "leaflet/dist/images/marker-shadow.png";
import {useState, useEffect, useRef } from "react";
import UserHeader from "../../components/UserHeader";
import {useData} from "../../contexts/DataContext.jsx";
import {useSocket} from "../../contexts/ConnectionContext.jsx";
import './EmergencyPage.css';
import UserNavbar from "../../components/UserNavbar.jsx";
import {useAuth} from "../../contexts/AuthContext.jsx";
import EmergencyResponseNotification from "../../components/EmergencyResponseNotification.jsx";
import UserFooter from "../../components/UserFooter.jsx";

const EmergencyPage = () => {
    const navigate = useNavigate();

    const { user, profile } = useData();
    const { isAuthenticated, setToken } = useAuth();

    const {reportEmergency, subscribeToEmergencyResponded, unsubscribeFromEmergencyResponded } = useSocket();
    const [emergencyNotification, setEmergencyNotification] = useState(false);
    const [emergencyData, setEmergencyData] = useState(null);

    const [currentLocation, setCurrentLocation] = useState(null);
    const mapRef = useRef(null);


    const confirmLogout = (event) => {
        event.preventDefault();
        if (window.confirm("Are you sure you want to logout?")) {
            setToken(null);
            navigate("/", { replace: true });
        }
    };

    const handleReportEmergency = async (event) => {
        event.preventDefault();
        const description = event.target['emergency-description'].value;

        console.log('Emergency Description:', description);
        console.log('Emergency Location:', currentLocation);

        const emergencyData = {
            reporter: user ,
            status: "REPORTED",
            latitude: currentLocation.latitude,
            longitude: currentLocation.longitude,
            description: description,
            reportedAt: new Date().toISOString(),
            responder: null
        };

        try {
            const response = await fetch('http://localhost:8080/aid-guardian/emergencies', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(emergencyData),
            });

            if (!response.ok) {
                alert('Error reporting emergency');
                return;
            }

            const reportedEmergencyData = await response.json();
            console.log('Reported Emergency:', reportedEmergencyData);
            setEmergencyData(reportedEmergencyData);
            reportEmergency(reportedEmergencyData);
        } catch (error) {
            console.error('Error reporting emergency:', error);
            alert('Error reporting emergency');
        }

    };

    const handleEmergencyResponded = (data) => {
        // Notify only the user who reported the emergency
        if (data.reporter.id !== user.id) return;

        console.log("Emergency Responded:", data);
        setEmergencyData(data);
        setEmergencyNotification(true);

        // Center the map on the emergency location
        mapRef.current.setView([data.latitude, data.longitude], 30);
    }

    const handleEmergencyNotificationClosed = () => {
        setEmergencyNotification(false);
    }

    useEffect(() => {
        if(!isAuthenticated) {
            navigate("/login", { replace: true });
            return;
        }

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
            <UserNavbar handleLogout={confirmLogout} />
            <main>
                <div className="emergency-report-container">
                    <h2>Report Emergency</h2>
                    <form className="emergency-report-form" onSubmit={handleReportEmergency}>
                        <div className="form-group">
                            <label htmlFor="emergency-description">Describe the emergency:</label>
                            <textarea id="emergency-description" name="emergency-description" rows="4"></textarea>
                        </div>

                        {/*<div className="emergency-report-details">*/}
                        {/*    <label>Emergency details</label>*/}
                        {/*</div>*/}

                        <button type="submit" className="report-button">Report</button>
                    </form>
                </div>
                <div className="map-container">
                    <div id="map"></div>
                </div>
                { emergencyNotification && (
                    <EmergencyResponseNotification data={emergencyData} onClose={handleEmergencyNotificationClosed} />
                )}
            </main>
            <UserFooter></UserFooter>
        </div>
    );
};

export default EmergencyPage;