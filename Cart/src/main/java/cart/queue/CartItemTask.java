package cart.queue;

import cart.model.Product;

import java.time.Instant;

public class CartItemTask {
    private String imageId;
    private Long stockId;
    private Integer price;
    private Long userId;
    private Instant expirationTime;
    

    public CartItemTask() {
    }

    public CartItemTask(String imageId, Long stockId, Integer price, Long userId) {
        this.imageId = imageId;
        this.stockId = stockId;
        this.price = price;
        this.userId = userId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Instant expirationTime) {
        this.expirationTime = expirationTime;
    }
}
