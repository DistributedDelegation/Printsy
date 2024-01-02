import { useState, useEffect } from 'react';
import { useNavigate } from "react-router-dom";
import "./Home.css";
import Gallery from "../components/Gallery";
import GenerationStyling from "../components/GenerationStyling";
import useMatchHeight from '../components/useMatchHeight';
import Timer from '../components/Timer';

const Home = () => {
  const [prompt, setPrompt] = useState('');
  const [features, setFeatures] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const { generateRef } = useMatchHeight('generateRef');
  const [initialTime, setInitialTime] = useState(0);
  const [showTimer, setShowTimer] = useState(false);

  const handlePromptChange = (e) => {
    setPrompt(e.target.value);
  };

  const onFeatureSelect = (featureName, value) => {
    setFeatures(prev => ({ ...prev, [featureName]: value }));
  };

  const handleSubmit = () => {
    const featureString = Object.entries(features)
      .filter(([_, value]) => value)
      .map(([key, value]) => `${key}: ${value}`)
      .join(", ");

    const combinedPrompt = `${prompt}, ${featureString}`;

    const query = JSON.stringify({
      query: "mutation($prompt: String!) { createImage(prompt: $prompt) { id imageURL } }",
      variables: { prompt: combinedPrompt }
    });

    setIsLoading(true);

    fetch("http://localhost:8080/generation/graphql", {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: query
    })
      .then(response => response.json())
      .then(data => {
        if (data && data.data && data.data.createImage) {
          setIsLoading(false);
          // Navigate to the selected-image route with the imageURL as state
          navigate('/selected-image', { state: { imageURL: data.data.createImage.imageURL, prompt: combinedPrompt } });
        }
      })
      .catch(error => {
        setIsLoading(false);
        console.error('Error:', error);
      });
  };

  // ------ Timer ------
  const fetchRemainingTime = async (userId) => {
    const response = await fetch('http://localhost:8080/cart/graphql', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ 
          query: `query($userId: ID!) { getRemainingCleanupTime(userId: $userId) }`,
          variables: { userId: 1 } 
      }),
    });
    const data = await response.json();
    console.log(data);
    const time = data.data.getRemainingCleanupTime;
    setInitialTime(time);
    setShowTimer(time > 0);
};

  const handleTimerEnd = () => {
    // Logic when timer ends
    setShowTimer(false);
    fetchRemainingTime();
  };

  useEffect(() => {
    fetchRemainingTime();
  }, []);

  // Cart icon link
  const handleNavigateCart = () => {
    navigate('/Cart');
  };

  return (
    <div id="gridTemplate">
      <div id="Generate" ref={generateRef}>
        <div className="header">
          <a href="/">
            <img src="/images/printsyLogo.svg" alt="Logo" className="logo" />
          </a>
        </div>
        <div className="content">
          <h2>Generate</h2>
          <p className="subtitle">Generate your own Image.</p>
          <h3>Prompt</h3>
          <textarea className="text-input" placeholder="Describe the image.." value={prompt} onChange={handlePromptChange}></textarea>
          <h3>Define Style</h3>
          <GenerationStyling onFeatureSelect={onFeatureSelect} />
          {isLoading ? (
            <div className="loading-indicator">
              <button className="secondary-button">Loading..</button>
            </div>
          ) : (
            <button className="primary-button" onClick={handleSubmit}>Generate</button>
          )}

        </div>
      </div>
      <div id="Gallery">
        <div className="cart">
          <span className="timer-next-to-icon"> {showTimer && <Timer initialTime={initialTime} onTimerEnd={handleTimerEnd} />}</span>
          <span className="icon icon-cart" onClick={handleNavigateCart}></span>
        </div>
        <div className="content">
          <h2>Print on Demand Products</h2>
          <div className="product-images">
            <img src="/images/mug.png" alt="Mug"></img>
            <img src="/images/shirt.png" alt="Shirt"></img>
            <img src="/images/polo.png" alt="Polo Shirt"></img>
          </div>
          <h2>Gallery</h2>
          <p>Select an Image from our Community.</p>
          <div className="gallery">
            <Gallery />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;
