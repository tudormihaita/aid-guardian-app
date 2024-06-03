import './TrainingPage.css';
import {useData} from "../../contexts/DataContext.jsx";
import {useAuth} from "../../contexts/AuthContext.jsx";
import UserHeader from "../../components/UserHeader.jsx";
import UserNavbar from "../../components/UserNavbar.jsx";
import {Link, useNavigate} from "react-router-dom";
import UserFooter from "../../components/UserFooter.jsx";

function TrainingPage() {
    const navigate = useNavigate();

    const { user, profile } = useData();
    const { setToken } = useAuth();

    const guides = [
        { id: 1, title: 'CPR', description: 'Learn how to perform CPR.', image: 'assets/CPR.png', link: '/training/cpr' },
        { id: 2, title: 'Choking', description: 'Learn how to assist someone who is choking.', image: 'assets/choking.png', link: '/training/choking' },
        { id: 3, title: 'Severe Bleeding', description: 'Learn how to control severe bleeding.', image: 'assets/bleeding.png', link: '/training/bleeding' },
        { id: 4, title: 'Burns', description: 'Learn how to treat burns.', image: 'assets/burns.png', link: '/training/burns' },
        { id: 5, title: 'Fractures', description: 'Learn how to manage fractures.', image: 'assets/fractures.png', link: '/training/fractures' }
    ];

    const confirmLogout = (event) => {
        event.preventDefault();
        if (window.confirm("Are you sure you want to logout?")) {
            setToken(null);
            navigate("/", { replace: true });
        }
    };

    if (!user || !profile) return null;

    return (
        <div className="guides-page">
            <UserHeader firstName={profile["firstName"]} lastName={profile["lastName"]} role={user["role"]}/>
            <UserNavbar handleLogout={confirmLogout} />
            <main>
                <div className="guides-container">
                    {guides.map(guide => (
                        <div key={guide.id} className="guide">
                            <img src={guide.image} alt={guide.title} />
                            <div className="guide-details">
                                <h3>{guide.title}</h3>
                                <Link to={guide.link}>{guide.description}</Link>
                            </div>
                        </div>
                    ))}
                </div>
            </main>
            <UserFooter />
        </div>
    );
}

export default TrainingPage;
