import { useState, useEffect } from 'react';
import { useLocation, useNavigate } from "react-router-dom";
import Product from '../components/Product';
import useMatchHeight from '../components/useMatchHeight';
import "./Home.css";
import "../components/ProdcutStyle.css";

const SelectedImage = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [isUploaded, setIsUploaded] = useState(false);
  const location = useLocation();
  const imageURL = location.state?.imageURL;
  const prompt = location.state?.prompt ? location.state.prompt : "Gallery";
  const navigate = useNavigate();
  const { generateRef } = useMatchHeight('generateRef');

  // Gallery Images are deactivating upload button
  useEffect(() => {
    if (location.state?.uploadedImage) {
      setIsUploaded(location.state.uploadedImage);
    }
  }, [location.state?.uploadedImage]);

  // Back to home link
  const handleNavigateHome = () => {
    navigate('/');
  };

  // Cart icon link
  const handleNavigateCart = () => {
    navigate('/Cart');
  };

  // Showing products
  const products = [
    {
      name: "Mug",
      productImageUrl: "/images/mug.png",
      price: 10,
      sizes: ["onesize"],
      overlayPosition: { top: '172px', left: '113px', widthHeight: '110px' }
    },
    {
      name: "T-Shirt",
      productImageUrl: "/images/shirt.png",
      price: 20,
      sizes: ["S", "M", "L"],
      overlayPosition: { top: '105px', left: '124px', widthHeight: '130px' }
    },
    {
      name: "Polo-Shirt",
      productImageUrl: "/images/polo.png",
      price: 30,
      sizes: ["S", "M", "L"],
      overlayPosition: { top: '100px', left: '207px', widthHeight: '45px' }
    },
  ];

  if (!imageURL) {
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
            <p className="subtitle">Error no image has been generated/selected.</p>
          </div>
        </div>
        <div id="Gallery"></div>
      </div>
    );
  }

  const handleSubmit = () => {
    const query = JSON.stringify({
      query: "mutation($url: String!) { uploadImage(url: $url) { id imageURL } }",
      variables: { url: imageURL }
    });

    setIsLoading(true);

    fetch("http://localhost:8089/graphql", {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: query
    })
      .then(response => response.json())
      .then(data => {
        if (data && data.data && data.data.uploadImage) {
          // Load public URL into gallary
          const imageUrl = data.data.uploadImage.imageURL;
          const imageNameInput = data.data.uploadImage.id;
          const imageDescriptionInput = prompt;
          const imagePublishedYN = true;
          const usrId = "XYZ123";

          console.log("imageUrl: ", imageUrl);
          console.log("imageNameInput: ", imageNameInput);
          console.log("imageDescriptionInput: ", imageDescriptionInput);

          // Define the GraphQL mutation
          const mutation = JSON.stringify({
            query: `mutation(
              $imageUrl: String!, 
              $imageNameInput: String!, 
              $imageDescriptionInput: String!, 
              $imagePublishedYN: Boolean!, 
              $usrId: String!
              ) {
                saveToImageAndGalleryTable(imageUrl: $imageUrl, 
                imageNameInput: $imageNameInput, 
                imageDescriptionInput: $imageDescriptionInput, 
                imagePublishedYN: $imagePublishedYN, 
                usrId: $usrId)
            }`,
            variables: {
              imageUrl: imageUrl,
              imageNameInput: imageNameInput,
              imageDescriptionInput: imageDescriptionInput,
              imagePublishedYN: imagePublishedYN,
              usrId: usrId
            }
          });

          // Define GraphQL endpoint
          let galleryGraphqlEndpoint = "http://localhost:8088/graphql";

          // Send the request to the GraphQL endpoint
          fetch(galleryGraphqlEndpoint, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json'
            },
            body: mutation
          })
            .then(response => response.json())
            .then(data => {
              console.log("Sucess on Generation: ", data.data.saveToImageAndGalleryTable);
              setIsLoading(false);
              setIsUploaded(true);
            })
            .catch(error => {
              console.error('Error saving the image:', error);
              setIsLoading(false);
            });
        } else {
          console.error('No image to save.');
          setIsLoading(false);
        }
      })
      .catch(error => {
        console.error('Error:', error);
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
          <img className="placeholder-square" src={imageURL} alt="Selected" />
          {isLoading ? (
            <div className="loading-indicator">
              <button className="secondary-button">Loading..</button>
            </div>
          ) : (
            <div>
              {isUploaded ? (
                <button className="secondary-button" style={{ cursor: "no-drop" }}>Successfully uploaded</button>
              ) : (
                <button className="primary-button" onClick={handleSubmit}>Save to Public Gallery</button>
              )}
            </div>
          )}
          <button className="secondary-button" onClick={handleNavigateHome}>Generate New Image</button>
        </div>
      </div><div id="Gallery">
        <div className="cart">
          <span className="icon icon-cart" onClick={handleNavigateCart}></span>
        </div>
        <div className="content">
          <h2>Products</h2>
          <div className="prodcut-preview">
            {products.map((product, index) => (
              <Product key={index} imageUrl={imageURL} product={product} />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default SelectedImage;
