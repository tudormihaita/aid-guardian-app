import {useEffect, useRef, useState} from "react";
import L from "leaflet";
import 'leaflet-routing-machine';
import "leaflet/dist/leaflet.css";
import 'leaflet-routing-machine/dist/leaflet-routing-machine.css';
import icon from "leaflet/dist/images/marker-icon.png";
import iconShadow from "leaflet/dist/images/marker-shadow.png";
import {Link, useNavigate} from "react-router-dom";
import "./ProfilePage.css";
import UserHeader from "../../components/UserHeader";
import {useSocket} from "../../contexts/ConnectionContext.jsx";
import {useData} from "../../contexts/DataContext.jsx";
import EmergencyNotification from "../../components/EmergencyNotification.jsx";


const ProfilePage = () => {
    const navigate = useNavigate();

    const { user, setUser, profile } = useData();
    const [isOnDuty, setIsOnDuty] = useState(!!(user && user.onDuty));

    const { respondToEmergency, subscribeToEmergencyReported, unsubscribeFromEmergencyReported } = useSocket();
    const [emergencyNotification, setEmergencyNotification] = useState(false);
    const [emergencyDetails, setEmergencyDetails] = useState(null);

    const mapRef = useRef(null);
    const [currentLocation, setCurrentLocation] = useState(null);
    const routeControlRef = useRef(null);



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

    const getPosition = async () => {
        try {
            const position = await new Promise((resolve, reject) => {
                navigator.geolocation.getCurrentPosition(resolve, reject);
            });
            const { latitude, longitude } = position.coords;
            setCurrentLocation({ latitude, longitude });
            return { latitude, longitude };
        } catch (error) {
            console.error('Error getting location:', error);
            throw error;
        }
    };

    const confirmLogout = (event) => {
        event.preventDefault();
        if (window.confirm("Are you sure you want to logout?")) {
            if (isOnDuty) {
                toggleDutyStatus().then(() => {
                    console.log('Duty status set to inactive before logging out');
                })
            }
            // TODO: Implement logout logic
            navigate("/");
        }
    };

    const handleEmergencyReported = async (data) => {
        console.log("Emergency Reported:", data);
        setEmergencyDetails(data.description);
        setEmergencyNotification(true);

        if (currentLocation && mapRef.current) {
            const { latitude, longitude } = await getPosition();
            console.log('Current Location:', latitude, longitude);

            const markerIcon = L.icon({
                iconUrl: icon,
                shadowUrl: iconShadow,
                iconAnchor: [12, 41]
            });
            L.marker([latitude, longitude], {icon: markerIcon}).addTo(mapRef.current);

            routeControlRef.current = L.Routing.control({
                waypoints: [
                    L.latLng(latitude, longitude),
                    L.latLng(data.latitude, data.longitude)
                    // L.latLng(46.66, 23.62)
                ],
                serviceUrl: 'http://router.project-osrm.org/route/v1',
                routeWhileDragging: true,
                showAlternatives: false,
                show: false,
                lineOptions: {
                    styles: [{color: '#3186cc', opacity: 0.7, weight: 5}]
                }
            }).addTo(mapRef.current);


            mapRef.current.flyTo([data.latitude, data.longitude], 15);
            // mapRef.current.flyTo([46.66, 23.62], 15);

        }
    }

    const handleAcceptEmergency = () => {
        console.log('Emergency Accepted');
        setEmergencyNotification(false);
        respondToEmergency({
            responder: user.id
        });
    }

    const handleIgnoreEmergency = async () => {
        console.log('Emergency Ignored');
        const { latitude, longitude } = await getPosition();
        mapRef.current.flyTo([latitude, longitude], 15);

        setEmergencyNotification(false);
        if (routeControlRef.current) {
            mapRef.current.removeControl(routeControlRef.current);
        }
    }

    useEffect(() => {
        // Assure user is logged in
        if(!user) {
            navigate("/login");
            return;
        }

        // Subscribe to emergency reported events
        subscribeToEmergencyReported(handleEmergencyReported);

        // Initialize map location
        if (!mapRef.current) {
            mapRef.current = L.map('map').setView([51.505, -0.09], 13);

            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
                id: 'mapbox/streets-v11',
                accessToken: 'pk.eyJ1Ijoicm9ja3kxMzNwYSIsImEiOiJjbHcyY3prYXEwbTVzMmxweXh2cjg1bmVtIn0.apvKY9k9AwrpEUWZ6w1sPw'
            }).addTo(mapRef.current);

            getPosition().then(
                (position) => {
                    const { latitude: lat, longitude: lng } = position;
                    setCurrentLocation({ lat, lng });

                    const markerIcon = L.icon({
                        iconUrl: icon,
                        shadowUrl: iconShadow,
                        iconAnchor: [12, 41]
                    })

                    let circle = L.circle([lat, lng], {
                        color: 'blue',
                        fillColor: '#3186cc',
                        fillOpacity: 0.5,
                    }).addTo(mapRef.current);
                    L.marker([lat, lng], {icon: markerIcon}).addTo(mapRef.current);
                    mapRef.current.setView([lat, lng], 13);
                    mapRef.current.fitBounds(circle.getBounds());
                },
                (error) => {
                    console.error('Error getting location: ', error);
                    alert('Error getting location. Please allow location access.');
                }
            );
        }

        return () => {
            unsubscribeFromEmergencyReported();
        }

    }, [currentLocation, navigate, subscribeToEmergencyReported, unsubscribeFromEmergencyReported, user]);

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
                    <div id="map"></div>
                </div>
                <div className="emergency-buttons">
                    <button id="report-button" onClick={() => navigate("/report-emergency" )}>
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
                { emergencyNotification && (
                    <EmergencyNotification details={emergencyDetails} onIgnore={handleIgnoreEmergency} onAccept={handleAcceptEmergency}/>
                )}
            </main>
            <footer>
                <p>Contact | Support</p>
            </footer>
        </div>
    );
}

export default ProfilePage;