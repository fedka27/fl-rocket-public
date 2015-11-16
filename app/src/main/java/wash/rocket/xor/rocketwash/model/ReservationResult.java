package wash.rocket.xor.rocketwash.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class ReservationResult {

    @JsonField
    private String status;

    @JsonField
    private Reservation data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Reservation getData() {
        return data;
    }

    public void setData(Reservation data) {
        this.data = data;
    }
}
