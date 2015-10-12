package wash.rocket.xor.rocketwash.model;

import com.google.api.client.util.Key;

import java.util.List;

public class WashServiceResult {
    @Key
    private String status;
    @Key
    private List<WashService> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<WashService> getData() {
        return data;
    }

    public void setData(List<WashService> data) {
        this.data = data;
    }
}
