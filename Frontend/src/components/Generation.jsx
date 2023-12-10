import { useState } from 'react'

const Generation = () => {
    
    // Define GraphQL endpoint
    let galleryGraphqlEndpoint = "http://localhost:8086/graphql";

    const sanitizeInput = (input) => {
        // Basic sanitation just to demonstrate. You might need more complex logic.
        return input.trim().replace(/[^a-zA-Z0-9-_.:/?&=%]/g, '')
    }

    // Function to save an image via a GraphQL mutation
    const saveToImageAndGalleryTable = () => {
        const imageUrl = sanitizeInput(document.getElementById('imageUrlInput').value);       
        const imageNameInput = document.getElementById('imageNameInput').value;
        const imageDescriptionInput = document.getElementById('imageDescriptionInput').value;
        const imagePublishedYN = document.getElementById('imagePublishedYN').checked;
        const usrId = "XYZ123";

        if (imageUrl) {
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
            })
            .catch(error => {
            console.error('Error saving the image:', error);
            });
        } else {
        console.error('No image to save.');
        }
    }  

    return (
        <div>
            <input type="text" id="imageUrlInput" placeholder="Enter Image URL"/>
            <input type="text" id="imageNameInput" placeholder="Enter Image Name"/>
            <input type="text" id="imageDescriptionInput" placeholder="Enter Image Description"/>
            <input type="checkbox" id="imagePublishedYN" />
            <button id="saveImageBtn" onClick={saveToImageAndGalleryTable}>Save Image</button>           
        </div>
    )
}

export default Generation;

