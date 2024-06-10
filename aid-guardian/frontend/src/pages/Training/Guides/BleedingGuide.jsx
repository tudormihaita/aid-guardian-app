import './GuideDetails.css';
import {useState} from "react";

const BleedingGuide = () => {
    const [currentIndex, setCurrentIndex] = useState(0);
    const numSlides = 9;
    const slideWidth = 900 / numSlides;



    const nextSlide = () => {
        console.log(currentIndex);
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
                <ol className="carousel" style={{ transform: `translateX(-${currentIndex * slideWidth}%)`}}>
                    <li className="carousel-item">
                        <img className="cpr-images" src="/assets/bleeding-images/bleeding-image1.jpeg" alt="Image 1"/>

                    </li>
                    <li className="carousel-item">
                        <img className="cpr-images" src="/assets/bleeding-images/bleeding-image2.jpeg" alt="Image 2"/>

                    </li>
                    <li className="carousel-item">
                        <img className="cpr-images" src="/assets/bleeding-images/bleeding-image3.jpeg" alt="Image 3"/>

                    </li>
                    <li className="carousel-item">
                        <img className="cpr-images" src="/assets/bleeding-images/bleeding-image4.jpeg" alt="Image 4"/>

                    </li>
                    <li className="carousel-item">
                        <img className="cpr-images" src="/assets/bleeding-images/bleeding-image5.jpeg" alt="Image 5"/>

                    </li>
                    <li className="carousel-item">
                        <img className="cpr-images" src="/assets/bleeding-images/bleeding-image6.jpeg" alt="Image 6"/>

                    </li>
                    <li className="carousel-item">
                        <img className="cpr-images" src="/assets/bleeding-images/bleeding-image7.jpeg" alt="Image 6"/>

                    </li>
                    <li className="carousel-item">
                        <img className="cpr-images" src="/assets/bleeding-images/bleeding-image8.jpeg" alt="Image 6"/>

                    </li>
                    <li className="carousel-item">
                        <img className="cpr-images" src="/assets/bleeding-images/bleeding-image9.jpeg" alt="Image 6"/>
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

export default BleedingGuide;
