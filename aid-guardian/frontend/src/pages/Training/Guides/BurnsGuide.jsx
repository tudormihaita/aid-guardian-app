import './GuideDetails.css';
import {useState} from "react";

const BurnsGuide = () => {
    const [currentIndex, setCurrentIndex] = useState(0);
    const numSlides = 6;
    const slideWidth = 600 / numSlides;

    const nextSlide = () => {
        setCurrentIndex((currentIndex + 1) % numSlides);
    };

    const prevSlide = () => {
        setCurrentIndex((currentIndex - 1 + numSlides) % numSlides);
    };

    return (
        <div className="guide-details">
            <header id="header">
                <div className="logo">
                    <img src="/assets/shield.png" alt="Aid Guardian Logo" className="logo"/>
                </div>
            </header>
            <div className="carousel-container">
                <ol className="carousel" style={{transform: `translateX(-${currentIndex * slideWidth}%)`}}>
                    <li className="carousel-item">
                        <img className="cpr-images" src="/assets/burn-images/burn-images1.png" alt="Image 1"/>
                    </li>
                    <li className="carousel-item">
                        <img className="cpr-images" src="/assets/burn-images/burn-images2.png" alt="Image 2"/>
                    </li>
                    <li className="carousel-item">
                        <img className="cpr-images" src="/assets/burn-images/burn-images3.png" alt="Image 3"/>
                    </li>
                    <li className="carousel-item">
                        <img className="cpr-images" src="/assets/burn-images/burn-images4.png" alt="Image 4"/>
                    </li>
                    <li className="carousel-item">
                        <img className="cpr-images" src="/assets/burn-images/burn-images5.png" alt="Image 5"/>
                    </li>
                    <li className="carousel-item">
                        <img className="cpr-images" src="/assets/burn-images/burn-images6.png" alt="Image 6"/>
                    </li>
                </ol>
                <div className="guide-buttons">
                    <button id="prevBtn" onClick={prevSlide}>Previous</button>
                    <button id="nextBtn" onClick={nextSlide}>Next</button>
                </div>
            </div>
        </div>
    );
}

export default BurnsGuide;