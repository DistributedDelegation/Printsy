import { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../AuthContext';

// Directly include the products array and mapToStockId function
const products = [
  {
    name: "Mug",
    productImageUrl: "/images/mug.png",
    price: 10,
    sizes: ["onesize"],
    overlayPosition: { top: '172px', left: '113px', widthHeight: '110px' }
  },
  {
    name: "T-Shirt",
    productImageUrl: "/images/shirt.png",
    price: 20,
    sizes: ["S", "M", "L"],
    overlayPosition: { top: '105px', left: '124px', widthHeight: '130px' }
  },
  {
    name: "Polo-Shirt",
    productImageUrl: "/images/polo.png",
    price: 30,
    sizes: ["S", "M", "L"],
    overlayPosition: { top: '100px', left: '207px', widthHeight: '45px' }
  },
];

const mapToStockId = (productName, size) => {
  const mapping = {
    "Mug": {"onesize": "1"},
    "T-Shirt": {"S": "2", "M": "3", "L": "4"},
    "Polo-Shirt": {"S": "5", "M": "6", "L": "7"}
  };
  return mapping[productName] && mapping[productName][size];
};

const mapFromStockId = (stockId) => {
  // Inverse mapping from stockId to {productName, size}
  const inverseMapping = {
    "1": { productName: "Mug", size: "onesize" },
    "2": { productName: "T-Shirt", size: "S" },
    "3": { productName: "T-Shirt", size: "M" },
    "4": { productName: "T-Shirt", size: "L" },
    "5": { productName: "Polo-Shirt", size: "S" },
    "6": { productName: "Polo-Shirt", size: "M" },
    "7": { productName: "Polo-Shirt", size: "L" }
  };
  return inverseMapping[stockId] || { productName: "Unknown", size: "Unknown" }; // default or error case
};

const CheckoutProducts = () => {
  const [cartItems, setCartItems] = useState([]);
  const [totalPrice, setTotalPrice] = useState(0);
  const authContext = useContext(AuthContext);
  const userId = authContext.userID;

  useEffect(() => {
    fetchCartItems();
  }, []);

  const fetchCartItems = async () => {
    const query = `query ($userId: ID!) { findCartItemsByUserId(userId: $userId) { productResult { imageId imageUrl stockId price } userId expirationTime } }`;
    //const userId = 1; // Replace with actual user ID
    const variables = { userId };

    try {
      const response = await fetch('http://localhost:8080/cart/graphql', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ query, variables }),
      });
      const data = await response.json();
      const cartItems = data.data.findCartItemsByUserId;
      console.log("Cart items:", cartItems);
      const items = cartItems.map(item => {
        const { productName, size } = mapFromStockId(item.productResult.stockId);
        const productDetail = products.find(p => 
          p.name === productName && p.sizes.includes(size)
        );
        return {
          ...item,
          productDetail: productDetail || { ...item.productDetail, productName: 'Unknown', size: 'Unknown' }, // Handling case where productDetail might not be found
        };
      });

      const totalPrice = items.reduce((acc, item) => acc + item.productResult.price, 0);
      setTotalPrice(totalPrice);
      setCartItems(items);
      
    } catch (error) {
      console.error('Error fetching cart products:', error);
    }
  };

  if (cartItems.length === 0) {
    return <div className="empty-cart-message">Cart is empty!</div>;
  }

  return (
    <div>
      {cartItems.map((product, index) => {
        const { productDetail } = product;
        const { productName, productOption, productImageUrl } = productDetail || {};
      
        return (
          <div key={index} className="product-in-cart">
            <img src={productImageUrl || '/images/placeholder.png'} alt="Product" className="product-image" />
            <span className="product-name">{productName || 'Unknown Product'}</span>
            <span className="product-option">{productOption || 'Unknown Option'}</span>
            <span className="product-price">€{product.productResult.price}</span>
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
