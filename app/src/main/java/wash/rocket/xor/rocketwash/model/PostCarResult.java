package wash.rocket.xor.rocketwash.model;

import com.google.api.client.util.Key;

/**
 * Created by aratj on 12.09.2015.
 */
public class PostCarResult {

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

    @Key

    private String status;

    @Key
    private CarsAttributes data;

}
