package wash.rocket.xor.rocketwash.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CarsProfileResult {

    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
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
