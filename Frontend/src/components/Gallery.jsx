import { useEffect, useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../AuthContext";

const Gallery = ({ containerHeight }) => {
  const [images, setImages] = useState([]);
  const [imageIds, setImageIds] = useState([]);
  let galleryGraphqlEndpoint = "http://localhost:8080/gallery/graphql";
  let cartGraphqlEndpoint = "http://localhost:8080/cart/graphql";
  let transactionGatewayGraphqlEndpoint =
    "http://localhost:8080/transaction/graphql";
  const navigate = useNavigate();

  const authContext = useContext(AuthContext);
  const userID = authContext.userID;

  const getUserLikedImages = () => {
    if (userID) {
      const query = JSON.stringify({
        query: `query(
            $userId: String!
            ) {
              getUserLikedImages(userId: $userId)
          }`,
        variables: {
          userId: userID,
        },
      });

      fetch(galleryGraphqlEndpoint, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: query,
      })
        .then((response) => response.json())
        .then((data) => {
          if (data && data.data.getUserLikedImages) {
            for (const element of data.data.getUserLikedImages) {
              let imageId = element;
              document
                .getElementById(imageId)
                .getElementsByClassName("heart-icon")[0].style.backgroundImage =
                'url("/images/heart-filled.svg")';
            }
          }
        })
        .catch((error) => {
          console.error("Error fetching the image:", error);
        });
    }
  };

  const getAllPublishedImageCount = () => {
    imageIds.forEach((imageId) => {
      // Assume a total of 10 images available initially
      let totalAvailable = 10;

      const cartQuery = JSON.stringify({
        query: `query($imageId: ID!) {
          findImageByImageId(imageId: $imageId)
        }`,
        variables: {
          imageId: imageId,
        },
      });

      const transactionQuery = JSON.stringify({
        query: `query($imageId: String!) {
          checkImageTransactionCount(imageId: $imageId) {
            count
          }
        }`,
        variables: {
          imageId: imageId,
        },
      });

      // Fetch cart count
      fetch(cartGraphqlEndpoint, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: cartQuery,
      })
        .then((response) => response.json())
        .then((cartData) => {
          const cartCount = cartData.data.findImageByImageId;
          Console.log("Cart count: " + cartCount);
          totalAvailable -= cartCount; // Subtract cart count from total available

          // Fetch transaction count
          fetch(transactionGatewayGraphqlEndpoint, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: transactionQuery,
          })
            .then((response) => response.json())
            .then((transactionData) => {
              const transactionCount =
                transactionData.data.checkImageTransactionCount.count;
              Console.log("transactionCount count: " + transactionCount);
              totalAvailable -= transactionCount; // Subtract transaction count from total available

              // Update UI with remaining count
              document
                .getElementById(imageId)
                .getElementsByClassName("imageCount")[0].innerText =
                totalAvailable + "/10";
            })
            .catch((transactionError) => {
              console.error(
                "Error fetching transaction count:",
                transactionError
              );
            });
        })
        .catch((cartError) => {
          console.error("Error fetching cart count:", cartError);
        });
    });
  };

  const getAllPublishedImages = () => {
    const query = JSON.stringify({
      query: `{
            getAllPublishedImages {
              imageId
              imageUrl
              likeCount
            }
          }`,
    });

    fetch(galleryGraphqlEndpoint, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: query,
    })
      .then((response) => response.json())
      .then((data) => {
        if (data && data.data.getAllPublishedImages) {
          setImages(data.data.getAllPublishedImages);
          setImageIds(
            data.data.getAllPublishedImages.map(({ imageId }) => imageId)
          );
          getAllPublishedImageCount();
        }
      })
      .catch((error) => {
        console.error("Error fetching the image:", error);
      });
  };

  useEffect(() => {
    getAllPublishedImages();
  }, []);

  setTimeout(() => {
    getAllPublishedImageCount();
    getUserLikedImages();
  }, 1000);

  const handleImageClick = (imageId, url) => {
    console.log("/selectimage imageURL: " + url);
    navigate("/selected-image", {
      state: { imageId: imageId, imageUrl: url, uploadedImage: true },
    });
  };

  const handleLikeClick = (imageId) => {
    if (
      document.getElementById(imageId).getElementsByClassName("heart-icon")[0]
        .style.backgroundImage === 'url("/images/heart-filled.svg")'
    ) {
      const mutation = JSON.stringify({
        query: `mutation(
            $imageId: String!,
            $userId: String!
            ) {
              saveDecreasedLikeCount(imageId: $imageId, userId: $userId)
          }`,
        variables: {
          imageId: imageId,
          userId: userID,
        },
      });

      fetch(galleryGraphqlEndpoint, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: mutation,
      })
        .then((response) => response.json())
        .then((data) => {
          document
            .getElementById(imageId)
            .getElementsByClassName("likesCount")[0].innerHTML =
            data.data.saveDecreasedLikeCount;
          document
            .getElementById(imageId)
            .getElementsByClassName("heart-icon")[0].style.backgroundImage =
            'url("/images/heart.svg")';
        })
        .catch((error) => {
          console.error("Error saving the image:", error);
        });
    } else {
      const mutation = JSON.stringify({
        query: `mutation(
            $imageId: String!,
            $userId: String!
            ) {
              saveIncreasedLikeCount(imageId: $imageId, userId: $userId)
          }`,
        variables: {
          imageId: imageId,
          userId: userID,
        },
      });

      fetch(galleryGraphqlEndpoint, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: mutation,
      })
        .then((response) => response.json())
        .then((data) => {
          document
            .getElementById(imageId)
            .getElementsByClassName("likesCount")[0].innerHTML =
            data.data.saveIncreasedLikeCount;
          document
            .getElementById(imageId)
            .getElementsByClassName("heart-icon")[0].style.backgroundImage =
            'url("/images/heart-filled.svg")';
        })
        .catch((error) => {
          console.error("Error saving the image:", error);
        });
    }
  };

  return (
    <div id="galleryImageContainer" style={{ height: containerHeight }}>
      {images.map(({ imageId, imageUrl, likeCount }) => (
        <div key={imageId} id={imageId} className="galleryImagesElement">
          <img
            className="galleryImages"
            src={imageUrl}
            alt="Gallery"
            onClick={() => handleImageClick(imageId, imageUrl)}
          />
          <span
            className="icon heart-icon"
            onClick={() => handleLikeClick(imageId)}
          ></span>
          <span className="likesCount">{likeCount}</span>
          <span className="imageCount">10/10</span>
        </div>
      ))}
    </div>
  );
};

export default Gallery;
