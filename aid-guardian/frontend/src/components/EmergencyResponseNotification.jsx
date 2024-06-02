
const EmergencyResponseNotification = ({ data, onClose }) => {
    if (!data.responder) return null;

    return (
        <div className="emergency-notification">
            <div className="emergency-notification-content">
                <h2>Emergency responder nearby is on the way!</h2>
                <p>Please wait at the incident site, help is on the way: {data.distance}m </p>
                <button onClick={onClose}>Close</button>
            </div>
        </div>
    )
}

export default EmergencyResponseNotification;