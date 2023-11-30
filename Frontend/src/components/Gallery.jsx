// TO DO: Create frontend and connect to Gallery once api-gateway is set up
import { useState } from 'react'

const Gallery = () => {

    const {setGalleryImages, galleryImages} = useState("")
    const fetchRandomImage = () => {
        fetch('http://localhost:8085/graphql', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                query: `{
                randomImage {
                    id
                    link
                }
            }`
            }),
        })
            .then(response => response.json())
            .then(data => {
                setGalleryImages(data)
            })
            .catch(error => console.error('Error fetching data: ', error));
    }

    return (
        <div>
            <button onClick={fetchRandomImage()}></button>
            {galleryImages && (
                <p>Gallery Images Displayed</p>
            )}
        </div>
    )
}

export default Gallery;

