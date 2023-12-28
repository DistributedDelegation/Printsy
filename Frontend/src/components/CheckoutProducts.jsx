import { useState, useEffect } from 'react';

const CheckoutProducts = () => {
  const [cartItems, setCartItems] = useState([]);

  useEffect(() => {
    // Fetch cart items when the component mounts
    fetchCartItems();
  }, []);

  const fetchCartItems = async () => {
    // GraphQL query to find cart items by user ID
    const query = `
      query findCartItemsByUserId($userId: ID!) {
        findCartItemsByUserId(userId: $userId) {
          productId
          userId
          expirationTime
        }
      }
    `;

    const userId = /* Retrieve the user ID from context or local storage */;
    const variables = { userId };

    try {
      const response = await fetch('http://localhost:8085/graphql', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ query, variables }),
      });
      const data = await response.json();
      setCartItems(data.data.findCartItemsByUserId);
    } catch (error) {
      console.error('Error fetching cart items:', error);
    }
  };

  return (
    <div>
      {/* Display cart items here */}
      {cartItems.map((item, index) => (
        <div key={index}>
          <p>Product ID: {item.productId}</p>
          <p>User ID: {item.userId}</p>
          <p>Expiration Time: {item.expirationTime}</p>
        </div>
      ))}
    </div>
  );
};

export default CheckoutProducts;