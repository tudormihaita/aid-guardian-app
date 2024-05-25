import React from "react";

const UserHeader = ({ firstName, lastName, role}) => {

    const formatRole = (role) => {
        if (!role) return '';
        return role
            .toLowerCase()
            .split('_')
            .map(word => word.charAt(0).toUpperCase() + word.slice(1))
            .join(' ');
    };

    return (
        <header className="user-header">
            <div className="user-info">
                <img src="assets/shield.png" alt="Aid Guardian Logo" style={{marginRight: '50px'}}/>
                <img id="icon" src="assets/first_responder_icon.png" alt="Avatar"/>
                <div className="user-details">
                    <h2>Welcome, {firstName} {lastName}</h2>
                    <p>{formatRole(role)}</p>
                </div>
            </div>
        </header>
    );
}

export default UserHeader;