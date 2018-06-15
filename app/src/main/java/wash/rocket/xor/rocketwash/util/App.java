package wash.rocket.xor.rocketwash.util;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import roboguice.util.temp.Ln;
import ru.tinkoff.acquiring.sdk.Journal;
import wash.rocket.xor.rocketwash.BuildConfig;
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

        if (profile == null) {
            Preferences pref = new Preferences(getBaseContext());
            profile = pref.getProfile();
        }

        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Ln.getConfig().setLoggingLevel(BuildConfig.DEBUG ? Log.DEBUG : Log.WARN);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);


        Journal.setDebug(BuildConfig.DEBUG);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
