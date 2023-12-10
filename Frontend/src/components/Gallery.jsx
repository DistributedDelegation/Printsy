import { useEffect, useState } from 'react'

const Gallery = () => {
    // Define GraphQL endpoint
    let galleryGraphqlEndpoint = "http://localhost:8086/graphql";

    const getAllPublishedImages = () => {    
        const query = JSON.stringify({
          query: "{ getAllPublishedImages }",
        });
      
        // Send the request to the GraphQL endpoint
        fetch(galleryGraphqlEndpoint, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: query
        })
          .then(response => response.json())
          .then(data => {
            if (data) {
              console.log("GetAllPublishedImages: " + data.data.getAllPublishedImages)
              const imageUrls = data.data.getAllPublishedImages;
              let imageDisplay = imageUrls.map(url => `<p>Image URL: ${url}</p><img className="galleryImages" src=${url}>`).join('');
              document.getElementById('galleryImageContainer').innerHTML = imageDisplay;
            }
          })
          .catch(error => {
            console.error('Error fetching the image:', error);
          });
    }

    useEffect(() => {
      getAllPublishedImages();
    }, []);
  
    return (
        <div id="results" style={{marginTop: "30px", display: "flex", flexDirection: "row"}}>
              <div id="galleryResults" style={{marginRight: "30px"}}>
                  <div id="galleryImageContainer"></div>
              </div>       
        </div>
    )
}

export default Gallery;

