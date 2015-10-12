package wash.rocket.xor.rocketwash.model;

import com.google.api.client.util.Key;

public class LoginResult {
    @Key
    private String status;
    @Key
    private LoginData data;

    public LoginData getData() {
        return data;
    }

    public void setData(LoginData data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toString() {
        String s;
        s = "status = " + status;
        s = s + "\ndata = " + (data == null ? "null" : data.toString());
        return s;
    }
}
