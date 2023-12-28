package gateway.dto;

public class TransactionResult {
    private boolean success;
    private int result;

    // Constructor, getters and setters
    public TransactionResult(boolean success, int result) {
        this.success = success;
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
