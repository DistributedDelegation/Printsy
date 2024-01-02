package gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionResult {
    private boolean success;
    private int result;

    // Constructor, getters and setters
    public TransactionResult(boolean success, int result) {
        this.success = success;
        this.result = result;
    }

    @JsonProperty("success")
    public boolean isSuccess() {
        return success;
    }

    @JsonProperty("success")
    public void setSuccess(boolean success) {
        this.success = success;
    }

    @JsonProperty("result")
    public int getResult() {
        return result;
    }

    @JsonProperty("result")
    public void setResult(int result) {
        this.result = result;
    }
}
