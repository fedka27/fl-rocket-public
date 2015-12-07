package wash.rocket.xor.rocketwash.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import io.fabric.sdk.android.Fabric;
import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.services.LocationService;
import wash.rocket.xor.rocketwash.util.Preferences;

public class MainActivity extends AppCompatActivity implements IFragmentCallbacksInterface {

    private static final String TAG = "MainActivity";
    private static final String FRAGMENT_LOGIN_TAG = "login";
    private static final String FRAGMENT_MAIN = "main";
    private static final String FRAGMENT_NETWORK_TAG = "network";
    private static final String FRAGMENT_GPS_TAG = "gps";
    private static final String FRAGMENT_LOADER_TAG = "login";
    private static final int FRAGMENT_GPS = 1;
    private static final int FRAGMENT_NETWORK = 2;
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 0;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private Preferences pref;
    private Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);

        if (findViewById(R.id.container_two) == null) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        pref = new Preferences(this);

        if (savedInstanceState == null) {
            init();
        }

        //  restoreTargets();
    }

    private void init() {
        if (!TextUtils.isEmpty(pref.getSessionID()) && pref.getRegistered()) {
            if (!isOnline()) {
                showNetworkFragment();
            } else if (!enableGPS()) {
                if (pref.getShowDialogGps()) {
                    showGPSFragment();
                } else
                    showLoaderFragment();
            } else
                showLoaderFragment();

        } else {
            if (!isOnline()) {
                showNetworkFragment();
            } else
                showLoginFragment();
            //getSupportFragmentManager().beginTransaction().replace(R.id.container, new LoginFragment(), FRAGMENT_LOGIN_TAG).commit();
        }
    }

    @Override
    public void onLogged() {
        pref.setRegistered(true);
        removePrevFragments();
        showLoaderFragment();
    }

    @Override
    public void onLoading() {

        removePrevFragments();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.container, new NearestWashServicesFragment(), NearestWashServicesFragment.TAG)
                .commitAllowingStateLoss();
    }

    @Override
    public void onErrorLoading() {

    }

    @Override
    public void onGPSWarningDone() {

        if (!TextUtils.isEmpty(pref.getSessionID())) {
            showLoaderFragment();
        } else
            showLoginFragment();
    }

    @Override
    public void onNetworkWarningDone() {

        if (!TextUtils.isEmpty(pref.getSessionID())) {
            if (!enableGPS() && pref.getShowDialogGps())
                showGPSFragment();
            else
                showLoaderFragment();
        } else
            showLoginFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreTargets();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        doStartLocationService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        doStopLocationService();
    }

    public boolean isOnline() {
        //Log.e("AAA", "isOnline");

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //Log.e("AAA", "connectivityManager = " + connectivityManager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Log.e("AAA", "1");
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                Log.d("Network", "NETWORKNAME: " + networkInfo.getTypeName());
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }

            }
        } else {
            if (connectivityManager != null) {
                //Log.e("AAA", "2");
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        Log.d("Network", "NETWORKNAME: " + anInfo.getTypeName());
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }


    public boolean enableGPS() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showGPSFragment() {

        GpsWarningFragment f = new GpsWarningFragment();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .add(R.id.container, f, GpsWarningFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private void showNetworkFragment() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .add(R.id.container, new NetworkErrorFragment(), NetworkErrorFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private void showLoaderFragment() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.container, new LoaderFragment(), LoaderFragment.TAG)
                .commitAllowingStateLoss();
    }


    private void showLoginFragment() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.container, new LoginFragment(), LoginFragment.TAG)
                .commitAllowingStateLoss();
    }

    protected void doStartLocationService() {
        Log.d(TAG, "doStartLocationService");
        mServiceIntent = new Intent(getApplicationContext(), LocationService.class);
        startService(mServiceIntent);
    }

    protected void doStopLocationService() {
        Log.d(TAG, "doStopLocationService");
        if (mServiceIntent == null)
            mServiceIntent = new Intent(getApplicationContext(), LocationService.class);

        getApplicationContext().stopService(mServiceIntent);
    }

    private void removePrevFragments() {
        Fragment f = getSupportFragmentManager().findFragmentByTag(LoginFragment.TAG);
        if (f != null)
            getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();

        f = getSupportFragmentManager().findFragmentByTag(NearestWashServicesFragment.TAG);
        if (f != null)
            getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();

        f = getSupportFragmentManager().findFragmentByTag(SendSmsFragment.TAG);
        if (f != null)
            getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();

        f = getSupportFragmentManager().findFragmentByTag(ConfirmationFragment.TAG);
        if (f != null)
            getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();

        f = getSupportFragmentManager().findFragmentByTag(WashServiceInfoFragment.TAG);
        if (f != null)
            getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();

        f = getSupportFragmentManager().findFragmentByTag(WashServiceInfoFragmentCall.TAG);
        if (f != null)
            getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();

        f = getSupportFragmentManager().findFragmentByTag(WashServiceInfoFragmentQuick.TAG);
        if (f != null)
            getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
    }


    private void restoreTargets() {
        Log.d(TAG, "restoreTargets");
        List<Fragment> flist = getSupportFragmentManager().getFragments();
        if (flist != null) {
            for (int i = 0; i < flist.size(); i++) {

                if (flist.get(i) instanceof BaseFragment) {
                    BaseFragment f = (BaseFragment) flist.get(i);
                    if (f != null) {
                        Log.d(TAG, f.getClass().getName());
                        f.restoreTargets();
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        BaseFragment f = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        if (f != null)
            f.onBackPress();
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        removePrevFragments();
        init();
    }

    private void requestFineLocation() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_FINE_LOCATION);
        }
    }

    private void requestCoarseLocation() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_COARSE_LOCATION);
        }
    }


}
