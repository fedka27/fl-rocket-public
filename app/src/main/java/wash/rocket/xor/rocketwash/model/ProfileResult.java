package wash.rocket.xor.rocketwash.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonObject
public class ProfileResult {

    @JsonField
    @JsonProperty("status")
    private String status;

    @JsonField
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
