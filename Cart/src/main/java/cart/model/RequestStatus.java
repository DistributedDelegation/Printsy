package cart.model;

public class RequestStatus {

    private boolean status;
    private Integer quantity;
    private String message;

    // Constructor
    public RequestStatus(boolean status, Integer quantity, String message) {
        this.status = status;
        this.quantity = quantity;
        this.message = message;
    }

    // Getters and setters
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "RequestStatus{" +
                "status=" + status +
                ", quantity=" + quantity +
                ", message='" + message + '\'' +
                '}';
    }
}
