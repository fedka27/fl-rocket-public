package wash.rocket.xor.rocketwash.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProfileResult {

    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private Profile data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Profile getData() {
        return data;
    }

    public void setData(Profile data) {
        this.data = data;
    }
}
