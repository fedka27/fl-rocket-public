package wash.rocket.xor.rocketwash.model;

import com.google.api.client.util.Key;

public class CarMake {
    @Key
    private int id;
    @Key
    private String name;

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
}
