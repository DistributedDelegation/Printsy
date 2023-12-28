import { useState, useEffect } from 'react';

const CheckoutProducts = () => {
  const [cartItems, setCartItems] = useState([]);
  const [totalPrice, setTotalPrice] = useState(0);

  // Mapping for stockIds to product names and options
  const stockIdMapping = {
    "1": { productName: "Mug", productOption: "Onesize" },
    "2": { productName: "T-Shirt", productOption: "S" },
    "3": { productName: "T-Shirt", productOption: "M" },
    "4": { productName: "T-Shirt", productOption: "L" },
    "5": { productName: "Polo-Shirt", productOption: "S" },
    "6": { productName: "Polo-Shirt", productOption: "M" },
    "7": { productName: "Polo-Shirt", productOption: "L" }
  };

  useEffect(() => {
    fetchCartItems();
  }, []);

  const fetchCartItems = async () => {
    const query = `query checkCartProductsByUserId($userId: ID!) { checkCartProductsByUserId(userId: $userId) { productId imageId stockId price } }`;
    const userId = 1; // Replace with actual user ID
    const variables = { userId };

    try {
      const response = await fetch('http://localhost:8086/graphql', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ query, variables }),
      });
      const data = await response.json();
      const items = data.data.checkCartProductsByUserId;

      const totalPrice = items.reduce((acc, item) => acc + item.price, 0);
      setTotalPrice(totalPrice);
      setCartItems(items);
    } catch (error) {
      console.error('Error fetching cart products:', error);
    }
  };

  if (cartItems.length === 0) {
    return <div className="empty-cart-message">Cart is empty!</div>;
  }

  console.log("Cart items:", cartItems);

  return (
    <div>
      {cartItems.map((product, index) => {
        const { productName, productOption } = stockIdMapping[product.stockId] || {};
  
        return (
          <div key={index} className="product-in-cart">
            <img src="/images/polo.png" alt="Product" className="placeholder-square" />
            <span className="product-name">{productName || 'Unknown Product'}</span>
            <span className="product-option">{productOption || 'Unknown Option'}</span>
            <span className="product-price">€{product.price}</span>
          </div>
        );
      })}
      <div className="divider"></div>
      <div id="total-cart-container">
        <span id="total-cart">Total</span>
        <span className="total-price">€{totalPrice}</span>
      </div>
    </div>
  );
};
  
  export default CheckoutProducts;