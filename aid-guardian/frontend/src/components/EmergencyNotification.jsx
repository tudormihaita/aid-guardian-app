
const EmergencyNotification = ({details, onIgnore, onAccept}) => {
    if (!details) return null;

    return (
        <div className="emergency-notification">
            <div className="emergency-notification-content">
                <h2>Emergency reported nearby</h2>
                <p>{details}</p>
                <button onClick={onAccept}>Respond</button>
                <button onClick={onIgnore}>Close</button>
            </div>
        </div>
    );
}

export default EmergencyNotification;