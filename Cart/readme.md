### Preliminary Cart Specs

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