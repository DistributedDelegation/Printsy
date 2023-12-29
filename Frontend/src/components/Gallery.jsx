import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const Gallery = () => {
    const [images, setImages] = useState([]);
    let galleryGraphqlEndpoint = "http://localhost:8088/graphql";
    const navigate = useNavigate();

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
