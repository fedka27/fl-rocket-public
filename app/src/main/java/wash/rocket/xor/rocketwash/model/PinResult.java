package wash.rocket.xor.rocketwash.model;


import com.google.api.client.util.Key;



public class PinResult {
    @Key
    private String status;
    @Key
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
