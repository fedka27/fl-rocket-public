package wash.rocket.xor.rocketwash.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

@JsonObject
public class CarsMakesResult {

    @JsonField
    private String status;

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
