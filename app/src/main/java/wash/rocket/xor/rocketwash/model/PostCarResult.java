package wash.rocket.xor.rocketwash.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;


@JsonObject
public class PostCarResult {
    @JsonField
    private String status;

    @JsonField
    private CarsAttributes data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CarsAttributes getData() {
        return data;
    }

    public void setData(CarsAttributes data) {
        this.data = data;
    }
}
