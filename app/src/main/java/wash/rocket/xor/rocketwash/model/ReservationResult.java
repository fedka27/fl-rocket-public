package wash.rocket.xor.rocketwash.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReservationResult {

    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
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
