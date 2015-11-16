package wash.rocket.xor.rocketwash.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

@JsonObject
public class CarsProfileResult {

    @JsonField
    private String status;

    @JsonField
    private List<CarsAttributes> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CarsAttributes> getData() {
        return data;
    }

    public void setData(List<CarsAttributes> data) {
        this.data = data;
    }
}
