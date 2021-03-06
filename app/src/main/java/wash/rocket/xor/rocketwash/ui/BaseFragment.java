package wash.rocket.xor.rocketwash.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.octo.android.robospice.SpiceManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.services.JacksonGoogleHttpClientSpiceServiceEx;
import wash.rocket.xor.rocketwash.services.LocationService;
import wash.rocket.xor.rocketwash.util.App;
import wash.rocket.xor.rocketwash.util.Country;
import wash.rocket.xor.rocketwash.util.CountryMaster;
import wash.rocket.xor.rocketwash.util.Preferences;
import wash.rocket.xor.rocketwash.widgets.ButtonWithState;
import wash.rocket.xor.rocketwash.widgets.SoftKeyboard;

/**
 * A placeholder fragment containing a simple view.
 */
public class BaseFragment extends Fragment {
    protected static final String TAG = BaseFragment.class.getSimpleName();
    private static final int DIALOG_SHARE = 100;
    private static final String DIALOG_SHARE_TAG = "DIALOG_SHARE";
    private static final int PERMISSION_REQUEST_CALL_PHONE = 1;
    //private SpiceManager spiceManager = new SpiceManager(RobospiceService.class);
    private SpiceManager spiceManager = new SpiceManager(JacksonGoogleHttpClientSpiceServiceEx.class);
    protected SoftKeyboard softKeyboard;
    protected Preferences pref;
    protected IFragmentCallbacksInterface mCallback;


    private Messenger mLocationService = null;
    private boolean isBoundLocationService;
    private ConnectivityManager connectivityManager;
    private Intent mServiceIntent;
    private LayoutInflater mInflater;

    private App app;

    private boolean mEventKeyboard = false;

    public BaseFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pref = new Preferences(getActivity());
        overrideFonts(getActivity(), getView());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onStart() {
        super.onStart();
        getSpiceManager().start(getActivity());
    }

    @Override
    public void onStop() {

        if (getSpiceManager().isStarted()) {
            getSpiceManager().shouldStop();
        }

        super.onStop();
    }


