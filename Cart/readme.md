### Cart Specs

MongoDB supports multi-document transactions, for updating multiple tables with different parameters at the same time.
Basic Logic = 
- Selected image and stock connected to product is immediately reduced from those tables upon product generation.
Cart Logic = 
- Every time a product list manipulation occurs (one of the products is changed by the user, one of the products is removed from the cart, or a new product is added), the cart's timestamp is updated to the current time.
Timer Logic =
- The timer is 10 minutes.
- If the end_timestamp of the user's userId is older than current time, then the purchase was unsuccessful and the cart is deleted. The product in Product table is deleted, and changes to Image and Stock tables are reversed.
- Purchase is successful if user proceeds with payment page. The cart information is given to the payment service, and the cart entry is saved as a record of purchases. No further changes need to be done to Image, Stock, or Product tables.


# Data Types & Schemas

#### Image Table (MongoDB) --> Tania & Florian; can be used by Gallery --> Michael

| Field            | Data Type          | Details                           | Example                                  |
|------------------|--------------------|-----------------------------------|------------------------------------------|
| imageId          | ID                 | Primary key for the image         | 22212345                                 | <--- created during image generation
| imageData        | String             |                                   | "(binary data)"                          |
| imageUrl         | String             |                                   | "http://example.com/image.jpg"           |
| imageName        | String             | From image generation             | "Starry Night"                           |
| imageDescription | String             | From image generation             | "A beautiful sky inspired by Van Gogh"   |
| imageSurcharge   | Float              | Addditional costs from algorithm  | 59.99                                    |
| imageRating      | Float              | From rating feature in gallery    | 4.8       (or NA if not in gallery)      | <--- rating between 1-10 hearts from gallery
| isPublished      | Boolean            |                                   | TRUE for in gallery, FALSE for not       |
| imageTimestamp   | String/Integer     | for the case of personal generation, item should be deleted after certain time? | |
| imageLimit       | Integer            | Limits products from this at 10!  | 10                                       | <--- updated upon cart storage, undone without
                                                                                                                              purchase

#### Stock Table (MySQL) --> Cole

| Field            | Data Type          | Details                           | Example                                      |
|------------------|--------------------|-----------------------------------|----------------------------------------------|
| stockId          | ID                 | Primary key for the stock item    | 33312345                                     | ----> stockId allows to store combo and 
| productType      | String             |                                   | "CUP", SHIRT, HOODIE, HAT, STICK             |       always check against stockQuantity 
| productSize      | String             | only applicable for SHIRT, HOODIE | "XS", S, M, L, XL, XXL,      NA              |
| productColor     | String             | only applicable for SHIRT, HOODIE | "YELLOW", RED, GREEN, BLUE, BLACK, WHITE, NA |
| basePrice        | Float              |                                   | 19.99                                        |
| stockQuantity    | Integer            |                                   | 80                                           | <--- updated upon cart storage, undone    
                                                                                                                                  without purchase
                                                                                                                    
#### Product Table (MySQL) --> Cole

| Field            | Data Type          | Details                           | Example                                  |
|------------------|--------------------|-----------------------------------|------------------------------------------|
| productId        | ID                 | Primary key desired product combo | 44412345                                 | ---> created during product selection
| imageId          | ID                 | Image Table foreign key           | 22212345                                 | ---> checked for availability
| stockId          | ID                 | Stock Table foreign key           | 33312345                                 | ---> checked for availability
| productPrice     | Float              | basePrice + imageSurcharge        | 79.98                                    | ---> aggregation simplifies payment operation
| productQuantity  | Integer            | checked against Image AND Stock   | 3       (in most cases, 1)               |  


#### Cart Table (MongoDB) --> Cole

| Field            | Data Type          | Details                              | Example               |
|------------------|--------------------|--------------------------------------|-----------------------|
| cartId           | ID                 | Primary key for cart                 | 55512345              |
| userId           | ID                 | Foreign Key with User table          | 66612345              |
| productList      | List  of Products  | [{ productId: 44412345,              |                       | 
|                  |                    |    imageId: 22212345,                |                       |
|                  |                    |    stockId: 33312345,                |                       |
|                  |                    |    productPrice: 79.98,              |                       |
|                  |                    |    productQuantity: 3   },           |                       |
|                  |                    |  { productId: 44412346,              |                       |
|                  |                    |    ...                  }]           |                       |
| lastUpdatedTime  | String/Integer     | Current timestamp                    | "2023-03-10 02:00:00" |
| expirationTime   | String/Integer     | Timestamp of cart expiration (+10min)| "2023-03-10 02:10:00" | ---> where expirationTime is older than system's time, delete


#### User Table --> Tania

| Field            | Data Type          | Details                              | Example               |
|------------------|--------------------|--------------------------------------|-----------------------|
| userId           | ID                 | Primary key for users                | 66612345              |
| userName         | String             | Unique username                      | "john_doe"            |
| passwordHash     | String             | Hashed password                      | "(hashed data)"       |
| userEmail        | String             | User's email address                 | "john@example.com"    |
| registrationDate | String/Integer     | Timestamp of account creation        | "2023-03-10 02:00:00" |
... additional user information or better not?


#### Purchase Table --> Tania

| Field            | Data Type          | Details                              | Example               |
|------------------|--------------------|--------------------------------------|-----------------------|
| purchaseId       | ID                 | Primary key for purchases            | 77712345              |
| userId           | ID                 | User ID who made the purchase        | 66612345              |
| purchaseDate     | String/Integer     | Timestamp of purchase                | "2023-03-10 02:15:00" |
| productList      | List  of Products  | [{ productId: 44412345,              |                       | 
|                  |                    |    imageId: 22212345,                |                       |
|                  |                    |    stockId: 33312345,                |                       |
|                  |                    |    productPrice: 79.98,              |                       |
|                  |                    |    productQuantity: 3   },           |                       |
|                  |                    |  { productId: 44412346,              |                       |
|                  |                    |    ...                  }]           |                       |
| totalPrice       | Float              | All productPrices + shipping costs   | 159.97                |
| blockchainRef    | ???                | ???                                  | ???                   |


#### User's Like Table - Michael

| Field            | Data Type          | Details                              | Example               |
|------------------|--------------------|--------------------------------------|-----------------------|
| likeId           | ID                 | Primary key for like record          | 88812345              |
| userId           | ID                 | User ID who liked the image          | 66612345              |
| imageId          | ID                 | Image that was liked                 | 22212345              | ---> updates the Image table with total likes
| likeDate         | String/Integer     | Date and time when  image was liked  | "2023-03-10 02:00:00" | ---> allows filtering by date & updates to deleted images





---------------------

POST /cart/add
Request Body: { userId, itemId, quantity }
Adds items to the cart

DELETE /cart/remove
Request Body: { userId, itemId }
Removes item from the cart

GET /cart/timer
Response: { timeLeft: <seconds> }
Returns time left for the cart timer

POST /cart/resetTimer
Request Body: { userId }
Resets the timer for all items in the cart

### Product/Stock Service

5. Product/Stock Service: (Cole - table schema)
-> relational DB (MySQL)
GET /stock/items
Returns all products with their inventory details
GET /stock/item/{itemId}
Returns details of a specific item
POST /stock/updateItem
Request Body: { itemId, quantity }
Updates the stock quantity of an item
