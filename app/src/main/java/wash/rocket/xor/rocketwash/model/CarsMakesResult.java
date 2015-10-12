package wash.rocket.xor.rocketwash.model;

import com.google.api.client.util.Key;

import java.util.List;

public class CarsMakesResult {

    @Key
    private String status;

    @Key
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
