import { useState } from 'react'

const Gallery = () => {
    const {setGalleryImages, galleryImages} = useState("")
    
    // Define GraphQL endpoint
    let mongoGraphqlEndpoint = "http://localhost:8086/graphql";
    let SQLGraphqlEndpoint = "http://localhost:8086/graphql";

    const sanitizeInput = (input) => {
        // Basic sanitation just to demonstrate. You might need more complex logic.
        return input.trim().replace(/[^a-zA-Z0-9-_.:/?&=%]/g, '')
    }

    const generateMongoImage = () => {
        const imageUrl = document.getElementById('imageUrlInput').value;
        const sanitizedUrl = sanitizeInput(imageUrl);
      
        console.log(sanitizedUrl);
      
        const query = JSON.stringify({
          query: "query($url: String!) { fetchMongoImage(url: $url) }",
          variables: {
            url: sanitizedUrl
          }
        });
      
        console.log(`Sending the following query to both ${mongoGraphqlEndpoint}: ${query}`);
      
        // Send the request to the GraphQL endpoint
        fetch(mongoGraphqlEndpoint, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: query
        })
          .then(response => response.json())
          .then(data => {
            const imageUrl = data.data.fetchMongoImage;
            const imageDisplay = `<p>Image URL: ${imageUrl}</p><img id="mongoImageToSave" src=${imageUrl}>`;
            document.getElementById('mongoImageContainer').innerHTML = imageDisplay
          })
          .catch(error => {
            console.error('Error fetching the image:', error);
          });
    }

    // Function to save an image via a GraphQL mutation
    const saveMongoImage = () => {
        const imageElement = document.getElementById('mongoImageToSave');
        if (imageElement) {
        // Extract the Base64 part of the data URL
        const imageUrl = imageElement.src;
    
        // Define the GraphQL mutation
        const mutation = JSON.stringify({
            query: `mutation($imageUrl: String!) {
            createMongoImage(imageUrl: $imageUrl)
            }`,
            variables: {
            imageUrl: imageUrl
            }
        });
    
        // Send the request to the GraphQL endpoint
        fetch(mongoGraphqlEndpoint, {
            method: 'POST',
            headers: {
            'Content-Type': 'application/json'
            },
            body: mutation
        })
            .then(response => response.json())
            .then(data => {
            console.log('Image saved, ID:', data.data.createMongoImage);
            const sucessDisplay = `<p>Image saved in MongoDB, ID: ${data.data.createMongoImage}</p>`;
            document.getElementById('mongoSuccessContainer').innerHTML = sucessDisplay;
            })
            .catch(error => {
            console.error('Error saving the image:', error);
            });
        } else {
        console.error('No image to save.');
        }
    }

    const generateSQLImage = () => {
      const imageUrl = document.getElementById('imageUrlInput').value;
      const sanitizedUrl = sanitizeInput(imageUrl);
    
      console.log(sanitizedUrl);
    
      const query = JSON.stringify({
        query: "query($url: String!) { fetchSQLImage(url: $url) }",
        variables: {
          url: sanitizedUrl
        }
      });
    
      console.log(`Sending the following query to both ${SQLGraphqlEndpoint}: ${query}`);
    
      fetch(SQLGraphqlEndpoint, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: query
        })
          .then(response => response.json())
          .then(data => {
            const imageUrl = data.data.fetchSQLImage;
            const imageDisplay = `<p>Image URL: ${imageUrl}</p><img id="sqlImageToSave" src=${imageUrl}>`;
            document.getElementById('SQLImageContainer').innerHTML = imageDisplay
          })
          .catch(error => {
            console.error('Error fetching the image:', error);
          });
      }

      const saveSQLImage = () => {
        const imageElement = document.getElementById('sqlImageToSave');
        if (imageElement) {
        // Extract the Base64 part of the data URL
        const imageUrl = imageElement.src;
    
        // Define the GraphQL mutation
        const mutation = JSON.stringify({
            query: `mutation($imageUrl: String!) {
            createSQLImage(imageUrl: $imageUrl)
            }`,
            variables: {
            imageUrl: imageUrl
            }
        });
        fetch(SQLGraphqlEndpoint, {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json'
              },
              body: mutation
            })
              .then(response => response.json())
              .then(data => {
                console.log('Image saved, ID:', data.data.createSQLImage);
                const sucessDisplay = `<p>Image saved in MySql, ID: ${data.data.createSQLImage}</p>`;
                document.getElementById('SQLSuccessContainer').innerHTML = sucessDisplay;
              })
              .catch(error => {
                console.error('Error saving the image:', error);
              });

        } else {
        console.error('No image to save.');
        }
    }
      
      const generateImage = () => {
        generateMongoImage();
        generateSQLImage();
      }

      const saveImage = () => {
        saveMongoImage();
        saveSQLImage();
      }
  

    return (
        <div>
            <input type="text" id="imageUrlInput" placeholder="Enter Image URL"/>
            <button id="generateImageBtn" onClick={generateImage}>Get Image</button>
            <button id="saveImageBtn" onClick={saveImage}>Save Image</button>

            <div id="results" style={{marginTop: "30px", display: "flex", flexDirection: "row"}}>
                <div id="mongo-results" style={{marginRight: "30px"}}>
                    <div id="mongoImageContainer"></div>
                    <div id="mongoSuccessContainer"></div>
                </div>
                <div id="SQL-results" style={{marginRight: "30px", marginTop:"1000px"}}>
                    <div id="SQLImageContainer"></div>
                    <div id="SQLSuccessContainer"></div>
                </div>
            </div>            
        </div>
    )
}

export default Gallery;