    public void initkeyboardEvents() {
        InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
        softKeyboard = new SoftKeyboard((ViewGroup) getView(), im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {

            @Override
            public void onSoftKeyboardHide() {
                Log.d(TAG, "onSoftKeyboardHide");

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onKeyBoardHide();
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                Log.d(TAG, "onSoftKeyboardShow");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onKeyBoardShow();
                    }
                });
            }
        });
    }

    public void removeKeyboardEvent() {
        if (softKeyboard != null)
            softKeyboard.unRegisterSoftKeyboardCallback();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void onKeyBoardHide() {

    }

    protected void onKeyBoardShow() {

    }

    protected SpiceManager getSpiceManager() {
        return spiceManager;
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            Log.d("Network", "NETWORKNAME: " + anInfo.getTypeName());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void share() {
        showDialogShare();
    }

    static class IncomingHandler extends Handler {
        private final WeakReference<BaseFragment> mService;

        IncomingHandler(BaseFragment service) {
            mService = new WeakReference<BaseFragment>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseFragment service = mService.get();
            if (service != null) {
                service.handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case LocationService.MSG_REGISTER_CLIENT:
                onRegisteredClient();
                break;

            case LocationService.MSG_CONNECTED:
                if (msg.getData() != null)
                    onConnected(msg.getData().getBundle(LocationService.EXTRAS));
                else
                    onConnected(null);
                break;
            case LocationService.MSG_CONNECTION_FAILED:

                if (msg.getData() != null)
                    onConnectionFailed(msg.getData().getInt(LocationService.EXTRAS));
                else
                    onConnectionFailed(0);
                break;

            case LocationService.MSG_DISCONNECTED:
                onDisconnected();
                break;

            case LocationService.MSG_LOCATION_CHANGE:
                //Log.d(TAG, "MSG_LOCATION_CHANGE");
                Bundle data = msg.getData();
                if (data != null) {
                    Location last_location = data.getParcelable(LocationService.LOCATION);
                    onLocationChanged(last_location);
                }

                break;
            case LocationService.MSG_START_TRACKING:
                onStartTracking();
                break;

            case LocationService.MSG_STOP_TRACKING:
                onStopTracking();
                break;
            case LocationService.MSG_GPS_STATUS_CHANGE:
                onGpsStatusChange(msg.arg1);
                break;

            case LocationService.MSG_GPS_EVENT_FIX:
                onGpsEventFix(msg.arg1);
                break;

            default:
                break;
        }
    }

    private final Messenger mLocationMessenger = new Messenger(new IncomingHandler(this));
    private ServiceConnection mConnectionLocationService = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            mLocationService = new Messenger(service);
            try {
                Message msg = Message.obtain(null, LocationService.MSG_REGISTER_CLIENT);
                msg.replyTo = mLocationMessenger;
                mLocationService.send(msg);
            } catch (RemoteException e) {
                // nothing to do
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, "onServiceDisconnected");
            mLocationService = null;
        }
    };

    protected void doStartLocationService() {
        Log.d(TAG, "doStartLocationService");
        mServiceIntent = new Intent(getActivity().getApplicationContext(), LocationService.class);
        getActivity().startService(mServiceIntent);
    }

    protected void doBindLocationService() {
        Log.d(TAG, "doBindLocationService");
        mServiceIntent = new Intent(getActivity().getApplicationContext(), LocationService.class);
        getActivity().getApplicationContext().bindService(mServiceIntent, mConnectionLocationService, Context.BIND_AUTO_CREATE);
        isBoundLocationService = true;
    }

    protected void doStopLocationService() {
        Log.d(TAG, "doStopLocationService");
        if (mServiceIntent == null)
            mServiceIntent = new Intent(getActivity().getApplicationContext(), LocationService.class);

        getActivity().getApplicationContext().stopService(mServiceIntent);
    }

    protected void doUnbindLocationService() {
        if (isBoundLocationService) {
            if (mLocationService != null) {
                try {
                    Message msg = Message.obtain(null, LocationService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mLocationMessenger;
                    mLocationService.send(msg);
                } catch (RemoteException e) {
                    //nothing to do
                }
            }

            getActivity().getApplicationContext().unbindService(mConnectionLocationService);
            isBoundLocationService = false;
        }
    }

    protected void doCloseLocationService() {
        doStopLocationService();
        doUnbindLocationService();
    }

    public void onRegisteredClient() {
        Log.d(TAG, "LocationService onRegisteredClient");
        //startTracking();
        onConnected(null);
    }

    public void onLocationChanged(Location location) {

    }

    public void onConnectionFailed(int result) {
    }

    public void onConnected(Bundle connectionHint) {
    }

    public void onDisconnected() {
    }

    public void onStartTracking() {
    }

    public void onStopTracking() {
    }

    public void onGpsStatusChange(int event) {
    }

    public void onGpsEventFix(int event) {
    }

    public void startTracking() {
        if (mLocationService != null) {
            try {
                Message msg = Message.obtain(null, LocationService.MSG_START_TRACKING);
                msg.replyTo = mLocationMessenger;
                mLocationService.send(msg);
            } catch (RemoteException e) {
                //nothing to do
            }
        }
    }

    public void stopTracking() {
        if (mLocationService != null) {
            try {
                Message msg = Message.obtain(null, LocationService.MSG_STOP_TRACKING);
                msg.replyTo = mLocationMessenger;
                mLocationService.send(msg);
            } catch (RemoteException e) {
                //nothing to do
            }
        }
    }

    public Location getLastLocation() {
        Log.d(TAG, "getLastLocation");

        Location location = null;
        final LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        for (final String provider : lm.getProviders(true)) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location loc = null;
                loc = lm.getLastKnownLocation(provider);
                Log.d("getLastLocation", "provider = " + provider);
                Log.d("getLastLocation", "Location = " + (loc == null ? "null" : loc.toString()));
                if (loc != null) {
                    location = loc;
                }
            }
        }
        return location;
    }

    protected Toolbar setToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity a = (AppCompatActivity) getActivity();
        if (a != null) {
            a.setSupportActionBar(toolbar);
            if (a.getSupportActionBar() != null)
                a.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        return toolbar;
    }

    protected Toolbar setToolbar(View view, String title) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity a = (AppCompatActivity) getActivity();
        if (a != null) {
            a.setSupportActionBar(toolbar);
            if (a.getSupportActionBar() != null)
                a.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (toolbar != null) {
            toolbar.setTitle(title);
        }
        return toolbar;
    }

    private String mPhone;
    private int mService_id;
    private String mName;

    protected void call(String phone, int service_id, String name) {

        mPhone = phone;
        mService_id = service_id;
        mName = name;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL_PHONE);
            } else if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                internalCall(phone, service_id, name);
            }
        } else {
            internalCall(phone, service_id, name);
        }
    }

    private void internalCall(String phone, int service_id, String name) {
        phone = phone == null ? "" : phone.replace("(", "").replace(")", "").replace(" ", "").replace("-", "");
        Log.d(TAG, "cal " + phone);
        //Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        startActivity(intent);

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(getActivity());
        Tracker tracker = analytics.newTracker("UA-54521987-4");
        // tracker.setScreenName("");
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("button")
                .setAction("call")
                .setLabel(name)
                .setValue(service_id)
                .build());
    }


    public void showToast(String text, int resback, int length) {
        mInflater = LayoutInflater.from(getActivity());
        Toast t = Toast.makeText(getActivity(), text, length);
        View v = mInflater.inflate(resback, null);
        TextView tv = (TextView) v.findViewById(R.id.text);
        tv.setText(text);
        t.setView(v);
        t.show();
    }

    public void showToastOk(String text) {
        showToast(text, R.layout.toast_ok, Toast.LENGTH_SHORT);
    }

    public void showToastOk(int res) {
        showToastOk(getActivity().getString(res));
    }

    public void showToastError(String text) {
        showToast(text, R.layout.toast_error, Toast.LENGTH_LONG);
    }

    public void showToastError(int res) {
        showToastError(getActivity().getString(res));
    }

    public void showToastWarn(String text) {
        showToast(text, R.layout.toast_warning, Toast.LENGTH_LONG);
    }

    public void showToastWarn(int res) {
        showToastWarn(getActivity().getString(res));
    }

    public String getDefaultPhonePrefix() {
        CountryMaster cm = CountryMaster.getInstance(getActivity());
        ArrayList<Country> countries = cm.getCountries();
        String countryIsoCode = cm.getDefaultCountryIso();
        Country country = cm.getCountryByIso(countryIsoCode);
        TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String countryiso = manager.getSimCountryIso().toUpperCase();
        String c = pref.getLastUsedPhoneCode();
        if (!TextUtils.isEmpty(c)) {
            return c;
        } else {
            if (country != null) {
                return "+" + country.mDialPrefix;
            } else {

                if (!TextUtils.isEmpty(countryiso)) {
                    for (int i = 0; i < countries.size(); i++) {
                        if (countries.get(i).mCountryIso.equals(countryiso)) {
                            return "+" + (country == null ? "" : country.mDialPrefix);
                        }
                    }
                } else {
                    return "+" + countries.get(0).mDialPrefix;
                }
            }
        }

        return "+" + countries.get(0).mDialPrefix;
    }

    private void showDialogShare() {
        AlertDialogFragment f = AlertDialogFragment.newInstance(R.string.share_friends,
                getActivity().getString(R.string.share_data),
                getActivity().getString(R.string.share_friends),
                getActivity().getString(R.string.cancel), DIALOG_SHARE, this);
        f.show(getFragmentManager(), DIALOG_SHARE_TAG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case DIALOG_SHARE:

                    String url = getActivity().getString(R.string.share_data);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, url);
                    startActivity(Intent.createChooser(intent, getString(R.string.share_with)));
                    break;
            }
        }
    }

    public App getApp() {
        if (getActivity() == null)
            return null;

        return (App) getActivity().getApplicationContext();
    }

    public void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else {
                if (v instanceof TextView)
                    ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "roboto_light.ttf"));
                if (v instanceof Button)
                    ((Button) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "roboto_light.ttf"));
                if (v instanceof ButtonWithState)
                    ((ButtonWithState) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "roboto_light.ttf"));
                if (v instanceof CheckBox)
                    ((CheckBox) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "roboto_light.ttf"));
                if (v instanceof RadioButton)
                    ((RadioButton) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "roboto_light.ttf"));
            }

        } catch (Exception e) {
            // do not show;
        }
    }


    public void onBackPress() {

    }

    public void restoreTargets() {

    }

    public void report(int service_id, String name, String screen) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(getActivity());
        Tracker tracker = analytics.newTracker("UA-54521987-4");
        tracker.setScreenName(screen);
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("button")
                .setAction("report")
                .setLabel(name)
                .setValue(service_id)
                .build());

        showToastOk("Благодарим за отзыв. В ближайшее время на данной мойке появиться онлайн запись.");
    }


    public void reservationAction(int service_id, String name) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(getActivity());
        Tracker tracker = analytics.newTracker("UA-54521987-4");
        tracker.setScreenName("");
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("button")
                .setAction("reservation")
                .setLabel(name)
                .setValue(service_id)
                .build());
    }

    public void setEventKeyboard(boolean value) {
        mEventKeyboard = value;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CALL_PHONE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        internalCall(mPhone, mService_id, mName);
                    }
                } else {
                    Toast.makeText(getActivity(), "Доступ к телефонии запрещен", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
