package wash.rocket.xor.rocketwash.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

@JsonObject
public class ChoiceServiceResult {

    @JsonField
    private String status;

    @JsonField
    private List<ChoiceService> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ChoiceService> getData() {
        return data;
    }

    public void setData(List<ChoiceService> data) {
        this.data = data;
    }
}
