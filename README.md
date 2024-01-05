# Team Printsy - Group 4

## Team Members
* Cole Neumark
* Tania Lopes
* Florian Lechner
* Michael Moyles

## Product Summary
Printsy is a web application that allows users to create personalised merchandise, leveraging OpenAI's image generation service. The user can generate their own images or select from a public library of pre-generated artwork. The user can then chose to print these custom images on to mugs and t-shirts. To foster uniqueness and orginality, a key feature of Printsy is a 10-print cap across all publically available images.

The service is powered by an Authentication service for user account creation and user data storage, a Generation service that connects to the OpenAI API for image generation and storage in IBM Cloud and a Gallery Service that stores the images urls and images ids, number of likes. On selection of an image, the Cart service manages the check out process and the Transcation service stores the payment history and item purchased on the Prinsty blockchain as a historical record of the transaction. Finally, the Frontend React service connects with each of these services through the Printsy's API Gateway for better scalability, fault tolerance and load balancing of requests.

## How to Run the Code
### For the backend services: 
1. In the root directory, run `docker-compose build`
2. In the same root directory, run `docker-compose up`

### For the frontend service:
1. From the root directory, navigate to the Frontend folder with `cd Frontend`
2. In the Frontend folder, run `npm install`
3. In the Frontend folder, run `npm run dev`
