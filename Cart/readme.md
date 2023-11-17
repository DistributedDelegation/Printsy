# Data Types & Schemas

#### Image Table --> MongoDB

| Field            | Data Type          | Details                           | Example                                  |
|------------------|--------------------|-----------------------------------|------------------------------------------|
| image_id          | ID                 | Primary key for the image         | 22212345                                 | <--- created during image generation
| image_url         | String             |                                   | "http://example.com/image.jpg"           |
| image_name        | String             | From image generation             | "Starry Night"                           |
| image_description | String             | From image generation             | "A beautiful sky inspired by Van Gogh"   |
| image_timestamp   | String/Integer     | for the case of personal generation, item should be deleted after certain time? | |

- index is the image_timestamp and image_id


#### Stock Table --> MongoDB

| Field            | Data Type          | Details                           | Example                                      |
|------------------|--------------------|-----------------------------------|----------------------------------------------|
| stock_id          | ID                 | Primary key for the stock item    | 33312345                                     | 
| stock_type        | String             |                                   | "CUP", SHIRT, HOODIE, HAT, STICKER           |   
| stockSize        | String             | only applicable for SHIRT, HOODIE | "XS", S, M, L, XL, XXL,      NA              |
| stockColor       | String             | only applicable for SHIRT, HOODIE | "YELLOW", RED, GREEN, BLUE, BLACK, WHITE, NA |
| basePrice        | Float              |                                   | 19.99                                        |

- index is the stock_id and stock_type
- properties are handled with enums in the code
- assumption: infinite stock. quantity can be added as property for testing if needed.

#### Product Table --> MySQL

| Field            | Data Type          | Details                           | Example                                  |
|------------------|--------------------|-----------------------------------|------------------------------------------|
| product_id        | ID                 | Primary key desired product combo | 44412345                                 | 
| image_id          | ID                 | Image Table foreign key           | 22212345                                 | 
| stock_id          | ID                 | Stock Table foreign key           | 33312345                                 | 
| price     | Integer              | basePrice + imageSurcharge        | 79.98                                    | ---> aggregation


#### Cart Table --> MySQL

| Field            | Data Type          | Details                              | Example               |
|------------------|--------------------|--------------------------------------|-----------------------|
| user_id           | ID                 | Foreign Key with User table          | 66612345              |
| product_id        | ID                 | Foreign Key with Product table       | 
| expiration_time   | String/Integer     | Timestamp of cart expiration (+10min)| "2023-03-10 02:10:00" |
...
 ---> where expirationTime is older than system's time, delete

- A user can only have one cart at a time. When a product manipulation occurs within the timer window, all active product timestamps are updated.
- The timer is 10 minutes.
- If the end_timestamp of the user's userId is older than current time, then the purchase was unsuccessful and the cart is deleted.
- baseline amount of images still available is checked against the transactions, then the Cart (or maybe already at the product stage) needs to check against all other products in the cart table if other users already have this image in their cart (limit is 10)


#### User Table --> MySQL

| Field            | Data Type          | Details                              | Example               |
|------------------|--------------------|--------------------------------------|-----------------------|
| user_id           | ID                 | Primary key for users                | 66612345              |
| username         | String             | Unique username                      | "john_doe"            |
| password     | String             | Hashed password                      | "(hashed data)"       |
| email        | String             | User's email address                 | "john@example.com"    |
| date_created | String/Integer     | Timestamp of account creation        | "2023-03-10 02:00:00" |

#### Transactions
Purchaser, ProductID, DateTime -- checked against the blockchain

Snapshot Table (possibly)
Image, user purchase history


#### User's Like Table --> MySQL

| Field            | Data Type          | Details                              | Example               |
|------------------|--------------------|--------------------------------------|-----------------------|
| user_id           | ID                 | User ID who liked the image          | 66612345              |
| image_id          | ID                 | Image that was liked                 | 22212345              |
| date_liked         | String/Integer     | Date and time when  image was liked  | "2023-03-10 02:00:00" | ---> allows filtering by date & updates to deleted images


### Gallery Table --> MySQL
| Field            | Data Type          | Details                              | Example               |
|------------------|--------------------|--------------------------------------|-----------------------|
| user_id           | ID                 | User ID who liked the image          | 66612345              |
| image_id          | ID                 | Image that was liked                 | 22212345              | 
| is_Published         | Boolean     | TRUE for public, FALSE for private  | TRUE |

- Image Surcharge can be calculated according to the likes in the likes table and the is_Published Boolean 
- Possible conditions: public vs private price difference (like private is generally more expensive than public); when you publish an image and it gets more likes, it is more expensive for others to buy but cheaper four yourself to buy etc. 
- Goes into the product table for price