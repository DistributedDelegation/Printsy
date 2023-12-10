To expand on the functionalities of the Cart service, I want to create a Queue Service as its own container and then communicate with the Cart Service and the database via GraphQL. This is the workflow I want to implement:

1.	The Cart Service sends a request addItemToCart to the Queue Service.
2.	The Queue Service has a declared CartQueue class which implements the Queue interface. Pop requests off the queue, double-check that the image count in the Cart table still smaller than 10 and then add the item to the Cart table with addCartItem query.
a.	Queue Service sends a checkCartItemExistence request to the database to check if the item already exists. If it does not exist yet, a new cart item is created with given userId, stockId, imageId and current expirationTime timestamp. If it does exist, the existing cart item is overwritten with the new parameters and also gets a new expirationTime timestamp.
3.	Then, Queue Service sends a success indicator (could be Boolean) back to the Cart service.
4.	Once the Cart Service receives the success indicator as the response to addItemToCart, a scheduled delete task is created to delete all of the user’s cart items.


