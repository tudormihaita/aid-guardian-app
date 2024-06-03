import './GuideDetails.css';

const FracturesGuide = () => {
    return (
        <div className="guide-details">
            <header id="header">
                <div className="logo">
                    <img src="/assets/shield.png" alt="Aid Guardian Logo" className="logo"/>
                </div>
            </header>
            <div className="tutorial-container">
                <h1>How to Treat Fractures</h1>
                <p>Follow the steps in this video to learn how to treat fractures.</p>
                <div className="video-container">
                    <iframe
                        width="560"
                        height="315"
                        src="https://www.youtube.com/embed/2v8vlXgGXwE"
                        title="Guide Tutorial Video Player"
                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                        allowFullScreen={true}
                    />
                </div>
            </div>
        </div>
    );
}

export default FracturesGuide;