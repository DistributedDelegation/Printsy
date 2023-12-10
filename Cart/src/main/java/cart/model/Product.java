package cart.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Products")
public class Product {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long productId;

    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "stock_id")
    private Long stockId;

    @Column(name = "price")
    private Integer price;

    @Transient  // creates non-persistent field for computations
    private Integer quantity;

//---------------Getters and Setters-----------------

    public Product() {
    }

    public Product(Long imageId, Long stockId, Integer price) {
        this.imageId = imageId;
        this.stockId = stockId;
        this.price = price;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
