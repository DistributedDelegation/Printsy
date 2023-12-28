package cart.model;

public class TransactionInput {
    private Long userId;
    private Long productId;
    private String imageId;

    // Constructor
    public TransactionInput(Long userId, Long productId, String imageId) {
        this.userId = userId;
        this.productId = productId;
        this.imageId = imageId;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
