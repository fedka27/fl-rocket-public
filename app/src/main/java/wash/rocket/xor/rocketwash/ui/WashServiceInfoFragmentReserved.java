package wash.rocket.xor.rocketwash.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.MapRouteResult;
import wash.rocket.xor.rocketwash.model.Point;
import wash.rocket.xor.rocketwash.model.Reservation;
import wash.rocket.xor.rocketwash.model.ReserveCancelResult;
import wash.rocket.xor.rocketwash.model.ReverseGeocoding;
import wash.rocket.xor.rocketwash.model.WashService;
import wash.rocket.xor.rocketwash.requests.MapDirectionRequest;
import wash.rocket.xor.rocketwash.requests.MapReverceGeocodingRequest;
import wash.rocket.xor.rocketwash.requests.ReserveCancelRequest;

@SuppressLint("LongLogTag")
public class WashServiceInfoFragmentReserved extends BaseFragment {

    public static final String TAG = "WashServiceInfoFragmentReserved";

    private static final String POINTS = "points";
    private static final String ID_SERVICE = "id_service";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";
    private static final String TITLE = "title";
    private static final String SERVICE = "service";
    private static final String RESERVED = "resrv";

    private static final int FRAGMENT_SERVCES = 1;
    private static final int FRAGMENT_PROFILE_EDIT = 2;
    private final int MAX_MARKERS = 50;

    private GoogleMap mMap;
    private NestedScrollView mScrollView1;
    private ScrollView mScrollView;

    // private LockedScrollView mContent;
    private LinearLayout mContent;
    private RelativeLayout actionCall;
    private LinearLayout mNoTime;

    private ArrayList<Point> mPoints;
    private ArrayList<Marker> mMarkers;

    private ActionButton fab;
    private Toolbar toolbar;

    private int mIdService;
    private double mLatitude;
    private double mLongitude;
    private String mTitle;

    private LayoutInflater mInflater;
    private WashService mService;
    private Typeface mFont;

    private TextView txtTime;
    private TextView txtSumm;
    private TextView txtDuration;

    private TextView txtInfoTitile;
    private TextView txtInfoDistance;
    private ProgressBar infoProgressBar;
    private ProgressBar progressBar;
    private ProgressBar mProgressBar3; // times

    private Button infoBtnPath;
    private GoogleMap.InfoWindowAdapter infoBaloon;
    private Marker mMarker;

    private Button share;
    private Button btnCancel;

    private int heightMap = 0;

    private Polyline mPolyLines;
    private Reservation mReserved;
    private Marker mPositionMarker;

