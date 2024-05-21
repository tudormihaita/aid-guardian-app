import React, {useContext, useEffect, useRef, useState} from "react";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import icon from "leaflet/dist/images/marker-icon.png";
import iconShadow from "leaflet/dist/images/marker-shadow.png";
import "./ProfilePage.css";
import {Link, useNavigate} from "react-router-dom";
import {UserContext} from "./Contexts";
import UserHeader from "./UserHeader";


const ProfilePage = () => {
    const mapRef = useRef(null);
    const [location, setLocation] = useState(null);
    const { user, profile } = useContext(UserContext);
    const navigate = useNavigate();

    const confirmLogout = (event) => {
        event.preventDefault();
        if (window.confirm("Are you sure you want to logout?")) {
            // TODO: Implement logout logic
            navigate("/");
        }
    };

    useEffect(() => {
        if(!user) {
            navigate("/login");
            return;
        }

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
    }, [location]);

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
                    <button id="report-button" onClick={() => navigate("/report-emergency")}>
                        Report Emergency
                    </button>
                    <button>Respond to Emergency</button>
                </div>
            </main>
            <footer>
                <p>Contact | Support</p>
            </footer>
        </div>
    );
}

export default ProfilePage;