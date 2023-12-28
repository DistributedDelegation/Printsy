package gateway.dto;

import gateway.model.Transaction;

public class TransactionInput {

    private Long userId;
    private Long imageId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Transaction toTransaction(){
        Transaction t = new Transaction();
        t.setUserId(userId);
        t.setImageId(imageId);

        return t;
    }
}
