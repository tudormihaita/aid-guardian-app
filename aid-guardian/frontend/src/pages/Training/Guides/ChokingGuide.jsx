import './GuideDetails.css';
import {useState} from "react";

const ChokingGuide = () => {
    const [currentIndex, setCurrentIndex] = useState(0);
    const numSlides = 5;
    const slideWidth = 500 / numSlides;

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
                        <img className="cpr-images" src="/assets/choking-images/choking-images1.jpeg" alt="Image 1"/>
                    </li>
                    <li className="carousel-item">
                        <img className="cpr-images" src="/assets/choking-images/choking-images2.jpeg" alt="Image 2"/>
                    </li>
                    <li className="carousel-item">
                        <img className="cpr-images" src="/assets/choking-images/choking-images3.jpeg" alt="Image 3"/>
                    </li>
                    <li className="carousel-item">
                        <img className="cpr-images" src="/assets/choking-images/choking-images4.jpeg" alt="Image 4"/>
                    </li>
                    <li className="carousel-item">
                        <img className="cpr-images" src="/assets/choking-images/choking-images5.jpeg" alt="Image 5"/>
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

export default ChokingGuide;