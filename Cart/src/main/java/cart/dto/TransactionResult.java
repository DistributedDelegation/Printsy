package cart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionResult {
    private Boolean success;

    @JsonProperty("success")
    public Boolean getSuccess() {
        return success;
    }
}