    public static WashServiceInfoFragmentReserved newInstance(int id_service, double lat, double lon, String title, WashService service, Reservation reserv) {
        WashServiceInfoFragmentReserved fragment = new WashServiceInfoFragmentReserved();
        Bundle args = new Bundle();
        args.putInt(ID_SERVICE, id_service);
        args.putDouble(LATITUDE, lat);
        args.putDouble(LONGITUDE, lon);
        args.putString(TITLE, title);
        args.putParcelable(SERVICE, service);
        args.putParcelable(RESERVED, reserv);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {

        Log.w(TAG, "onAttach");

        super.onAttach(activity);
        try {
            mCallback = (IFragmentCallbacksInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IFragmentCallbacksInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.w(TAG, "onCreate");

        doStartLocationService();
        setRetainInstance(true);
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            Log.d(TAG, "savedInstanceState != null");
            mPoints = savedInstanceState.getParcelableArrayList(POINTS);
        } else {
            Log.d(TAG, "savedInstanceState == null");
            mPoints = new ArrayList<Point>();
        }

        mMarkers = new ArrayList<Marker>();
        mIdService = getArguments().getInt(ID_SERVICE);
        mLatitude = getArguments().getDouble(LATITUDE);
        mLongitude = getArguments().getDouble(LONGITUDE);
        mTitle = getArguments().getString(TITLE);
        mService = getArguments().getParcelable(SERVICE);
        mReserved = getArguments().getParcelable(RESERVED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.w(TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_wash_service_info_reserved, container, false);

        mInflater = inflater;

        mMap = ((MapFragmentWrapper) getChildFragmentManager().findFragmentById(R.id.map)).getMap();

        if (mMap != null) {
            mMap.setMyLocationEnabled(true);

            int mp = (int) getActivity().getResources().getDimension(R.dimen.map_padding);
            mMap.setPadding(mp, mp, mp, mp);

            infoBaloon = new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {

                    if (marker.equals(mPositionMarker))
                        return null;

                    String s = "";
                    String d = "";
                    if (mMarker != null) {
                        s = txtInfoTitile.getText().toString();
                        d = txtInfoDistance.getText().toString();
                    } else {

                        LatLng loc = null;
                        if (mReserved.getCarwash() != null) {
                            loc = new LatLng(mReserved.getCarwash().getLatitude(), mReserved.getCarwash().getLongitude());
                        } else
                            loc = new LatLng(mService.getLatitude(), mService.getLongitude());

                        getSpiceManager().execute(new MapReverceGeocodingRequest(loc.latitude, loc.longitude), "direction", DurationInMillis.ALWAYS_EXPIRED, new MapReverceGeocodingListener());
                    }

                    mMarker = marker;

                    // Getting view from the layout file
                    View v = getActivity().getLayoutInflater().inflate(R.layout.info_windows, null);
                    txtInfoTitile = (TextView) v.findViewById(R.id.address);
                    txtInfoDistance = (TextView) v.findViewById(R.id.distance);
                    infoProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
                    infoBtnPath = (Button) v.findViewById(R.id.btnPath);
                    infoBtnPath.setVisibility(View.GONE);

                    if (!TextUtils.isEmpty(s)) {
                        txtInfoTitile.setVisibility(View.VISIBLE);
                        txtInfoDistance.setVisibility(View.VISIBLE);
                        infoBtnPath.setVisibility(View.VISIBLE);
                        infoProgressBar.setVisibility(View.GONE);

                        txtInfoTitile.setText(s);
                        txtInfoDistance.setText(d);
                    } else {

                        txtInfoTitile.setVisibility(View.GONE);
                        txtInfoDistance.setVisibility(View.GONE);
                        infoBtnPath.setVisibility(View.GONE);
                        infoProgressBar.setVisibility(View.VISIBLE);
                    }

                    infoBtnPath.setVisibility(View.GONE);

                    infoBtnPath.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getSpiceManager().execute(new MapDirectionRequest(new LatLng(mService.getLatitude(), mService.getLongitude()), new LatLng(mService.getLatitude(), mService.getLongitude())), "direction", DurationInMillis.ONE_SECOND * 5, new MapDirectionRouteListener());
                        }
                    });


                    return v;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    //View v = getActivity().getLayoutInflater().inflate(R.layout.info_windows, null);
                    //return v;
                    return null;
                }
            };

            mMap.setInfoWindowAdapter(infoBaloon);
        }

        fab = (ActionButton) rootView.findViewById(R.id.fab);
        mScrollView1 = (NestedScrollView) rootView.findViewById(R.id.scroll);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mContent = (LinearLayout) rootView.findViewById(R.id.content_info);

        ((MapFragmentWrapper) getChildFragmentManager().findFragmentById(R.id.map)).setListener(new MapFragmentWrapper.OnTouchListener() {
            @Override
            public void onTouch() {
                mScrollView1.requestDisallowInterceptTouchEvent(true);
            }
        });

        setUpMapIfNeeded();

        mContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                int d = (int) getActivity().getResources().getDimension(R.dimen.fab_right);
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) fab.getLayoutParams();
                rl.setMargins(0, 0, d, -(fab.getMeasuredHeight() / 2));
                fab.setLayoutParams(rl);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    mContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                heightMap = fab.getTop() + toolbar.getMeasuredHeight() * 2;
            }
        });

        toolbar = setToolbar(rootView);
        toolbar.setTitle(mTitle);


        /*
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReserved.getCarwash() != null)
                    call(mReserved.getCarwash().getPhone());
                else
                    call(mService.getPhone());
            }
        });

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                LatLng loc = null;
                if (mReserved.getCarwash() != null) {
                    loc = new LatLng(mReserved.getCarwash().getLatitude(), mReserved.getCarwash().getLongitude());
                } else
                    loc = new LatLng(mService.getLatitude(), mService.getLongitude());

                if (mMap != null) {
                    mMap.addMarker(new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f), 1000, null);
                }
            }
        }, 200);

        initControls(rootView);

        return rootView;
    }

    private void initControls(View rootView) {

        txtTime = (TextView) mContent.findViewById(R.id.txtTime);
        txtDuration = (TextView) mContent.findViewById(R.id.txtDuration);
        txtSumm = (TextView) mContent.findViewById(R.id.txtSumm);
        share = (Button) mContent.findViewById(R.id.btnShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

        btnCancel = (Button) mContent.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                getSpiceManager().execute(new ReserveCancelRequest(pref.getSessionID(), mReserved.getId()), "cancel", DurationInMillis.ALWAYS_EXPIRED, new CancelRequestListener());
            }
        });

        ActionButton fab1 = (ActionButton) rootView.findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng loc = null;
                if (mReserved.getCarwash() != null) {
                    loc = new LatLng(mReserved.getCarwash().getLatitude(), mReserved.getCarwash().getLongitude());
                } else
                    loc = new LatLng(mService.getLatitude(), mService.getLongitude());

                getSpiceManager().execute(new MapDirectionRequest(new LatLng(mLatitude, mLongitude), loc), "direction", DurationInMillis.ONE_SECOND, new MapDirectionRouteListener());
            }
        });

        //  View v = rootView.findViewById(R.id.root_time_info);
        //  mProgressBar3 = (ProgressBar) v.findViewById(R.id.progressBar3);
        //  mProgressBar3.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mReserved != null) {
            txtTime.setText("Вы записаны на: " + mReserved.getTime_start_format());
            txtDuration.setText("Длительность: " + mReserved.getFull_duration());
            txtSumm.setText("Стоимость: " + mReserved.getPrice());
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        // if (mMap == null) {
        // Try to obtain the map from the SupportMapFragment.
        // mMap = mMapFragment.getMap();
        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            LatLng update = null; //getLastKnownLocation();
            if (update != null) {
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(update, 11.0f)));
            }
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    // mIsNeedLocationUpdate = false;
                    // moveToLocation(latLng, false);
                }
            });
        }
    }


    private void addMarkers(ArrayList<Point> points) {

        if (points.size() + mPoints.size() > MAX_MARKERS) {
            int i = points.size();
            while (i >= 0) {
                mPoints.remove(0);
                Marker marker = mMarkers.get(0);
                mMarkers.remove(0);
                marker.remove();
                i--;
            }
        }

        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            mPoints.add(p);
            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(p.lat, p.lon)).icon(BitmapDescriptorFactory.defaultMarker()));
            mMarkers.add(marker);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        doBindLocationService();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTracking();
        doUnbindLocationService();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(POINTS, mPoints);
        super.onSaveInstanceState(outState);
    }

    private void collapseMap() {

        if (mMap != null && mContent != null) {
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 11f), 1000, null);
        }
    }

    private void expandMap() {
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 1000, null);
        }
    }

    boolean manual = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                manual = true;
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MapFragmentWrapper f = (MapFragmentWrapper) getChildFragmentManager().findFragmentById(R.id.map);
        if (f != null)
            // getFragmentManager().beginTransaction().remove(f).commit();
            getChildFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);

        ((AppCompatActivity) getActivity()).invalidateOptionsMenu();
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if (resultCode == Activity.RESULT_OK) {

        }
    }

    public final class MapReverceGeocodingListener implements RequestListener<ReverseGeocoding> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Ошибка получения данных", Toast.LENGTH_SHORT).show();
            // progressBar.setVisibility(View.GONE);

            txtInfoTitile.setVisibility(View.VISIBLE);
            txtInfoTitile.setText("Не удалось уточнить адрес");
            txtInfoDistance.setVisibility(View.GONE);
            infoBtnPath.setVisibility(View.GONE);
            infoProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(final ReverseGeocoding result) {

            if (result != null) {

                if (mMarker != null) {
                    txtInfoTitile.setVisibility(View.VISIBLE);
                    txtInfoDistance.setVisibility(View.VISIBLE);
                    infoProgressBar.setVisibility(View.GONE);
                    infoBtnPath.setVisibility(View.VISIBLE);
                    txtInfoTitile.setText(result.getStreet() + ", " + result.getHouse());

                    float d;
                    if (mReserved.getCarwash() != null) {
                        d = mReserved.getCarwash().getDistance();
                    } else
                        d = mService.getDistance();

                    txtInfoDistance.setText(String.format(getActivity().getString(R.string.wash_distance), d));

                    if (mMarker != null && mMarker.isInfoWindowShown()) {
                        mMarker.hideInfoWindow();
                        mMarker.showInfoWindow();
                    }
                }
            }
        }
    }

    public final class MapDirectionRouteListener implements RequestListener<MapRouteResult> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Не удалось проложить маршрут", Toast.LENGTH_SHORT).show();
            // progressBar.setVisibility(View.GONE);

            txtInfoTitile.setVisibility(View.VISIBLE);
            txtInfoTitile.setText("Не удалось уточнить адрес");
            txtInfoDistance.setVisibility(View.GONE);
            infoBtnPath.setVisibility(View.GONE);
            infoProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(final MapRouteResult result) {

            if (result != null) {

                Toast.makeText(getActivity(), "Маршрут проложен", Toast.LENGTH_SHORT).show();

                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;
                MarkerOptions markerOptions = new MarkerOptions();

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                // Traversing through all the routes
                for (int i = 0; i < result.getData().size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.getData().get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                        builder.include(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(4);
                    lineOptions.color(getActivity().getResources().getColor(R.color.red_notify));
                }

                // Drawing polyline in the Google Map for the i-th route
                // mMap.addPolyline(null);

                if (mPolyLines != null) {
                    mPolyLines.remove();
                }

                if (mMap != null) {
                    mPolyLines = mMap.addPolyline(lineOptions);
                    LatLngBounds bounds = builder.build();
                    int padding = 0; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.animateCamera(cu);
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "onLocationChanged");

        if (location != null) {
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();


            if (mPositionMarker == null) {
                mPositionMarker = mMap.addMarker(new MarkerOptions()
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_position))
                                //.anchor(0.5f, 0.5f)
                        .position(new LatLng(mLatitude, mLongitude)));

            }

            animateMarker(mPositionMarker, location); // Helper method for smooth
            // animation
        }
    }

    @Override
    public void onRegisteredClient() {
        Log.d(TAG, "onRegisteredClient");
        startTracking();
    }

    public void animateMarker(final Marker marker, final Location location) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final LatLng startLatLng = marker.getPosition();
        final double startRotation = marker.getRotation();
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);

                double lng = t * location.getLongitude() + (1 - t)
                        * startLatLng.longitude;
                double lat = t * location.getLatitude() + (1 - t)
                        * startLatLng.latitude;

                float rotation = (float) (t * location.getBearing() + (1 - t)
                        * startRotation);

                marker.setPosition(new LatLng(lat, lng));
                marker.setRotation(rotation);

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    private class CancelRequestListener implements RequestListener<wash.rocket.xor.rocketwash.model.ReserveCancelResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Не удалось отменить запись", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(ReserveCancelResult reserveCancelResult) {

            if (reserveCancelResult.isData()) {
                Toast.makeText(getActivity(), "Запись отменена", Toast.LENGTH_SHORT).show();
                manual = true;
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent());
                getFragmentManager().popBackStack();
            } else
                Toast.makeText(getActivity(), "Не удалось отменить запись", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (!manual)
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);

    }
}
