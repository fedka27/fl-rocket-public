package wash.rocket.xor.rocketwash.util;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import net.danlew.android.joda.JodaTimeAndroid;

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


    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();

        JodaTimeAndroid.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(this);
        super.attachBaseContext(base);
    }
}
