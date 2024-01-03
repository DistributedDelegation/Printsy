package gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionResult {
    private boolean success;

    // Constructor, getters and setters
    public TransactionResult(boolean success) {
        this.success = success;
    }


    @JsonProperty("success")
    public boolean isSuccess() {
        return success;
    }

    @JsonProperty("success")
    public void setSuccess(boolean success) {
        this.success = success;
    }

}
