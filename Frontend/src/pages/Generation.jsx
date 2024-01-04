import { useState, useEffect, useContext } from "react";
import { AuthContext } from "../AuthContext";
import { useNavigate } from "react-router-dom";
import "./Generation.css";
import Gallery from "../components/Gallery";
import GenerationStyling from "../components/GenerationStyling";
import useMatchHeight from "../components/useMatchHeight";
import Timer from "../components/Timer";
import UserImages from "../components/UserImages";

const Generation = () => {
  const [prompt, setPrompt] = useState("");
  const [features, setFeatures] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [isUploaded, setIsUploaded] = useState(false);
  const navigate = useNavigate();
  const { generateRef } = useMatchHeight("generateRef");
  const [galleryHeight, setGalleryHeight] = useState("auto");
  const [initialTime, setInitialTime] = useState(0);
  const [showTimer, setShowTimer] = useState(false);
  const authContext = useContext(AuthContext);
  const userId = authContext.userID;

  console.log(showTimer);

  const handlePromptChange = (e) => {
    setPrompt(e.target.value);
  };

  const onFeatureSelect = (featureName, value) => {
    setFeatures((prev) => ({ ...prev, [featureName]: value }));
  };

  const handleSubmit = () => {
    const featureString = Object.entries(features)
      .filter(([_, value]) => value)
      .map(([key, value]) => `${key}: ${value}`)
      .join(", ");

    const combinedPrompt = `${prompt}, ${featureString}`;

    setIsLoading(true);

    const createImageQuery = JSON.stringify({
      query:
        "mutation($prompt: String!) { createImage(prompt: $prompt) { id imageURL } }",
      variables: { prompt: combinedPrompt },
    });

    // First, fetch to create the image
    fetch("http://localhost:8080/generation/graphql", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: createImageQuery,
    })
      .then((response) => response.json())
      .then((data) => {
        if (data && data.data && data.data.createImage) {
          setIsLoading(false);
          const imageUrl = data.data.createImage.imageURL;
          const imageNameInput = data.data.createImage.id;
          const imageDescriptionInput = prompt;
          const imagePublishedYN = false;
          const usrId = userId;

          console.log("imageUrl: ", imageUrl);
          console.log("imageNameInput: ", imageNameInput);
          console.log("imageDescriptionInput: ", imageDescriptionInput);

          // Define the GraphQL mutation for saving to gallery
          const saveToGalleryMutation = JSON.stringify({
            query: `mutation ($imageUrl: String!, $imageNameInput: String!, $imageDescriptionInput: String!, $imagePublishedYN: Boolean!, $usrId: String!) {
            saveToImageAndGalleryTable(imageUrl: $imageUrl, imageNameInput: $imageNameInput, imageDescriptionInput: $imageDescriptionInput, imagePublishedYN: $imagePublishedYN, usrId: $usrId)
          }`,
            variables: {
              imageUrl: imageUrl,
              imageNameInput: imageNameInput,
              imageDescriptionInput: imageDescriptionInput,
              imagePublishedYN: imagePublishedYN,
              usrId: usrId,
            },
          });

          // Fetch to save the image to the gallery
          fetch("http://localhost:8080/gallery/graphql", {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: saveToGalleryMutation,
          })
            .then((response) => response.json())
            .then((data) => {
              console.log(
                "Success on saving to gallery: ",
                data.data.saveToImageAndGalleryTable
              );
              setIsLoading(false); // Stop loading
              setIsUploaded(false); // Set uploaded state

              const imageId = data.data.saveToImageAndGalleryTable;

              // Navigate to the selected-image route or any other desired route
              navigate("/selected-image", {
                state: {
                  imageId: imageId,
                  imageUrl: imageUrl,
                  prompt: combinedPrompt,
                  isUploaded: false,
                  showTimer: showTimer,
                  initialTime: initialTime,
                },
              });
            })
            .catch((error) => {
              console.error("Error saving the image to gallery:", error);
              setIsLoading(false); // Stop loading
            });
        } else {
          console.error("No image created to save.");
          setIsLoading(false); // Stop loading
        }
      })
      .catch((error) => {
        console.error("Error creating the image:", error);
        setIsLoading(false); // Stop loading
      });
  };

  // ------ Timer ------
  const fetchRemainingTime = async (userId) => {
    const response = await fetch("http://localhost:8080/cart/graphql", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        query: `query($userId: ID!) { getRemainingCleanupTime(userId: $userId) }`,
        variables: { userId: userId },
      }),
    });
    const data = await response.json();
    //console.log(data);
    const time = data.data.getRemainingCleanupTime;
    setInitialTime(time);
    setShowTimer(time > 0);
  };

  const handleTimerEnd = () => {
    // Logic when timer ends
    setShowTimer(false);
    fetchRemainingTime(userId);
  };

  useEffect(() => {
    fetchRemainingTime(userId);
  }, []);

  useEffect(() => {
    const handleResize = () => {
      if (generateRef.current) {
        const generateDivHeight = generateRef.clientHeight;
        const adjustedHeight = 245;
        setGalleryHeight(adjustedHeight);
      }
    };

    handleResize();
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  // Cart icon link
  const handleNavigateCart = () => {
    navigate("/Cart");
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
          <textarea
            className="text-input"
            placeholder="Describe the image.."
            value={prompt}
            onChange={handlePromptChange}
          ></textarea>
          <h3>Define Style</h3>
          <GenerationStyling onFeatureSelect={onFeatureSelect} />
          {isLoading ? (
            <div className="loading-indicator">
              <button className="secondary-button">Loading..</button>
            </div>
          ) : (
            <button className="primary-button" onClick={handleSubmit}>
              Generate
            </button>
          )}
        </div>
      </div>
      <div id="Gallery">
        <div className="cart">
          <span className="timer-next-to-icon">
            {" "}
            {showTimer && (
              <Timer initialTime={initialTime} onTimerEnd={handleTimerEnd} />
            )}
          </span>
          <span className="icon icon-cart" onClick={handleNavigateCart}></span>
        </div>
        <div className="content">
          <h2>Print on Demand Products</h2>
          <div className="product-images">
            <img src="/images/mug.png" alt="Mug"></img>
            <img src="/images/shirt.png" alt="Shirt"></img>
            <img src="/images/polo.png" alt="Polo Shirt"></img>
          </div>
          <h2>Public Gallery</h2>
          <p className="gallery-text">
            Select an Image from our Community. Only 10 of each unique image
            available.
          </p>
          <div className="gallery">
            <Gallery containerHeight={galleryHeight} />
            <h2>Your Images</h2>
            <UserImages containerHeight={galleryHeight - 35} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Generation;
