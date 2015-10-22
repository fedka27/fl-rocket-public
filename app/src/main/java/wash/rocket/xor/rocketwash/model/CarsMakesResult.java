package wash.rocket.xor.rocketwash.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.google.api.client.util.Key;

import java.util.List;

@JsonObject
public class CarsMakesResult {

    @Key
    @JsonField
    private String status;

    @Key
    @JsonField
    private List<CarsMakes> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CarsMakes> getData() {
        return data;
    }

    public void setData(List<CarsMakes> data) {
        this.data = data;
    }
}
