import {useAuth} from "../contexts/AuthContext.jsx";
import {Link} from "react-router-dom";

const UserNavbar = ( {handleLogout} ) => {
    const { isAuthenticated } = useAuth();

    if(!isAuthenticated) {
        return null;
    }

    return (
        <nav className="main-nav">
            <ul className="nav-links">
                <li><Link to="/profile">Home</Link></li>
                <li><Link to="/training">Guides</Link></li>
                <li><Link to="/score">Your Score</Link></li>
                <li><Link to="/history">History</Link></li>
                <li><Link to="/settings">Settings</Link></li>
                <li><Link to="/" onClick={handleLogout}>Logout</Link></li>
            </ul>
        </nav>
    );
}

export default UserNavbar;