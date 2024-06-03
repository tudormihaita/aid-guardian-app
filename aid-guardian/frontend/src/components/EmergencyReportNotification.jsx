
const EmergencyReportNotification = ({data, distance, onIgnore, onAccept}) => {
    if (!data) return null;

    return (
        <div className="emergency-notification">
            <div className="emergency-notification-content">
                <h2>Emergency reported nearby! ({distance}m)</h2>
                <p>{data.description}</p>
                <button onClick={() => onAccept(data, distance)}>Respond</button>
                <button onClick={() => onIgnore()}>Close</button>
            </div>
        </div>
    );
}

export default EmergencyReportNotification;