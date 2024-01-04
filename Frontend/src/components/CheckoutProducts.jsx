import { useState, useEffect, useContext } from "react";
import { AuthContext } from "../AuthContext";

// Directly include the products array and mapToStockId function
const products = [
  {
    name: "Mug",
    productImageUrl: "/images/mug.png",
    price: 10,
    sizes: ["OneSize"],
    overlayPosition: { top: "36px", left: "23px", widthHeight: "24px" },
  },

  {
    name: "T-Shirt",
    productImageUrl: "/images/shirt.png",
    price: 20,
    sizes: ["S", "M", "L"],
    overlayPosition: { top: "22px", left: "25px", widthHeight: "28px" },
  },
  {
    name: "Polo-Shirt",
    productImageUrl: "/images/polo.png",
    price: 30,
    sizes: ["S", "M", "L"],
    overlayPosition: { top: "21px", left: "42px", widthHeight: "10px" },
  },
];

const mapFromStockId = (stockId) => {
  // Inverse mapping from stockId to {productName, size}
  const inverseMapping = {
    1: { productName: "Mug", size: "OneSize" },
    2: { productName: "T-Shirt", size: "S" },
    3: { productName: "T-Shirt", size: "M" },
    4: { productName: "T-Shirt", size: "L" },
    5: { productName: "Polo-Shirt", size: "S" },
    6: { productName: "Polo-Shirt", size: "M" },
    7: { productName: "Polo-Shirt", size: "L" },
  };
  return inverseMapping[stockId] || { productName: "Unknown", size: "Unknown" }; // default or error case
};

const CheckoutProducts = ({ refreshKey, timerValue, onCartEmpty }) => {
  const [cartItems, setCartItems] = useState([]);
  const [totalPrice, setTotalPrice] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const authContext = useContext(AuthContext);
  const userId = authContext.userID;

  useEffect(() => {
    setIsLoading(true); // Start loading
    const timeout = setTimeout(() => {
      fetchCartItems();
    }, 3000);

    return () => {
      clearTimeout(timeout); // Cleanup the timeout
    };
  }, [refreshKey]);

  const fetchCartItems = async () => {
    const query = `query ($userId: ID!) { findCartItemsByUserId(userId: $userId) { productResult { imageId imageUrl stockId price } userId expirationTime } }`;
    const variables = { userId };

    try {
      const response = await fetch("http://localhost:8080/cart/graphql", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ query, variables }),
      });
      const data = await response.json();
      const cartItems = data.data.findCartItemsByUserId;
      console.log("Cart items:", cartItems);
      const items = cartItems.map((item) => {
        const { productName, size } = mapFromStockId(
          item.productResult.stockId
        );
        const productTypeDetail = products.find(
          (p) => p.name === productName && p.sizes.includes(size)
        );
        return {
          ...item,
          productName: productName,
          productSize: size,
          productTypeDetail: productTypeDetail || {
            ...item.productTypeDetail,
            productName: "Unknown",
            size: "Unknown",
          }, // Handling case where productTypeDetail might not be found
        };
      });

      const productCountMap = {};
      const uniqueItems = [];

      items.forEach((item) => {
        const id = `${item.productResult.stockId}-${item.productSize}-${item.productResult.imageId}`;
        if (productCountMap[id]) {
          productCountMap[id].count += 1;
        } else {
          productCountMap[id] = { ...item, count: 1 };
          uniqueItems.push(productCountMap[id]);
        }
      });

      uniqueItems.sort((a, b) => {
        const imageIdA = a.productResult.imageId;
        const imageIdB = b.productResult.imageId;

        // Assuming imageId is a string for alphanumeric sorting
        return imageIdA.localeCompare(imageIdB);
      });

      const totalPrice = uniqueItems.reduce(
        (acc, item) => acc + item.productResult.price * item.count,
        0
      );

      setTotalPrice(totalPrice);
      setCartItems(uniqueItems);
      setIsLoading(false);
    } catch (error) {
      console.error("Error fetching cart products:", error);
      setIsLoading(false);
    }
  };

  useEffect(() => {
    // Notify parent component about the cart status
    onCartEmpty(cartItems.length === 0);
  }, [cartItems, onCartEmpty]);

  if (isLoading) {
    return (
      <div className="loading" style={{ marginTop: "10px" }}>
        Loading...{" "}
      </div>
    );
  }

  if (cartItems.length === 0) {
    return (
      <div className="empty-cart-message" style={{ marginTop: "10px" }}>
        Cart is empty!
      </div>
    );
  }

  return (
    <div>
      <div className="cart-items">
        {cartItems.map((product, index) => {
          const { productResult, productSize, count } = product;
          const { imageUrl, price } = productResult;
          const { name, productImageUrl, overlayPosition, sizes } =
            product.productTypeDetail || {};

          return (
            <div key={index} className="product-in-cart">
              <div className="product-image">
                <img
                  src={productImageUrl || "/images/placeholder.png"}
                  style={{ width: "80px", height: "80px" }}
                  alt="Product"
                  className="product-image"
                />
                <img
                  src={imageUrl}
                  alt="Overlay"
                  className="overlay-image"
                  style={{
                    top: overlayPosition?.top,
                    left: overlayPosition?.left,
                    width: overlayPosition?.widthHeight,
                    height: overlayPosition?.widthHeight,
                  }}
                />
              </div>
              <div className="product-details">
                <div className="product-details-left">
                  <span className="product-name">
                    {name || "Unknown Product"}
                  </span>
                  <span className="product-size">
                    {productSize || "Unknown Size"}
                  </span>
                  <span className="product-count">Quantity: {count}</span>
                </div>
                <div className="product-details-right">
                  <span className="product-price">€{price}</span>
                </div>
              </div>
            </div>
          );
        })}
      </div>
      <div className="divider"></div>
      <div id="total-cart-container">
        <span id="total-cart">Total</span>
        <span className="total-price">€{totalPrice}</span>
      </div>
    </div>
  );
};

export default CheckoutProducts;
