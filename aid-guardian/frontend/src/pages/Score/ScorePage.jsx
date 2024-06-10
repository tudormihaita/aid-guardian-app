import "./ScorePage.css";
import { useData } from "../../contexts/DataContext.jsx";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext.jsx";
import UserHeader from "../../components/UserHeader.jsx";
import UserNavbar from "../../components/UserNavbar.jsx";
import UserFooter from "../../components/UserFooter.jsx";

const ScorePage = () => {
    const navigate = useNavigate();

    const { user, profile } = useData();
    const [score, setScore] = useState(0);
    const { setToken } = useAuth();
    const maxScore = 5.0;

    useEffect(() => {
        const animationDuration = 1500;
        const steps = 10;
        const stepDuration = animationDuration / steps;
        const increment = profile.score / steps;

        let currentXp = 0;
        const interval = setInterval(() => {
            currentXp += increment;
            setScore(Math.min(currentXp, profile.score));
            if (currentXp >= profile.score) {
                clearInterval(interval);
            }
        }, stepDuration);

        return () => clearInterval(interval);
    }, [profile.score]);

    const confirmLogout = (event) => {
        event.preventDefault();
        if (window.confirm("Are you sure you want to logout?")) {
            setToken(null);
            navigate("/", { replace: true });
        }
    };

    if (!user || !profile) return null;

    return (
        <div className="score-page">
            <UserHeader firstName={profile["firstName"]} lastName={profile["lastName"]} role={user["role"]} />
            <UserNavbar handleLogout={confirmLogout} />
            <main className="mainClass">
                <div className="score-container">
                    <div className="score-label">Your Score: {profile.score}</div>
                    <div className="xp-bar-container">
                        <div className="xp-bar" style={{ width: `${(score / maxScore) * 100}%` }}></div>
                    </div>
                    <div className="ranking-container">
                        <div className="score-ranking">
                            <div className={`ranking-item ${score >= 1.25 ? 'active' : ''}`}>
                                <img src="/assets/basic_level.png" alt="0-1.25 points" />
                                <h3>Basic</h3>
                            </div>
                            <div className={`ranking-item ${score >= 2.5 ? 'active' : ''}`}>
                                <img src="/assets/advanced_level.png" alt="1.26-2.5 points" />
                                <h3>Advanced</h3>
                            </div>
                            <div className={`ranking-item ${score >= 3.75 ? 'active' : ''}`}>
                                <img src="/assets/staff&volunteer_level.png" alt="2.6-3.75 points" />
                                <h3>Staff & Volunteer</h3>
                            </div>
                            <div className={`ranking-item ${score >= 5 ? 'active' : ''}`}>
                                <img src="/assets/aed_level.png" alt="3.76-5 points" />
                                <h3>AED</h3>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
            <UserFooter/>
        </div>
    );
}

export default ScorePage;
