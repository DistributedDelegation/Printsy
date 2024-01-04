import { useEffect, useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../AuthContext";

const Gallery = ({ containerHeight }) => {
  const [images, setImages] = useState([]);
  const [imageIds, setImageIds] = useState([]);
  const [imageLikes, setImageLikes] = useState([]);
  let galleryGraphqlEndpoint = "http://localhost:8080/gallery/graphql";
  let cartGraphqlEndpoint = "http://localhost:8080/cart/graphql";
  let transactionGatewayGraphqlEndpoint =
    "http://localhost:8080/transaction/graphql";
  const navigate = useNavigate();

  const authContext = useContext(AuthContext);
  const userID = authContext.userID;

  useEffect(() => {
    getAllPublishedImages();
  }, []);

  const getUserLikedImages = (initialImages) => {
    console.log("getting user liked images");
    if (userID) {
      const query = JSON.stringify({
        query: `query($userId: String!) {
            getUserLikedImages(userId: $userId)
          }`,
        variables: { userId: userID },
      });

      return fetch(galleryGraphqlEndpoint, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: query,
      })
        .then((response) => response.json())
        .then((data) => {
          if (data && data.data.getUserLikedImages) {
            const likedImageIds = data.data.getUserLikedImages; // array of liked image IDs

            // Update the images state to reflect the liked status
            return initialImages.map((image) => ({
              ...image,
              isLikedByUser: likedImageIds.includes(image.imageId),
            }));
          } else {
            return initialImages; // If no data or no liked images, return the images as is
          }
        })
        .catch((error) => {
          console.error("Error fetching the image:", error);
          return initialImages; // In case of error, return the initial images as is
        });
    } else {
      return Promise.resolve(initialImages); // If no userID, return the initial images as is
    }
  };

  const updateImageAvailabilityCount = (image) => {
    const cartQuery = JSON.stringify({
      query: `query($imageId: ID!) {
        findImageByImageId(imageId: $imageId)
      }`,
      variables: {
        imageId: image.imageId,
      },
    });

    return fetch(cartGraphqlEndpoint, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: cartQuery,
    })
      .then((response) => response.json())
      .then((cartData) => {
        const cartCount = cartData.data.findImageByImageId || 0;
        console.log("Cart count for image " + image.imageId + ": " + cartCount);
        const totalAvailable = 10 - cartCount;
        return { ...image, availabilityCount: totalAvailable };
      })
      .catch((cartError) => {
        console.error(
          "Error fetching cart count for image " + image.imageId + ": ",
          cartError
        );
        return { ...image }; // In case of error, return the image unchanged
      });
  };

  const getAllPublishedImages = () => {
    console.log("getting all published images");
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
      headers: { "Content-Type": "application/json" },
      body: query,
    })
      .then((response) => response.json())
      .then((data) => {
        if (data && data.data.getAllPublishedImages) {
          const initialImages = data.data.getAllPublishedImages.map(
            (image) => ({
              ...image,
              availabilityCount: 10, // initial assumption
              isLikedByUser: false, // initial assumption
            })
          );

          // Update initialImages with liked status from getUserLikedImages
          getUserLikedImages(initialImages).then((updatedImages) => {
            setImages(updatedImages); // Set state with new liked status

            // For each image, update availability count
            Promise.all(updatedImages.map(updateImageAvailabilityCount)).then(
              (finalImages) => {
                setImages(finalImages); // Update state with new availability counts
              }
            );
          });

          setImageIds(
            data.data.getAllPublishedImages.map(({ imageId }) => imageId)
          );
        }
      })
      .catch((error) => {
        console.error("Error fetching the images:", error);
      });
  };

  const handleImageClick = (imageId, url) => {
    console.log("/selectimage imageURL: " + url);
    navigate("/selected-image", {
      state: { imageId: imageId, imageUrl: url, uploadedImage: true },
    });
  };

  const handleLikeClick = (imageId) => {
    const imageIndex = images.findIndex((image) => image.imageId === imageId);
    if (imageIndex === -1) return; // Image not found

    const image = images[imageIndex];
    if (image.isLikedByUser) {
      dislikeImage(imageId, imageIndex);
    } else {
      likeImage(imageId, imageIndex);
    }
  };

  const likeImage = (imageId, imageIndex) => {
    const mutation = JSON.stringify({
      query: `mutation($imageId: String!, $userId: String!) {
        saveIncreasedLikeCount(imageId: $imageId, userId: $userId)
      }`,
      variables: {
        imageId: imageId,
        userId: userID,
      },
    });

    // Perform the mutation to increase like count
    fetch(galleryGraphqlEndpoint, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: mutation,
    })
      .then((response) => response.json())
      .then((data) => {
        // Update the local state to reflect the new like status and count
        setImages((prevImages) =>
          prevImages.map((img, index) =>
            index === imageIndex
              ? {
                  ...img,
                  isLikedByUser: true,
                  likeCount: data.data.saveIncreasedLikeCount,
                }
              : img
          )
        );
      })
      .catch((error) => {
        console.error("Error liking the image:", error);
      });
  };

  const dislikeImage = (imageId, imageIndex) => {
    const mutation = JSON.stringify({
      query: `mutation($imageId: String!, $userId: String!) {
        saveDecreasedLikeCount(imageId: $imageId, userId: $userId)
      }`,
      variables: {
        imageId: imageId,
        userId: userID,
      },
    });

    // Perform the mutation to decrease like count
    fetch(galleryGraphqlEndpoint, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: mutation,
    })
      .then((response) => response.json())
      .then((data) => {
        // Update the local state to reflect the new like status and count
        setImages((prevImages) =>
          prevImages.map((img, index) =>
            index === imageIndex
              ? {
                  ...img,
                  isLikedByUser: false,
                  likeCount: data.data.saveDecreasedLikeCount,
                }
              : img
          )
        );
      })
      .catch((error) => {
        console.error("Error disliking the image:", error);
      });
  };

  return (
    <div id="galleryImageContainer" style={{ height: containerHeight }}>
      {images.map(({ imageId, imageUrl, likeCount, availabilityCount }) => (
        <div
          key={imageId}
          id={imageId}
          className="galleryImagesElement"
          style={{ opacity: availabilityCount === 0 ? 0.25 : 1 }}
        >
          <img
            className="galleryImages"
            src={imageUrl}
            alt="Gallery"
            onClick={() => handleImageClick(imageId, imageUrl)}
          />
          <span
            className="icon heart-icon"
            style={{
              backgroundImage: images.find((img) => img.imageId === imageId)
                .isLikedByUser
                ? 'url("/images/heart-filled.svg")'
                : 'url("/images/heart.svg")',
            }}
            onClick={() => handleLikeClick(imageId)}
          ></span>
          <span className="likesCount">{likeCount}</span>
          <span className="imageCount">{availabilityCount}/10</span>
        </div>
      ))}
    </div>
  );
};

export default Gallery;
