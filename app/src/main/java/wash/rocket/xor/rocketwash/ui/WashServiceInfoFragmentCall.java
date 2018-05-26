package wash.rocket.xor.rocketwash.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.widget.NestedScrollView;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import wash.rocket.xor.rocketwash.model.CarsMakes;
import wash.rocket.xor.rocketwash.model.ChoiceService;
import wash.rocket.xor.rocketwash.model.MapRouteResult;
import wash.rocket.xor.rocketwash.model.Point;
import wash.rocket.xor.rocketwash.model.ReverseGeocoding;
import wash.rocket.xor.rocketwash.model.WashService;
import wash.rocket.xor.rocketwash.requests.MapDirectionRequest;
import wash.rocket.xor.rocketwash.requests.MapReverceGeocodingRequest;
import wash.rocket.xor.rocketwash.widgets.CalendarScrollWidget;

@SuppressLint("LongLogTag")
public class WashServiceInfoFragmentCall extends BaseFragment {

    public static final String TAG = "WashServiceInfoFragmentCall";

    private static final String POINTS = "points";
    private static final String ID_SERVICE = "id_service";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";
    private static final String TITLE = "title";
    private static final String SERVICE = "service";

    private static final int FRAGNEBT_SERVCES = 1;


    private final int MAX_MARKERS = 50;

    private GoogleMap mMap;
    private NestedScrollView mScrollView1;
    private ScrollView mScrollView;
    private FrameLayout time_content;

    // private LockedScrollView mContent;
    private LinearLayout mContent;

    private ArrayList<Point> mPoints;
    private ArrayList<Marker> mMarkers;

    private ActionButton fab;
    private Toolbar toolbar;

    private int mIdService;
    private double mLatitude;
    private double mLongitude;
    private String mTitle;


    private LayoutInflater mInflater;
    private List<CarsMakes> list_cars;
    private ArrayList<ChoiceService> list;
    private CalendarScrollWidget mCalendar;
    private WashService mService;
    private Typeface mFont;

    private TextView txtInfoTitile;
    private TextView txtInfoDistance;
    private TextView txtStub;
    private ProgressBar infoProgressBar;
    private Button infoBtnPath;
    private Button btnReport;
    private GoogleMap.InfoWindowAdapter infoBaloon;
    private Marker mMarker;
    private ActionButton fab1;
    private Polyline mPolyLines;
    private Marker mPositionMarker;

