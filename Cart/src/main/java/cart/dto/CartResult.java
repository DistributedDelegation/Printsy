package cart.dto;

import cart.model.Cart;

import java.time.Instant;

public class CartResult {

    private Long userId;

    private ProductResult productResult;

    private Instant expirationTime;

    public CartResult() {
    }

    public CartResult(Long userId, ProductResult productResult, Instant expirationTime) {
        this.userId = userId;
        this.productResult = productResult;
        this.expirationTime = expirationTime;
    }

    public CartResult(Cart cart){
        this.userId = cart.getUserId();
        this.expirationTime = cart.getExpirationTime();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ProductResult getProductResult() {
        return productResult;
    }

    public void setProductResult(ProductResult productResult) {
        this.productResult = productResult;
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Instant expirationTime) {
        this.expirationTime = expirationTime;
    }
}
