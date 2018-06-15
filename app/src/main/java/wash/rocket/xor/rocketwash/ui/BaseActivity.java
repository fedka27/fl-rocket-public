package wash.rocket.xor.rocketwash.ui;

import android.support.v7.app.AppCompatActivity;

import com.octo.android.robospice.SpiceManager;

import wash.rocket.xor.rocketwash.services.JacksonGoogleHttpClientSpiceServiceEx;
import wash.rocket.xor.rocketwash.util.Preferences;

public class BaseActivity extends AppCompatActivity {
    protected Preferences preferences = new Preferences(this);

    protected SpiceManager spiceManager = new SpiceManager(JacksonGoogleHttpClientSpiceServiceEx.class);

}
