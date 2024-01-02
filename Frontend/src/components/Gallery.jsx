import { useEffect, useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../AuthContext';

const Gallery = () => {
    const [images, setImages] = useState([]);
    const [imageIds, setImageIds] = useState([]);
    const [imageCount, setImageCount] = useState({});
    let galleryGraphqlEndpoint = "http://localhost:8080/gallery/graphql";
    let cartGraphqlEndpoint = "http://localhost:8080/cart/graphql";
    let transactionGatewayGraphqlEndpoint = "http://localhost:8080/transaction/graphql";
    const navigate = useNavigate();

    const authContext = useContext(AuthContext);
    const userID = authContext.userID; // Use authContext.userID directly

    const test = () => {
        console.log("userID: " + userID);
    }
    
    const getAllPublishedImageCount = () => {
      // Waiting until Cart uses Alphanumeric imageId, issue with connecting to Transaction Gateway due to CORS, will be fixed by API Gateway
      // for (let imageId in imageIds) {
      //   console.log(imageIds[imageId]);
      //   const query = JSON.stringify({
      //     query: `query(
      //       $imageId: String!
      //       ) {
      //         saveIncreasedLikeCount(imageId: $imageId)
      //     }`,
      //     variables: {
      //       imageId: imageIds[imageId],
      //     }
      //   });
  
      //   fetch(transactionGatewayGraphqlEndpoint, {
      //     method: 'POST',
      //     headers: {
      //       'Content-Type': 'application/json'
      //     },
      //     body: query
      //   })
      //   .then(response => response.json())
      //   .then(data => {
      //     console.log(data);
      //   })
      //   .catch(error => {
      //     console.error('Error fetching the image:', error);
      //   });
      // }
    }

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
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: query
        })
        .then(response => response.json())
        .then(data => {
          if (data && data.data.getAllPublishedImages) {
            setImages(data.data.getAllPublishedImages);
            setImageIds(data.data.getAllPublishedImages.map(({imageId}) => imageId));
            getAllPublishedImageCount();
          }
        })
        .catch(error => {
          console.error('Error fetching the image:', error);
        });
    };

    useEffect(() => {
      getAllPublishedImages();
    }, []);

    const handleImageClick = (url) => {
      console.log("/selectimage imageURL: " + url)
      navigate('/selected-image', { state: { imageURL: url, uploadedImage: true } });
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
        }
      });

      fetch(galleryGraphqlEndpoint, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: mutation
      })
        .then(response => response.json())
        .then(data => {
            document.getElementById(imageId).getElementsByClassName("likesCount")[0].innerHTML = data.data.saveIncreasedLikeCount;
        })
        .catch(error => {
            console.error('Error saving the image:', error);
        });
    };

    return (
      <div id="galleryImageContainer">
        <button onClick={() => test()}>Run Test</button>
        {images.map(({imageId, imageUrl, likeCount}) => (
          <div key={imageId} id={imageId} className="galleryImagesElement">
            <img className="galleryImages" src={imageUrl} alt="Gallery" onClick={() => handleImageClick(imageUrl)}/>
            <span className="icon heart-icon" onClick={() => handleLikeClick(imageId)}></span>
            <span className="likesCount">{likeCount}</span>
            <span className="imageCount">3/10</span>
          </div>
        ))}
      </div>
    );
};

export default Gallery;
