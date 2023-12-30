package cart.dto;

import java.time.Instant;

public class CartResult {

    private Long userId;

    private Long product_id;

    private Instant expirationTime;

    public CartResult() {
    }

    public CartResult(Long userId, Long product_id, Instant expirationTime) {
        this.userId = userId;
        this.product_id = product_id;
        this.expirationTime = expirationTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Instant expirationTime) {
        this.expirationTime = expirationTime;
    }
}
