package wash.rocket.xor.rocketwash.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonObject
public class ReservedResult {

    @JsonProperty("status")
    @JsonField
    private String status;

    @JsonProperty("data")
    @JsonField
    private List<Reservation> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Reservation> getData() {
        return data;
    }

    public void setData(List<Reservation> data) {
        this.data = data;
    }
}
