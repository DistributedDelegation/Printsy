const Product = ({ imageUrl, product }) => {
    const { name, productImageUrl, price, sizes, overlayPosition } = product;

    // Handling => stockId
    const mapToStockId = (productName, size) => {
        // Define the mapping based on the stockId conversion
        const mapping = {
            "Mug": {"onesize": "001"},
            "T-Shirt": {"S": "002", "M": "003", "L": "004"},
            "Polo-Shirt": {"S": "005", "M": "006", "L": "007"}
        };
        return mapping[productName][size];
    };

    // Handling => imageId
    const FETCH_IMAGE_QUERY = `query($imageUrl: String!) {getImageByUrl(imageUrl: $imageUrl) {
      imageId imageUrl imageName imageDescription isImagePublishedYN imageTimestamp}}`;

      const fetchImageByURL = async (url) => {
        const query = JSON.stringify({
          query: FETCH_IMAGE_QUERY,
          variables: { imageUrl: url }
        });
        try {
          const response = await fetch("http://localhost:8089/graphql", {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json'
            },
            body: query
          });
          const data = await response.json();
          if (data.errors) {
            console.error('GraphQL Errors:', data.errors);
            return null;
          }
          return data.data.getImageByUrl;
        } catch (error) {
          console.error('Network Error:', error);
          return null;
        }
      };

      const handleAddToCart = async (event, price) => {
        event.preventDefault();
        const selectedSize = event.target[0].value; // Corrected to directly access the value
        const stockId = mapToStockId(name, selectedSize);
        console.log("Selected stockId:", stockId);
        const userId = 1; // Replace with actual logic to get userId
        const image = await fetchImageByURL(imageUrl);
        if (!image) {
            console.error("Failed to fetch image details.");
            return;
        }
        const imageId = image.imageId;
        console.log("Sending to cart: imageId: ", imageId, ", stockId: ", stockId, ", price: ", price, ", userId: ", userId);
        const query_message = JSON.stringify({
            query: `mutation addItemtoCart($imageId: ID!, $stockId: ID!, $price: Int!, $userId: ID!) { addItemtoCart(imageId: $imageId, stockId: $stockId, price: $price, userId: $userId) }`,
            variables: {
                imageId: imageId,
                stockId: stockId.toString(),
                price: price,
                userId: userId.toString()
            }
        });
        console.log("Query message:", query_message);
        try {
            const response = await fetch('http://localhost:8086/graphql', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
            body: query_message
            });
            const data = await response.json();
            if (data.errors) {
                console.error("GraphQL Errors:", data.errors);
            } else {
                console.log("Add to cart response:", data);
            }
        } catch (error) {
            console.error('Error adding item to cart:', error);
        }
    };
    

    return (
        <div className="product-container">
            <div className="product-image">
                <img src={productImageUrl} style={{ 
                        width: '380px',
                        height: '380px'
                    }} alt={name} />
                <img 
                    src={imageUrl} 
                    alt="Overlay" 
                    className="overlay-image"
                    style={{ 
                        top: overlayPosition.top, 
                        left: overlayPosition.left,
                        width: overlayPosition.widthHeight,
                        height: overlayPosition.widthHeight 
                    }} 
                />
            </div>
            <span className="product-name">{name}</span>
            <span className="product-price">€{price}</span>
            <form onSubmit={(e) => handleAddToCart(e, product.price)}>
                <select className="size-dropdown">
                    {product.sizes.map((size, index) => (
                        <option key={index} value={size}>{size}</option>
                    ))}
                </select>
                <button type="submit" className="secondary-button">Add to Cart</button>
            </form>
        </div>
    );
};

export default Product;