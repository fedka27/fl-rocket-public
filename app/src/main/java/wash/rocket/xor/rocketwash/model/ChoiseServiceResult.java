package wash.rocket.xor.rocketwash.model;

import com.google.api.client.util.Key;

import java.util.List;

public class ChoiseServiceResult {
    @Key
    private String status;
    @Key
    private List<ChoiseService> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ChoiseService> getData() {
        return data;
    }

    public void setData(List<ChoiseService> data) {
        this.data = data;
    }
}
