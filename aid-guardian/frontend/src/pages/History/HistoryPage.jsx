import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useData } from "../../contexts/DataContext.jsx";
import { useAuth } from "../../contexts/AuthContext.jsx";
import UserHeader from "../../components/UserHeader.jsx";
import UserNavbar from "../../components/UserNavbar.jsx";
import "./HistoryPage.css";
import UserFooter from "../../components/UserFooter.jsx";

const HistoryPage = () => {
    const navigate = useNavigate();
    const { user, profile } = useData();
    const { setToken } = useAuth();

    const [emergencies, setEmergencies] = useState([]);
    const [view, setView] = useState('reported');


    useEffect(() => {
        const fetchEmergencies = async () => {
            try {
                const userId = user.id;
                const endpoint =
                    view === 'reported'
                        ? `http://localhost:8080/aid-guardian/users/${userId}/emergencies/reported`
                        : `http://localhost:8080/aid-guardian/users/${userId}/emergencies/responded`;
                const response = await fetch(endpoint, {
                    method: 'GET',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json',
                    },
                });

                if (!response.ok) {
                    alert('Error fetching emergencies');
                }

                const data = await response.json();
                console.log("Retrieved emergencies:", data);
                setEmergencies(data);
            } catch (error) {
                console.error("Error fetching emergencies:", error);
                alert('Error fetching emergencies');
            }
        };

        fetchEmergencies();
    }, [user.id, view]);

    const handleViewChange = (event) => {
        setView(event.target.value);
    }

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
            <UserHeader firstName={profile["firstName"]} lastName={profile["lastName"]} role={user["role"]} />
            <UserNavbar handleLogout={confirmLogout} />
            <main className="mainClass">
                <div className="emergency-history-container">
                    <h2>Emergency History</h2>
                    <div className="view-selector">
                        <label>View:
                            <select id="view" name="view" value={view} onChange={handleViewChange}>
                                <option value="reported">Reported Emergencies</option>
                                {user.role === 'FIRST_RESPONDER' &&
                                    <option value="responded">Responded Emergencies</option>
                                }
                            </select>
                        </label>
                    </div>
                    {emergencies.length > 0 ? (
                        <ul className="emergency-list">
                            {emergencies.map((emergency) => (
                                <li key={emergency.id} className="emergency-item">
                                    <div className="emergency-info">
                                        <h3>{emergency.title}</h3>
                                        <p><strong>Description:</strong> {emergency.description}</p>
                                        <p><strong>Date:</strong> {new Date(emergency.reportedAt).toLocaleDateString()}</p>
                                        <p><strong>Status:</strong> {emergency.status}</p>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p>No previously responded emergencies found.</p>
                    )}
                </div>
            </main>
            <UserFooter/>
        </div>
    );
};

export default HistoryPage;
