### Preliminary Gallery Specs

GET /gallery/allImages
Returns all images, their transaction count, and likes

GET /gallery/userPublishedImages/{userId}
Request Body: { userId }
Returns all published images by a user

GET /gallery/userSavedImages/{userId}
Request Body: { userId }
Returns all saved images of a user

POST /gallery/saveImage
Request Body: { userId, imageId }
Saves an image to the personal gallery