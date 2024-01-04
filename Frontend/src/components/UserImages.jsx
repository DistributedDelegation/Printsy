import { useEffect, useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../AuthContext";

const UserImages = () => {
  const [userImages, setUserImages] = useState([]);
  let galleryGraphqlEndpoint = "http://localhost:8080/gallery/graphql";
  const navigate = useNavigate();

  const authContext = useContext(AuthContext);
  const userID = authContext.userID;

  const getUserImages = () => {
    if (userID) {
      const query = JSON.stringify({
        query: `query(
                $userId: ID!
                ) {
                  getUserImages(userId: $userId) {
                    imageId
                    imageUrl
                  }
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
          if (data && data.data.getUserImages) {
            setUserImages(data.data.getUserImages);
          }
        })
        .catch((error) => {
          console.error("Error fetching the image:", error);
        });
    }
  };

  const handleImageClick = (imageId, url) => {
    console.log("/selectimage imageURL: " + url);
    navigate("/selected-image", {
      state: { imageId: imageId, imageUrl: url, uploadedImage: true },
    });
  };

  useEffect(() => {
    getUserImages();
  }, []);

  return (
    <div id="user-images-container">
      <h2>Your Images</h2>
      <div id="user-images">
        {userImages.map(({ imageId, imageUrl }) => (
          <div key={imageId} id={imageId} className="galleryImagesElement">
            <img
              className="galleryImages"
              src={imageUrl}
              alt="Gallery"
              onClick={() => handleImageClick(imageId, imageUrl)}
            />
          </div>
        ))}
      </div>
    </div>
  );
};

export default UserImages;
