# How to interact with the Cart


## 1. Send a Product information (Long imageId, Long stockId, Int price, Long userId) to the Cart like this:

{
    "query": "mutation addItemtoCart($imageId: ID!, $stockId: ID!, $price: Int!, $userId: ID!) { addItemtoCart(imageId: $imageId, stockId: $stockId, price: $price, userId: $userId) }",
    "variables": {
        "imageId": "1",
        "stockId": "1",
        "price": 100,
        "userId": "1"
    }
}

You receive the following response options:

    a. It worked, item is being added to the user's Cart.

    {
        "data": {
            "addItemtoCart": "successfully added"
        }
    }

    b. You reached the imageId limit of 10 (from transaction service and cart table storage)

    {
        "data": {
            "addItemtoCart": "limit exceeded"
        }
    }

    c. An error occurred, try again.

    {
        "data": {
            "addItemtoCart": "error occurred"
        }
    }

## 2. Request the remaining time before user's Cart items run out:

{
    "query": "{ getRemainingCleanupTime }"
}

You receive the following example response:

{
    "data": {
        "getRemainingCleanupTime": 102
    }
}

Time is given in seconds. As of the current setup, it is set to 120 seconds. When timer runs out or no items are in the Cart, the timer stands at 0 seconds.

## 3. When the user wants to purchase the items (which stops the scheduled deletion task, sends the information to the transaction service, and then deletes the items from the Cart):

{
    "query": "mutation CompletePurchase($userId: ID!) { completePurchase(userId: $userId) }",
    "variables": {
        "userId": "1"
    }
}

You receive the following response options:

a. The purchased items were successfully sent to the Transaction Service. 

{
    "data": {
        "completePurchase": true
    }
}

b. The purchased items were not successfully sent to the Transaction Service. Or an error occurred.

{
    "data": {
        "completePurchase": false
    }
}

## 4. Get all Cart items for a userId:

{
    "query": "query findCartItemsByUserId($userId: ID!) { findCartItemsByUserId(userId: $userId) { productId userId expirationTime } }",
    "variables": {
        "userId": "1"
    }
}

You receive the following response example:

{
    "data": {
        "findCartItemsByUserId": [
            {
                "productId": "333",
                "userId": "1",
                "expirationTime": "2023-12-26 12:57:05.578"
            },
            {
                "productId": "334",
                "userId": "1",
                "expirationTime": "2023-12-26 12:57:05.59"
            },
            {
                "productId": "335",
                "userId": "1",
                "expirationTime": "2023-12-26 12:57:05.601"
            },
            {
                "productId": "336",
                "userId": "1",
                "expirationTime": "2023-12-26 12:57:05.611"
            },
            {
                "productId": "337",
                "userId": "1",
                "expirationTime": "2023-12-26 12:57:05.623"
            }
        ]
    }
}