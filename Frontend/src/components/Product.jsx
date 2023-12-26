const Product = ({ imageUrl, product }) => {
    const { name, productImageUrl, price, sizes, overlayPosition } = product;

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
            <select className="size-dropdown">
                {sizes.map((size, index) => (
                    <option key={index} value={size}>{size}</option>
                ))}
            </select>
            <button className="secondary-button">Add to Cart</button>
        </div>
    );
};

export default Product;