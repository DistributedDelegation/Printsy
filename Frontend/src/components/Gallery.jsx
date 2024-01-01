import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const Gallery = () => {
    const [imageUrls, setImageUrls] = useState([]);
    let galleryGraphqlEndpoint = "http://localhost:8080/gallery/graphql";
    const navigate = useNavigate();

    const getAllPublishedImages = () => {
        const query = JSON.stringify({
          query: "{ getAllPublishedImages }",
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
            setImageUrls(data.data.getAllPublishedImages);
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

    return (
      <div id="galleryImageContainer">
        {imageUrls.map((url, index) => (
          <div key={index} className="galleryImagesElement">
            <img className="galleryImages" src={url} alt="Gallery" onClick={() => handleImageClick(url)}/>
            <span className="icon heart-icon"></span>
            <span className="likesCount">10</span>
            <span className="imageCount">3/10</span>
          </div>
        ))}
      </div>
    );
};

export default Gallery;
