import { useEffect, useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../AuthContext";

const Gallery = () => {
  const [images, setImages] = useState([]);
  const [imageIds, setImageIds] = useState([]);
  let galleryGraphqlEndpoint = "http://localhost:8080/gallery/graphql";
  let cartGraphqlEndpoint = "http://localhost:8080/cart/graphql";
  let transactionGatewayGraphqlEndpoint =
    "http://localhost:8080/transaction/graphql";
  const navigate = useNavigate();

  const authContext = useContext(AuthContext);
  const userID = authContext.userID; // Use authContext.userID directly

  const getAllPublishedImageCount = () => {
    for (let i = 0; i < imageIds.length; i++) {
      let imageId = imageIds[i];
      let count = 10;

      const cartQuery = JSON.stringify({
        query: `query(
            $imageId: ID!
            ) {
              findImageByImageId(imageId: $imageId)
          }`,
        variables: {
          imageId: imageId,
        },
      });

      const transactionQuery = JSON.stringify({
        query: `query(
            $imageId: String!
            ) {
              checkImageTransactionCount(imageId: $imageId) {
                count
              }
          }`,
        variables: {
          imageId: imageId,
        },
      });

      fetch(cartGraphqlEndpoint, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: cartQuery,
      })
        .then((response) => response.json())
        .then((data) => {
          count = count - data.data.findImageByImageId;

          fetch(transactionGatewayGraphqlEndpoint, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: transactionQuery,
          })
            .then((response) => response.json())
            .then((data) => {
              count = count - data.data.checkImageTransactionCount.count;
              console.log("imageCount: " + count);
              document
                .getElementById(imageId)
                .getElementsByClassName("imageCount")[0].innerText =
                count + "/10";
            })
            .catch((error) => {
              console.error("Error fetching the image:", error);
            });
        })
        .catch((error) => {
          console.error("Error fetching the image:", error);
        });
    }
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
  }, 500);

  const handleImageClick = (imageId, url) => {
    console.log("/selectimage imageURL: " + url);
    navigate("/selected-image", {
      state: { imageId: imageId, imageUrl: url, uploadedImage: true },
    });
  };

  const handleLikeClick = (imageId) => {
    const mutation = JSON.stringify({
      query: `mutation(
          $imageId: String!
          ) {
            saveIncreasedLikeCount(imageId: $imageId)
        }`,
      variables: {
        imageId: imageId,
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
      })
      .catch((error) => {
        console.error("Error saving the image:", error);
      });
  };

  return (
    <div id="galleryImageContainer">
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
