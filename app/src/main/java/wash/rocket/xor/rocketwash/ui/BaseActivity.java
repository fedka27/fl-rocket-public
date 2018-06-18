package wash.rocket.xor.rocketwash.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.octo.android.robospice.SpiceManager;

import wash.rocket.xor.rocketwash.services.JacksonGoogleHttpClientSpiceServiceEx;
import wash.rocket.xor.rocketwash.util.Preferences;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CALL_PHONE = 312;

    protected Preferences preferences = new Preferences(this);

    protected SpiceManager spiceManager = new SpiceManager(JacksonGoogleHttpClientSpiceServiceEx.class);

    protected void call(String phone, int service_id, String name) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL_PHONE);
            } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                internalCall(phone, service_id, name);
            }
        } else {
            internalCall(phone, service_id, name);
        }
    }

    @SuppressLint("MissingPermission")
    private void internalCall(String phone, int service_id, String name) {
        phone = phone == null ? "" : phone.replace("(", "").replace(")", "").replace(" ", "").replace("-", "");

        Log.d(TAG, "cal " + phone);

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        startActivity(intent);

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        Tracker tracker = analytics.newTracker("UA-54521987-4");
        // tracker.setScreenName("");
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("button")
                .setAction("call")
                .setLabel(name)
                .setValue(service_id)
                .build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CALL_PHONE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        //TODO try again

//                        internalCall(mPhone, mService_id, mName);
                    }
                } else {
                    Toast.makeText(this, "Доступ к телефонии запрещен", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
