package wash.rocket.xor.rocketwash.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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

        //  restoreTargets();
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

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
                .commit();
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

        //public static boolean isNetworkAvailable(Context context) {
        boolean isMobile = false, isWifi = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] infoAvailableNetworks = cm.getAllNetworkInfo();

        if (infoAvailableNetworks != null) {
            for (NetworkInfo network : infoAvailableNetworks) {

                if (network.getType() == ConnectivityManager.TYPE_WIFI) {
                    if (network.isConnected() && network.isAvailable())
                        isWifi = true;
                }
                if (network.getType() == ConnectivityManager.TYPE_MOBILE) {
                    if (network.isConnected() && network.isAvailable())
                        isMobile = true;
                }
            }
        }
        return isMobile || isWifi;
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
                .commit();
    }

    private void showNetworkFragment() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .add(R.id.container, new NetworkErrorFragment(), NetworkErrorFragment.TAG)
                .addToBackStack(null)
                .commit();
    }

    private void showLoaderFragment() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.container, new LoaderFragment(), LoaderFragment.TAG)
                .commit();
    }


    private void showLoginFragment() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.container, new LoginFragment(), LoginFragment.TAG)
                .commit();
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
            getSupportFragmentManager().beginTransaction().remove(f).commit();

        f = getSupportFragmentManager().findFragmentByTag(NearestWashServicesFragment.TAG);
        if (f != null)
            getSupportFragmentManager().beginTransaction().remove(f).commit();

        f = getSupportFragmentManager().findFragmentByTag(SendSmsFragment.TAG);
        if (f != null)
            getSupportFragmentManager().beginTransaction().remove(f).commit();

        f = getSupportFragmentManager().findFragmentByTag(ConfirmationFragment.TAG);
        if (f != null)
            getSupportFragmentManager().beginTransaction().remove(f).commit();

        f = getSupportFragmentManager().findFragmentByTag(WashServiceInfoFragment.TAG);
        if (f != null)
            getSupportFragmentManager().beginTransaction().remove(f).commit();

        f = getSupportFragmentManager().findFragmentByTag(WashServiceInfoFragmentCall.TAG);
        if (f != null)
            getSupportFragmentManager().beginTransaction().remove(f).commit();

        f = getSupportFragmentManager().findFragmentByTag(WashServiceInfoFragmentQuick.TAG);
        if (f != null)
            getSupportFragmentManager().beginTransaction().remove(f).commit();
    }


    private void restoreTargets() {
        Log.d(TAG, "restoreTargets");
        List<Fragment> flist = getSupportFragmentManager().getFragments();
        if (flist != null) {
            for (int i = 0; i < flist.size(); i++) {
                BaseFragment f = (BaseFragment) flist.get(i);
                if (f != null) {
                    Log.d(TAG, f.getClass().getName());
                    f.restoreTargets();
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

}
