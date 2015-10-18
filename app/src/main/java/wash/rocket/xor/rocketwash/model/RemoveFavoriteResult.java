package wash.rocket.xor.rocketwash.model;

import com.fasterxml.jackson.annotation.JsonProperty;


//{"status":"success","data":true};

public class RemoveFavoriteResult {

    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private boolean data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }
}
