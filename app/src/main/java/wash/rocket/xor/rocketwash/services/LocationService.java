package wash.rocket.xor.rocketwash.services;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class LocationService extends Service implements LocationListener, GpsStatus.Listener, ConnectionCallbacks, OnConnectionFailedListener {
    private static final String TAG = "LocationService";
    private static final int NOTIFICATION_ID = 5545675;

    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_START_TRACKING = 3;
    public static final int MSG_STOP_TRACKING = 4;
    public static final int MSG_LOCATION_CHANGE = 5;
    public static final int MSG_GPS_STATUS_CHANGE = 9;
    public static final int MSG_CONNECTION_FAILED = 6;
    public static final int MSG_CONNECTED = 7;
    public static final int MSG_DISCONNECTED = 8;
    public static final int MSG_GPS_EVENT_FIX = 10;

    public static final String LOCATION = "ru.location.service";
    public static final String EXTRAS = "ru.location.extras";

    private ArrayList<Messenger> mClients = new ArrayList<>();
    private boolean mRunning = false;
    private LocationRequest locationRequest;

    //private LocationClient locationClient;
    //private GoogleApiClient mGoogleApiClient;
    // Если isGooglePlayServicesAvailable - FAIL
    // то будем работать по старинке

    private LocationManager gpsLocationManager;
    private boolean isGooglePlayServicesAvailable = false;
    private final static int DELAY_CHANGE_LOCATION = 3000;
    private final static int GPS_MIN_TIME = 1000;
    private final static float GPS_MIN_DISTANCE = 0.0f; // every time
    private boolean mGpsStatusReceiverRegistered = false;
    private Location mLastLocation;
    private boolean mProviderEnable = false;

    private boolean mGPSFixed = false;

    protected GoogleApiClient mGoogleApiClient;

    static class IncomingHandler extends Handler {
        private final WeakReference<LocationService> mService;

        IncomingHandler(LocationService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            LocationService service = mService.get();
            if (service != null) {
                service.handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_REGISTER_CLIENT:
                mClients.add(msg.replyTo);

                try {
                    msg.replyTo.send(Message.obtain(null, MSG_REGISTER_CLIENT, 0, 0));
                } catch (RemoteException e) {
                    // nothing to do
                }

                break;
            case MSG_UNREGISTER_CLIENT:
                mClients.remove(msg.replyTo);
                break;
            case MSG_START_TRACKING:
                startTracking();
                break;
            case MSG_STOP_TRACKING:
                stopTracking();
                break;

            default:
                break;
        }
    }

    final private Messenger mMessenger = new Messenger(new IncomingHandler(this));

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        //	startTracking();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        gpsLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gpsLocationManager.addGpsStatusListener(this);

        doRegisterGpsStatusReceiver();
        buildGoogleApiClient();
    }

    public void startTracking() {
        if (mRunning)
            return;

        Log.d(TAG, "startTracking");

        try {
            startForeground(NOTIFICATION_ID, new Notification());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        initGPS();
    }

    public void stopTracking() {
        if (!mRunning)
            return;
        Log.d(TAG, "stopTracking");

		/*
        if (locationClient != null && locationClient.isConnected())
		{
			locationClient.removeLocationUpdates(this);
			locationClient.disconnect();
		}*/

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

        if (gpsLocationManager != null) {
            gpsLocationManager.removeUpdates(CompatLocationListener);
            gpsLocationManager.removeGpsStatusListener(this);
            gpsLocationManager.removeNmeaListener(mNmeaListener);
        }

        mRunning = false;
        //isGooglePlayServicesAvailable = false;

        stopForeground(true);
    }

    private void initGPS() {
        Log.i(TAG, "initGPS");

        //if (mGoogleApiClient != null)
        //    mGoogleApiClient.connect();

        if (gpsLocationManager != null) {
            gpsLocationManager.removeUpdates(CompatLocationListener);
            gpsLocationManager.removeNmeaListener(mNmeaListener);
        }

        if (gpsLocationManager != null && gpsLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.i(TAG, "GPS_PROVIDER enable");

            //gpsLocationManager.removeUpdates(CompatLocationListener);
            gpsLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_MIN_TIME, GPS_MIN_DISTANCE, CompatLocationListener);

            Log.i(TAG, "set GpsStatus.NmeaListener");
            gpsLocationManager.removeNmeaListener(mNmeaListener);
            //gpsLocationManager.addNmeaListener(mNmeaListener);
            mProviderEnable = true;
        } else {
            mProviderEnable = false;
        }

        // сменли состояние GPS , если true , то можно не идти дальше, если нет то, проблуем еще раз.
        if (isGooglePlayServicesAvailable)
            return;

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isGooglePlayServicesAvailable == true");
            mGoogleApiClient.connect();
            isGooglePlayServicesAvailable = true;
        } else {
            Log.e(TAG, "unable to connect to google play services.");
            isGooglePlayServicesAvailable = false;
            //onLocationChanged(getLastLocation());
            sendMessageToClients(MSG_CONNECTED, 0, 0, null);
        }
        mRunning = true;
    }

    private GpsStatus.NmeaListener mNmeaListener = new GpsStatus.NmeaListener() {
        // читаем статус из NMEA
        public void onNmeaReceived(long timestamp, String nmea) {
            //System.out.println("nmea = " + nmea);
            if (!TextUtils.isEmpty(nmea) && nmea.contains("$GPGGA")) {
                String[] s = nmea.split(",");
                if (s != null && s.length >= 6) {
                    try {
                        Integer i = Integer.valueOf(TextUtils.isEmpty(s[6]) ? "0" : s[6]);
                        //Log.i(TAG, "GPGGA = " + i);
                        // 0  - GPS d`not fixed
                        sendMessageToClients(MSG_GPS_EVENT_FIX, i, 0, null);
                        mGPSFixed = i != 0;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //onGpsStatusChanged(0);
                    sendMessageToClients(MSG_GPS_EVENT_FIX, 0, 0, null);
                }
            } else {
                //onGpsStatusChanged(0);
                sendMessageToClients(MSG_GPS_EVENT_FIX, 0, 0, null);
            }
        }
    };

    Location testLocation;

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged =  " + location + "; provider = " + (location == null ? "null" : location.getProvider()));

        if (location == null)
            return;

        /*
        float dist = 0;
        if (testLocation != null) {
            Log.d(TAG, " distance  = " + location.distanceTo(testLocation));
            dist = location.distanceTo(testLocation);
        }

        testLocation = location;
        // dist < 1 (meter) may be stay ?

        if (dist > 1 && mLastLocation != null && location.getAccuracy() > location.distanceTo(mLastLocation)) {
            Log.d(TAG, "onLocationChanged  invalid locaion, skip (" + location.distanceTo(mLastLocation) + ") ");
            //return;
        }*/

        mLastLocation = location;


        Log.d(TAG, "fuck");

        Bundle data = new Bundle();
        data.putParcelable(LOCATION, location);
        sendMessageToClients(MSG_LOCATION_CHANGE, 0, 0, data);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "onConnectionFailed");
        Bundle data = new Bundle();
        data.putInt(EXTRAS, result.getErrorCode());
        //sendMessageToClients(MSG_CONNECTION_FAILED, 0, 0, data);
        isGooglePlayServicesAvailable = false;
        sendMessageToClients(MSG_CONNECTED, 0, 0, null);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected");

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(DELAY_CHANGE_LOCATION);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(0);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

        Bundle data = new Bundle();
        data.putBundle(EXTRAS, connectionHint);
        sendMessageToClients(MSG_CONNECTED, 0, 0, data);
        onLocationChanged(getLastLocation());
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        if (gpsLocationManager != null) {
            gpsLocationManager.removeUpdates(CompatLocationListener);
            gpsLocationManager.removeGpsStatusListener(this);
            gpsLocationManager.removeNmeaListener(mNmeaListener);
            gpsLocationManager = null;
        }

        doUnregisterGpsStatusReceiver();
        mLastLocation = null;
        mHandlerChange.removeCallbacksAndMessages(null);
    }


    public Location getLastLocation() {
        Log.d(TAG, "getLastLocation");

        Location location = null;
        final LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        for (final String provider : lm.getProviders(true)) {
            Location loc = lm.getLastKnownLocation(provider);
            System.out.println(" getLastLocation : " + provider);
            if (location != null && loc != null) {
                if (loc.getAccuracy() > location.getAccuracy())
                    loc = location;
            }
            location = loc;
        }
        return location;

    }

    @Override
    public void onGpsStatusChanged(int event) {
        Log.e(TAG, "onGpsStatusChanged event = " + event);
        //sendMessageToClients(MSG_GPS_STATUS_CHANGE, event, 0, null);
    }

    /**
     * Для совместимости. там где гугл плей сервисы не поддерживаются.
     */
    private android.location.LocationListener CompatLocationListener = new android.location.LocationListener() {
        private long last_time = 0;

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderEnabled = " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG, "onProviderDisabled = " + provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "CompatLocationListener > onLocationChanged; provider = ");
            // Есои гугло сервисы доступны, они будут сообщаять о смене локации
            if (isGooglePlayServicesAvailable)
                return;

            Log.d(TAG, "fuck1");

            if (location == null)
                return;

            if (!location.hasAccuracy())
                return;

            long currentTimeStamp = System.currentTimeMillis();

            // если меньше х секунд, то игнорим.
            if ((currentTimeStamp - last_time) < DELAY_CHANGE_LOCATION) {
                Log.d(TAG, "GpsLoggingService.OnLocationChanged skip - time");
                // return;
            }

			/*
            if (mLastLocation != null && location.getAccuracy() > location.distanceTo(mLastLocation))
			{
				return;
			}*/

            //mLastLocation = location;
            last_time = currentTimeStamp;

            // отправляем.
            LocationService.this.onLocationChanged(location);
        }
    };

    private void doRegisterGpsStatusReceiver() {
        if (!mGpsStatusReceiverRegistered) {
            IntentFilter filter = new IntentFilter();
            //filter.addAction("location.internal");
            filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
            filter.addAction(LocationManager.MODE_CHANGED_ACTION);
            registerReceiver(mGpsStatusReceiver, filter);
            mGpsStatusReceiverRegistered = true;
        }
    }

    private void doUnregisterGpsStatusReceiver() {
        if (mGpsStatusReceiverRegistered) {
            unregisterReceiver(mGpsStatusReceiver);
            mGpsStatusReceiverRegistered = false;
        }
    }

    private static Handler mHandlerChange = new Handler();
    private static final int CHANGE_DELAY = 300;
    private Runnable mRunnableChange;

    private BroadcastReceiver mGpsStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "PROVIDERS_CHANGED_ACTION = " + intent.getAction());
            // если изначально не был включеен GPS провайдер, то инициализируем все чот с этим связанно.

            //if (gpsLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !mProviderEnable)
            //{
            mHandlerChange.removeCallbacks(mRunnableChange);
            mRunnableChange = new Runnable() {
                @Override
                public void run() {
                    final boolean gps_enabled = gpsLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                    Log.e(TAG, "PROVIDERS_CHANGED_ACTION;  gps_enabled = " + gps_enabled);

                    if (gps_enabled)
                        initGPS();

                    //onGpsStatusChanged(gps_enabled ? 1 : 0);
                    sendMessageToClients(MSG_GPS_STATUS_CHANGE, gps_enabled ? 1 : 0, 0, null);
                }
            };
            mHandlerChange.postDelayed(mRunnableChange, CHANGE_DELAY);
            //}
        }
    };

    private void sendMessageToClients(int message, int a, int b, Bundle data) {
        if (mClients == null) {
            Log.e(TAG, "sendMessageToClients mClients == null");
            return;
        } else {
            Log.i(TAG, "sendMessageToClients mClients == " + mClients.size());
        }

        for (int i = mClients.size() - 1; i >= 0; i--) {
            try {
                Message msg = Message.obtain(null, message, a, b);
                msg.setData(data);
                mClients.get(i).send(msg);
            } catch (RemoteException e) {
                mClients.remove(i);
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

}
