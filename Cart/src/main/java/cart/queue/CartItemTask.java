package cart.queue;

public class CartItemTask {
    private Long userId;
    private Long productId;
    private String imageId;
    

    public CartItemTask() {
    }

    public CartItemTask(Long userId, Long productId, String imageId) {
        this.userId = userId;
        this.productId = productId;
        this.imageId = imageId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

}
