import { useState, useEffect, useContext } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import Product from "../components/Product";
import useMatchHeight from "../components/useMatchHeight";
import "./Generation.css";
import "../components/ProdcutStyle.css";
import Timer from "../components/Timer";
import { AuthContext } from "../AuthContext";

const SelectedImage = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [isUploaded, setIsUploaded] = useState(false);
  const location = useLocation();
  const imageUrl = location.state?.imageUrl;
  const imageId = location.state?.imageId;
  const prompt = location.state?.prompt ? location.state.prompt : "Gallery";
  const navigate = useNavigate();
  const { generateRef } = useMatchHeight("generateRef");
  const [initialTime, setInitialTime] = useState(0);
  const [showTimer, setShowTimer] = useState(false);
  const [cartItemsChanged, setCartItemsChanged] = useState(false);
  const authContext = useContext(AuthContext);
  const userId = authContext.userID;

  // Gallery Images are deactivating upload button
  useEffect(() => {
    if (location.state?.uploadedImage) {
      setIsUploaded(location.state.uploadedImage);
    }
  }, [location.state?.uploadedImage]);

  // Back to home link
  const handleNavigateHome = () => {
    navigate("/");
  };

  console.log("Current Image Id:" + imageId);
  console.log("Current Image Url:" + imageUrl);

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
    console.log(data);
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
  }, [cartItemsChanged]);

  const onCartChange = () => {
    setCartItemsChanged(!cartItemsChanged);
  };

  // Cart icon link
  const handleNavigateCart = () => {
    navigate("/Cart");
  };

  // Showing products
  const products = [
    {
      name: "Mug",
      productImageUrl: "/images/mug.png",
      price: 10,
      sizes: ["onesize"],
      overlayPosition: { top: "172px", left: "113px", widthHeight: "110px" },
    },
    {
      name: "T-Shirt",
      productImageUrl: "/images/shirt.png",
      price: 20,
      sizes: ["S", "M", "L"],
      overlayPosition: { top: "105px", left: "124px", widthHeight: "130px" },
    },
    {
      name: "Polo-Shirt",
      productImageUrl: "/images/polo.png",
      price: 30,
      sizes: ["S", "M", "L"],
      overlayPosition: { top: "100px", left: "207px", widthHeight: "45px" },
    },
  ];

  if (!imageUrl || !imageId) {
    return (
      <div id="gridTemplate">
        <div id="Generate">
          <div className="generate-header">
            <a href="/">
              <img src="/images/printsyLogo.svg" alt="Logo" className="logo" />
            </a>
          </div>
          <div className="content">
            <h2>Selected Image</h2>
            <p className="subtitle">
              Error no image has been generated/selected.
            </p>
          </div>
        </div>
        <div id="Gallery"></div>
      </div>
    );
  }

  const handleSubmit = () => {
    // Define the GraphQL mutation
    const mutation = JSON.stringify({
      query: `
        mutation($imageId: String!) {
          makeImagePublic(imageId: $imageId)
        }`,
      variables: {
        imageId: imageId,
      },
    });

    // Set GraphQL endpoint
    let endpoint = "http://localhost:8080/gallery/graphql";

    // Send the request to the GraphQL endpoint
    fetch(endpoint, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: mutation,
    })
      .then((response) => response.json())
      .then((data) => {
        console.log("Mutation Response: ", data.data.makeImagePublic);
        if (data.data.makeImagePublic) {
          setIsUploaded(true);
        } else {
          // Handle errors or unsuccessful mutation
          console.error("Mutation unsuccessful or returned false.");
        }
        setIsLoading(false);
      })
      .catch((error) => {
        console.error("Error in mutation:", error);
        setIsLoading(false);
      });
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
          <h2>Selected Image</h2>
          <p className="subtitle">Your selected Image.</p>
          <img className="placeholder-square" src={imageUrl} alt="Selected" />
          {isLoading ? (
            <div className="loading-indicator">
              <button className="secondary-button">Loading..</button>
            </div>
          ) : (
            <div>
              {isUploaded ? (
                <button
                  className="secondary-button"
                  style={{ cursor: "no-drop" }}
                >
                  Successfully uploaded
                </button>
              ) : (
                <button className="primary-button" onClick={handleSubmit}>
                  Save to Public Gallery
                </button>
              )}
            </div>
          )}
          <button className="secondary-button" onClick={handleNavigateHome}>
            Generate New Image
          </button>
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
          <h2>Products</h2>
          <div className="prodcut-preview">
            {products.map((product, index) => (
              <Product
                key={index}
                imageId={imageId}
                imageUrl={imageUrl}
                product={product}
                onCartChange={onCartChange}
              />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default SelectedImage;
