package wash.rocket.xor.rocketwash.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

@JsonObject
public class CarsMakes {

    @JsonField
    private int id;

    @JsonField
    private String name;

    @JsonField
    private List<CarMake> car_models;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CarMake> getCar_models() {
        return car_models;
    }

    public void setCar_models(List<CarMake> car_models) {
        this.car_models = car_models;
    }
}
