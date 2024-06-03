import {useEffect, useState} from "react";

const UserHeader = ({ firstName, lastName, role}) => {
    const [dateTime, setDateTime] = useState(new Date());

    useEffect(() => {
        const timer = setInterval(() => {
            setDateTime(new Date());
        }, 1000);

        return () => clearInterval(timer);
    }, []);

    const formatRole = (role) => {
        if (!role) return '';
        return role
            .toLowerCase()
            .split('_')
            .map(word => word.charAt(0).toUpperCase() + word.slice(1))
            .join(' ');
    };

    const formatDateTime = (date) => {
        const options = {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        };
        return date.toLocaleDateString(undefined, options);
    };

    const getAvatarByRole = (role) => {
        switch(role.toLowerCase()) {
            case 'first_responder':
                return 'assets/first_responder_icon.png';
            default:
                return 'assets/community_dispatcher_icon.png';
        }
    };

    return (
        <header className="user-header">
            <div className="user-info">
                <img src="assets/shield.png" alt="Aid Guardian Logo" style={{marginRight: '50px'}}/>
                <img id="icon" src={getAvatarByRole(role)} alt="Avatar"/>
                <div className="user-details">
                    <h2>Welcome, {firstName} {lastName}</h2>
                    <p>{formatRole(role)}</p>
                </div>
            </div>
            <div className="date-time">
                {formatDateTime(dateTime)}
            </div>
        </header>
    );
}

export default UserHeader;