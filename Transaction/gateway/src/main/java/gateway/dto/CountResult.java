package gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CountResult {

    private int count;

    public CountResult(int count) {
        this.count = count;
    }

    @JsonProperty("count")
    public int getCount() {
        return count;
    }

    @JsonProperty("count")
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "CountResult{" +
                "count=" + count +
                '}';
    }
}