    public static WashServiceInfoFragmentCall newInstance(int id_service, double lat, double lon, String title, WashService service) {
        WashServiceInfoFragmentCall fragment = new WashServiceInfoFragmentCall();
        Bundle args = new Bundle();
        args.putInt(ID_SERVICE, id_service);
        args.putDouble(LATITUDE, lat);
        args.putDouble(LONGITUDE, lon);
        args.putString(TITLE, title);
        args.putParcelable(SERVICE, service);
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
        Log.w(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        //doStartLocationService();

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
        // mLatitude = getArguments().getDouble(LATITUDE);
        // mLongitude = getArguments().getDouble(LONGITUDE);
        mTitle = getArguments().getString(TITLE);
        mService = getArguments().getParcelable(SERVICE);

        //if (mService != null)
        //    mService.var_dump();

        Location l = getLastLocation();
        if (l != null) {
            mLatitude = l.getLatitude();
            mLongitude = l.getLongitude();
        }

        Log.d(TAG, "mLatitude = " + mLatitude);
        Log.d(TAG, "mLongitude = " + mLongitude);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.w(TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_wash_service_info_call, container, false);

        mInflater = inflater;

        MapFragmentWrapper mapFragmentWrapper = ((MapFragmentWrapper) getChildFragmentManager().findFragmentById(R.id.map));

        mapFragmentWrapper.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                initMap(googleMap);
            }
        });

        mapFragmentWrapper.setListener(new MapFragmentWrapper.OnTouchListener() {
            @Override
            public void onTouch() {
                mScrollView1.requestDisallowInterceptTouchEvent(true);
            }
        });

        fab = (ActionButton) rootView.findViewById(R.id.fab);
        mScrollView1 = (NestedScrollView) rootView.findViewById(R.id.scroll);
        mContent = (LinearLayout) rootView.findViewById(R.id.content_car);

        setToolbar(rootView, mTitle);

        initControls(rootView);


        return rootView;
    }

    private void initMap(GoogleMap googleMap) {
        mMap = googleMap;


        if (mMap != null) {
//            mMap.setMyLocationEnabled(true); // todo location ?
            mMap.setPadding(20, 20, 20, 20);

            infoBaloon = new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker marker) {

                    if (marker.equals(mPositionMarker))
                        return null;

                    //if (mMarker != null)
                    //    return mMarker.

                    String s = "";
                    String d = "";
                    if (mMarker != null) {
                        s = txtInfoTitile.getText().toString();
                        d = txtInfoDistance.getText().toString();
                    } else
                        getSpiceManager().execute(new MapReverceGeocodingRequest(mService.getLatitude(), mService.getLongitude()), "info", DurationInMillis.ALWAYS_EXPIRED, new MapReverceGeocodingListener());

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
                            getSpiceManager().execute(new MapDirectionRequest(new LatLng(mLatitude, mLongitude), new LatLng(mService.getLatitude(), mService.getLongitude())), "wash", 30, new MapDirectionRouteListener());
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

        //addMarkers(mPoints);

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
            }
        });

        /*
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(mTitle);
        }*/


        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                LatLng loc = new LatLng(mService.getLatitude(), mService.getLongitude());

                if (mMap != null) {
                    mMap.addMarker(new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f), 1000, null);
                }
            }
        }, 200);
    }

    private void initControls(View rootView) {

        mFont = Typeface.createFromAsset(getActivity().getAssets(), "roboto.ttf");

        fab1 = (ActionButton) rootView.findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSpiceManager().execute(new MapDirectionRequest(new LatLng(mLatitude, mLongitude), new LatLng(mService.getLatitude(), mService.getLongitude())), "wash", 30, new MapDirectionRouteListener());
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(mService.getPhone(), mService.getId(), mService.getName());
            }
        });

        txtStub = (TextView) rootView.findViewById(R.id.txtStub);
        txtStub.setText(mService.getMobile_stub_text());

        ViewGroup content_share = (ViewGroup) rootView.findViewById(R.id.content_share);
        Button share = (Button) content_share.findViewById(R.id.btnShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

        btnReport = (Button) rootView.findViewById(R.id.btnReport);

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report(mIdService, mTitle, TAG);
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(getActivity());
        Tracker tracker = analytics.newTracker("UA-54521987-4");
        tracker.setScreenName(TAG);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        //if (mMap == null) {
        // Try to obtain the map from the SupportMapFragment.
        //  mMap = mMapFragment.getMap();
        // Check if we were successful in obtaining the map.
        if (mMap != null) {
//            mMap.setMyLocationEnabled(false); //TODO Location ?
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
            //}
            // }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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

    public final class MapReverceGeocodingListener implements RequestListener<ReverseGeocoding> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            showToastError(R.string.error_loading_data);
            // progressBar.setVisibility(View.GONE);

            txtInfoTitile.setVisibility(View.VISIBLE);
            txtInfoTitile.setText(R.string.error_find_address);
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
                    txtInfoTitile.setText(String.format("%s, %s", result.getStreet(), result.getHouse()));
                    txtInfoDistance.setText(String.format(getActivity().getString(R.string.wash_distance), mService.getDistance()));

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

            showToastError(R.string.error_direction);
            // progressBar.setVisibility(View.GONE);

            txtInfoTitile.setVisibility(View.VISIBLE);
            txtInfoTitile.setText(R.string.error_find_address);
            txtInfoDistance.setVisibility(View.GONE);
            infoBtnPath.setVisibility(View.GONE);
            infoProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(final MapRouteResult result) {

            if (result != null) {
                showToastOk(R.string.success_direction);

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

                if (lineOptions == null) {
                    Toast.makeText(getActivity(), "Неудалось проложить маршрут", Toast.LENGTH_SHORT).show();
                } else {
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


            if (mPositionMarker == null && mMap != null) {
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

        if (mMap == null || marker == null)
            return;

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


}
