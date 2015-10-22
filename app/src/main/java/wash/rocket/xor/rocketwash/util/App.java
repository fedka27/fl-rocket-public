package wash.rocket.xor.rocketwash.util;

import android.app.Application;

import java.util.ArrayList;

import wash.rocket.xor.rocketwash.model.CarsAttributes;
import wash.rocket.xor.rocketwash.model.Profile;

public class App extends Application {

    private ArrayList<CarsAttributes> carsAttributes;
    private Profile profile;

    public ArrayList<CarsAttributes> getCarsAttributes() {
        return carsAttributes;
    }

    public void setCarsAttributes(ArrayList<CarsAttributes> carsAttributes) {
        this.carsAttributes = carsAttributes;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
