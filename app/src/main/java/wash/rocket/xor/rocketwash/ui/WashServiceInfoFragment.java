package wash.rocket.xor.rocketwash.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.adapters.TimeRecyclerViewAdapter;
import wash.rocket.xor.rocketwash.model.AvailableTimesResult;
import wash.rocket.xor.rocketwash.model.CarMake;
import wash.rocket.xor.rocketwash.model.CarsAttributes;
import wash.rocket.xor.rocketwash.model.CarsMakes;
import wash.rocket.xor.rocketwash.model.CarsMakesResult;
import wash.rocket.xor.rocketwash.model.CarsProfileResult;
import wash.rocket.xor.rocketwash.model.ChoiceService;
import wash.rocket.xor.rocketwash.model.ChoiceServiceResult;
import wash.rocket.xor.rocketwash.model.MapRouteResult;
import wash.rocket.xor.rocketwash.model.Point;
import wash.rocket.xor.rocketwash.model.Profile;
import wash.rocket.xor.rocketwash.model.ReservationResult;
import wash.rocket.xor.rocketwash.model.ReverseGeocoding;
import wash.rocket.xor.rocketwash.model.TimePeriods;
import wash.rocket.xor.rocketwash.model.WashService;
import wash.rocket.xor.rocketwash.requests.AvailableTimesRequest;
import wash.rocket.xor.rocketwash.requests.CarsMakesRequest;
import wash.rocket.xor.rocketwash.requests.ChoiseServiceRequest;
import wash.rocket.xor.rocketwash.requests.MapDirectionRequest;
import wash.rocket.xor.rocketwash.requests.MapReverceGeocodingRequest;
import wash.rocket.xor.rocketwash.requests.ReservationRequest;
import wash.rocket.xor.rocketwash.util.Constants;
import wash.rocket.xor.rocketwash.util.util;
import wash.rocket.xor.rocketwash.widgets.CalendarScrollWidget;
import wash.rocket.xor.rocketwash.widgets.MarginDecoration;
import wash.rocket.xor.rocketwash.widgets.NestedScrollView;
import wash.rocket.xor.rocketwash.widgets.NiceSupportMapFragment;
import wash.rocket.xor.rocketwash.widgets.SlidingTabLayout;

@SuppressLint("LongLogTag")
public class WashServiceInfoFragment extends BaseFragment {

    public static final String TAG = "WashServiceInfoFragment";

    private static final String POINTS = "points";
    private static final String ID_SERVICE = "id_service";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";
    private static final String TITLE = "title";
    private static final String SERVICE = "service";

    private static final int FRAGMENT_SERVCES = 1;
    private static final int FRAGMENT_PROFILE_EDIT = 2;


    private static final int DIALOG_WASH1 = 3;
    private static final int DIALOG_WASH2 = 4;
    private static final String DIALOG_WASH1_TAG = "DIALOG_WASH1";
    private static final String DIALOG_WASH2_TAG = "DIALOG_WASH2";

    private final int MAX_MARKERS = 50;

    private GoogleMap mMap;
    private Button mDisconnect;
    private Intent mServiceIntent;
    private boolean isBoundService;
    private NestedScrollView mScrollView1;
    private ScrollView mScrollView;
    private FrameLayout time_content;

    // private LockedScrollView mContent;
    private LinearLayout mContent;
    private RelativeLayout actionWash;
    private LinearLayout mNoTime;

    private ArrayList<Point> mPoints;
    private ArrayList<Marker> mMarkers;

    private ActionButton fab;
    private Toolbar toolbar;

    private int mIdService;
    private double mLatitude = 0;
    private double mLongitude = 0;
    private String mTitle;

    private TableLayout tableServicesContent;

    private ImageView imgChoiseServices;
    private ImageView imgAddCars;
    private RadioGroup radioGroupCars;

    private LayoutInflater mInflater;
    private List<CarsMakes> list_cars;
    private SlidingTabLayout mSlidingTabLayout = null;
    private ArrayList<ChoiceService> list;
    private CalendarScrollWidget mCalendar;
    private WashService mService;
    private Typeface mFont;
    private TextView txtPrice;
    private TextView txtBal;
    private TextView txtDiscount;
    private TextView txtDiscountSrv;
    private TextView txtFullPrice;
    private TextView txtStub;
    private TextView txtInfoTitile;
    private TextView txtInfoDistance;
    private ProgressBar infoProgressBar;
    private ProgressBar mProgressBar1;
    private ProgressBar mProgressBar2;
    private ProgressBar progressBar;
    private ProgressBar mProgressBar3; // times


    private Button infoBtnPath;
    private GoogleMap.InfoWindowAdapter infoBaloon;
    private Marker mMarker;

    private int heightMap = 0;
    private int mDiscount = 0;

    private com.software.shell.fab.ActionButton fab1;
    private Polyline mPolyLines;

    private String selected_time;
    private String first_time;

    private Button share;
    private Marker mPositionMarker;

    private boolean loading = false;

    public static WashServiceInfoFragment newInstance(int id_service, double lat, double lon, String title, WashService service) {
        WashServiceInfoFragment fragment = new WashServiceInfoFragment();
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
        //mLatitude = getArguments().getDouble(LATITUDE);
        //mLongitude = getArguments().getDouble(LONGITUDE);
        mTitle = getArguments().getString(TITLE);
        mService = getArguments().getParcelable(SERVICE);

        Location l = getLastLocation();
        if (l != null) {
            mLatitude = l.getLatitude();
            mLongitude = l.getLongitude();
        }

        if (mService != null)
            mService.var_dump();

        Log.d(TAG, "mLatitude = " + mLatitude);
        Log.d(TAG, "mLongitude = " + mLongitude);

        //ghj
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.w(TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_wash_service_info, container, false);

        mInflater = inflater;
        //mMap = ((MapFragmentWrapper) getChildFragmentManager().findFragmentById(R.id.map)).getMap();

        NiceSupportMapFragment mapFragment = (NiceSupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMap = mapFragment.getMap();

        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

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
                    } else
                        getSpiceManager().execute(new MapReverceGeocodingRequest(mService.getLatitude(), mService.getLongitude()), "direction", DurationInMillis.ALWAYS_EXPIRED, new MapReverceGeocodingListener());

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

        //addMarkers(mPoints);

        fab = (ActionButton) rootView.findViewById(R.id.fab);
        mScrollView1 = (NestedScrollView) rootView.findViewById(R.id.scroll);

        //mScrollView1.seton
        // NestedScrollView.OnScrollChangeListener

        /*
        mScrollView1.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.d("scroll", "scrollY = " + scrollY);
            }
        });*/

        mScrollView1.setNestedScrollingEnabled(true);
        mScrollView1.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {

                int scrollX = mScrollView1.getScrollX(); //for horizontalScrollView
                int scrollY = mScrollView1.getScrollY(); //for verticalScrollView
                //DO SOMETHING WITH THE SCROLL COORDINATES
                //Log.d("scroll", "scrollY = " + scrollY + "; heightMap = " + heightMap);

                //XXX
                /*
                if (scrollY > heightMap)
                    actionWash.setVisibility(View.VISIBLE);
                else
                    actionWash.setVisibility(View.GONE);*/

                actionWash.setVisibility(View.GONE);
            }
        });

        mContent = (LinearLayout) rootView.findViewById(R.id.content_car);
        /*
        ((MapFragmentWrapper) getChildFragmentManager().findFragmentById(R.id.map)).setListener(new MapFragmentWrapper.OnTouchListener() {
            @Override
            public void onTouch() {
                mScrollView1.requestDisallowInterceptTouchEvent(true);
            }
        });*/

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

        toolbar = setToolbar(rootView, mTitle);

        /*
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(mTitle);
        }*/

        actionWash = (RelativeLayout) rootView.findViewById(R.id.actionWash);
        actionWash.setVisibility(View.GONE);

        actionWash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reservation();

                //XXX move to func
                Date d1 = util.getDate(first_time);
                if (d1 == null)
                    d1 = new Date();
                String s = "Время мойки: " + util.dateToHM(d1) + "\n";
                s = s + "\n";
                s = s + "Выбранные услуги:" + "\n";
                s = s + getTextServices();
                showDialog(R.string.rec_on_carwash, s, DIALOG_WASH2, DIALOG_WASH2_TAG);
            }
        });

        //actionWash.setVisibility(View.GONE);
        fab.setVisibility(View.INVISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(first_time))
                    return;

                Date d1 = util.getDatenoUTC(first_time);
                if (d1 == null)
                    d1 = new Date();
                String s = "Время мойки: " + util.dateToHM(d1) + "\n";
                s = s + "\n";
                s = s + "Выбранные услуги:" + "\n";
                s = s + getTextServices();

                showDialog(R.string.rec_on_carwash, s, DIALOG_WASH2, DIALOG_WASH2_TAG);
            }
        });

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

        initControls(rootView);

        return rootView;
    }

    private void initControls(View rootView) {

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarMain);
        progressBar.setVisibility(View.GONE);

        imgChoiseServices = (ImageView) rootView.findViewById(R.id.imgChoiseServices);
        imgChoiseServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int id_model = pref.getCarModelId();
                if (id_model == 0)
                    id_model = getApp().getProfile().getCars_attributes().get(0).getCar_model_id();

                ChoiceServicesFragment f = ChoiceServicesFragment.newInstance(mIdService, id_model, list);
                f.setTargetFragment(WashServiceInfoFragment.this, FRAGMENT_SERVCES);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .add(R.id.container, f, ChoiceServicesFragment.TAG)
                        .addToBackStack(TAG).commit();
            }
        });

        imgAddCars = (ImageView) rootView.findViewById(R.id.imgAddCars);
        imgAddCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProfileEditFragment f = ProfileEditFragment.newInstance(getApp().getProfile(), false);
                f.setTargetFragment(WashServiceInfoFragment.this, FRAGMENT_PROFILE_EDIT);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .add(R.id.container, f, ProfileEditFragment.TAG)
                        .addToBackStack(TAG).commit();
            }
        });

        radioGroupCars = (RadioGroup) rootView.findViewById(R.id.radioGroupCars);
        radioGroupCars.removeAllViews();

        radioGroupCars.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton radioButton = (RadioButton) radioGroupCars.findViewById(checkedId);
                int index = radioGroupCars.indexOfChild(radioButton);

                //XXX

                if (radioButton != null && !loading) {

                    fab.setVisibility(View.INVISIBLE);

                    pref.setCarName(radioButton.getText().toString());
                    pref.setUseCar(index);

                    CarsAttributes r = (CarsAttributes) radioButton.getTag();
                    pref.setCarModelId(r.getCar_model_id());
                    pref.setCarNum(r.getTag());
                    pref.setCarName(r.getBrandName() + " " + r.getModelName());

                    mProgressBar2.setVisibility(View.VISIBLE);
                    getSpiceManager().execute(new ChoiseServiceRequest(mIdService, pref.getCarModelId(), pref.getSessionID()), mIdService + "_services_chose_" + pref.getUseCar(), DurationInMillis.ALWAYS_EXPIRED, new ChoiceServiceRequestListener());

                    radioGroupCars.setEnabled(false);
                }
            }
        });

        //CalendarScrollWidget.LayoutParams lp = new CalendarScrollWidget.LayoutParams();
        //mCalendar = new CalendarScrollWidget(getActivity(), null);
        //mCalendar.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        //time_content = (FrameLayout) rootView.findViewById(R.id.time_content);
        //time_content.addView(mCalendar);

        mCalendar = (CalendarScrollWidget) rootView.findViewById(R.id.time_content);
        mSlidingTabLayout = (SlidingTabLayout) rootView.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.tab_time, R.id.title);
        mCalendar.setVisibility(View.GONE);

        Resources res = getResources();
        mSlidingTabLayout.setSelectedIndicatorColors(res.getColor(R.color.green_rocket));
        mSlidingTabLayout.setDistributeEvenly(true);

        mSlidingTabLayout.setOnTabSelected(new SlidingTabLayout.OnTabSelected() {
            @Override
            public void onTabSelected(int item) {
                mCalendar.selected(item);
            }
        });

        mCalendar.setOnPagetChange(new CalendarScrollWidget.IOnPageChanged() {
            @Override
            public void onPagetChange(int page) {
                mSlidingTabLayout.selected(page);
            }
        });

        View v = rootView.findViewById(R.id.root_time_info);
        mNoTime = (LinearLayout) v.findViewById(R.id.notime);
        mNoTime.setVisibility(View.GONE);
        mProgressBar3 = (ProgressBar) v.findViewById(R.id.progressBar3);

        v = rootView.findViewById(R.id.content_car);
        mProgressBar1 = (ProgressBar) v.findViewById(R.id.progressBar);
        v = rootView.findViewById(R.id.content_choise_services);
        mProgressBar2 = (ProgressBar) v.findViewById(R.id.progressBar1);


        mProgressBar1.setVisibility(View.VISIBLE);
        mProgressBar2.setVisibility(View.VISIBLE);
        mProgressBar3.setVisibility(View.VISIBLE);

        tableServicesContent = (TableLayout) rootView.findViewById(R.id.tableServicesContent);
        tableServicesContent.removeAllViews();
        mFont = Typeface.createFromAsset(getActivity().getAssets(), "roboto.ttf");

        txtPrice = (TextView) rootView.findViewById(R.id.txtPrice);
        txtDiscount = (TextView) rootView.findViewById(R.id.txtDiscount);
        txtDiscountSrv = (TextView) rootView.findViewById(R.id.txtDiscountSrv);
        txtBal = (TextView) rootView.findViewById(R.id.txtBal);
        txtFullPrice = (TextView) rootView.findViewById(R.id.txtFullPrice);

        txtPrice.setTypeface(mFont);
        txtDiscountSrv.setTypeface(mFont);
        txtFullPrice.setTypeface(mFont);

        txtPrice.setText(String.format("%d %s", 0, getActivity().getString(R.string.rubleSymbolJava)));
        txtDiscountSrv.setText(String.format("%d %s", 0, getActivity().getString(R.string.rubleSymbolJava)));
        txtFullPrice.setText(String.format("%d %s", 0, getActivity().getString(R.string.rubleSymbolJava)));

        fab1 = (ActionButton) rootView.findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSpiceManager().execute(new MapDirectionRequest(new LatLng(mLatitude, mLongitude), new LatLng(mService.getLatitude(), mService.getLongitude())), "direction", DurationInMillis.ONE_SECOND, new MapDirectionRouteListener());
            }
        });

        actionWash = (RelativeLayout) rootView.findViewById(R.id.actionWash);
        txtStub = (TextView) rootView.findViewById(R.id.txtStub);

        if (mService != null)
            txtStub.setText(mService.getMobile_stub_text());
        else
            txtStub.setText("");

        ViewGroup content_share = (ViewGroup) rootView.findViewById(R.id.content_share);
        share = (Button) content_share.findViewById(R.id.btnShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Profile prof = getApp().getProfile();

        if (prof != null)
            mDiscount = prof.getDiscount();
        else
            mDiscount = 0;

        txtDiscount.setText(String.format(getActivity().getString(R.string.fragment_info_wash_service_my_discount), mDiscount));
        txtBal.setText(String.format(getActivity().getString(R.string.fragment_info_wash_service_my_counter), 0));
        getSpiceManager().execute(new CarsMakesRequest(""), "carsmakes", DurationInMillis.ONE_HOUR, new CarsRequestListener());

        //TimeZone utc = TimeZone.getTimeZone("UTC");
        //Calendar c = Calendar.getInstance(utc);
        Calendar c = Calendar.getInstance();
        //String a = util.dateToZZ(c.getTime());
        long dd = c.getTime().getTime() + 1000 * 60 * 10;
        Date d = new Date(dd);
        String a = util.dateToZZ1(d);

        c.add(Calendar.HOUR_OF_DAY, 24 * 2);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        String b = util.dateToZZ(c.getTime());
        getSpiceManager().execute(new AvailableTimesRequest(pref.getSessionID(), mService.getId(), a, b, 30), "times", DurationInMillis.ALWAYS_EXPIRED, new AvailableTimesRequestListener());

        restoreTargets();
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

        //stopTracking();
        doUnbindLocationService();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        setTargetFragment(null, -1);
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

        BaseFragment f = (BaseFragment) getFragmentManager().findFragmentById(R.id.container);
        if (f != null && !f.equals(this))
            return false;

        Log.d(TAG, "onOptionsItemSelected");

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
        Fragment f = getChildFragmentManager().findFragmentById(R.id.map);
        if (f != null)
            getChildFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
    }

    private int res = 0;

    public final class CarsProfileRequestListener implements RequestListener<CarsProfileResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            showToastError(R.string.error_loading_data);
            mProgressBar1.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(final CarsProfileResult result) {

            Log.d("CarsProfileRequestListener", "onRequestSuccess = " + (result.getStatus() == null ? "null" : result.getStatus()));
            res = res + 1000;
            if (Constants.SUCCESS.equals(result.getStatus())) {

                radioGroupCars.removeAllViews();
                if (result.getData() != null) {

                    loading = true;
                    try {

                        int selected = pref.getUseCar();

                        if (selected > (result.getData().size() - 1))
                            selected = 0;

                        Log.e("CarsProfileRequestListener", "selected = " + selected);

                        for (int i = 0; i < result.getData().size(); i++) {
                            CarsAttributes r = result.getData().get(i);
                            RadioButton rb = (RadioButton) mInflater.inflate(R.layout.radio_button, null);
                            String a = "", b = "";

                            for (int j = 0; j < list_cars.size(); j++) {
                                if (list_cars.get(j).getId() == r.getCar_make_id()) {
                                    a = list_cars.get(j).getName();
                                    CarMake m;
                                    for (int k = 0; k < list_cars.get(j).getCar_models().size(); k++) {
                                        m = list_cars.get(j).getCar_models().get(k);
                                        if (m.getId() == r.getCar_model_id())
                                            b = m.getName();
                                    }
                                    break;
                                }
                            }

                            r.setBrandName(a);
                            r.setModelName(b);
                            if (TextUtils.isEmpty(r.getTag()))
                                rb.setText(String.format("%s %s", a, b));
                            else
                                rb.setText(String.format("%s %s (%s)", a, b, r.getTag()));
                            rb.setTag(r);
                            rb.setId(res + i);
                            radioGroupCars.addView(rb);
                            rb.setChecked(selected == i);
                        }

                    } finally {

                        radioGroupCars.post(new Runnable() {
                            @Override
                            public void run() {
                                radioGroupCars.invalidate();
                            }
                        });

                        loading = false;
                    }

                    int id_model = pref.getCarModelId();
                    if (id_model == 0) {
                        id_model = getApp().getProfile().getCars_attributes().get(0).getCar_model_id();
                        pref.setCarModelId(id_model);
                    }

                    getSpiceManager().execute(new ChoiseServiceRequest(mIdService, id_model, pref.getSessionID()), mIdService + "_services_chose_" + pref.getUseCar(), DurationInMillis.ALWAYS_EXPIRED, new ChoiceServiceRequestListener());
                }
            } else {
                showToastError(R.string.error_loading_data);
            }
            mProgressBar1.setVisibility(View.GONE);
        }
    }

    public final class CarsRequestListener implements RequestListener<CarsMakesResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            showToastError(R.string.error_loading_data);
            mProgressBar1.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(final CarsMakesResult result) {
            if (result != null) {
                Log.d("CarsRequestListener", "onRequestSuccess = " + (result.getStatus() == null ? "null" : result.getStatus()));
                list_cars = result.getData();
                //getSpiceManager().execute(new CarsProfileRequest(pref.getSessionID()), "cars_profile", DurationInMillis.ALWAYS_EXPIRED, new CarsProfileRequestListener());

                res = res + 1000;
                //if (Constants.SUCCESS.equals(result.getStatus())) {

                radioGroupCars.removeAllViews();
                if (result.getData() != null) {

                    loading = true;
                    try {
                        int selected = pref.getUseCar();

                        if (selected > (result.getData().size() - 1))
                            selected = 0;

                        Log.e("CarsProfileRequestListener", "selected = " + selected);

                        for (int i = 0; i < getApp().getProfile().getCars_attributes().size(); i++) {
                            CarsAttributes r = getApp().getProfile().getCars_attributes().get(i);
                            RadioButton rb = (RadioButton) mInflater.inflate(R.layout.radio_button, null);
                            String a = "", b = "";

                            for (int j = 0; j < list_cars.size(); j++) {
                                if (list_cars.get(j).getId() == r.getCar_make_id()) {
                                    a = list_cars.get(j).getName();
                                    CarMake m;
                                    for (int k = 0; k < list_cars.get(j).getCar_models().size(); k++) {
                                        m = list_cars.get(j).getCar_models().get(k);
                                        if (m.getId() == r.getCar_model_id())
                                            b = m.getName();
                                    }
                                    break;
                                }
                            }

                            r.setBrandName(a);
                            r.setModelName(b);
                            if (TextUtils.isEmpty(r.getTag()))
                                rb.setText(String.format("%s %s", a, b));
                            else
                                rb.setText(String.format("%s %s (%s)", a, b, r.getTag()));
                            rb.setTag(r);
                            rb.setId(res + i);
                            radioGroupCars.addView(rb);
                            rb.setChecked(selected == i);
                        }

                    } finally {

                        radioGroupCars.post(new Runnable() {
                            @Override
                            public void run() {
                                radioGroupCars.invalidate();
                            }
                        });

                        loading = false;
                    }

                    int id_model = pref.getCarModelId();
                    if (id_model == 0) {
                        id_model = getApp().getProfile().getCars_attributes().get(0).getCar_model_id();
                        pref.setCarModelId(id_model);
                    }

                    getSpiceManager().execute(new ChoiseServiceRequest(mIdService, id_model, pref.getSessionID()), mIdService + "_services_chose_" + pref.getUseCar(), DurationInMillis.ALWAYS_EXPIRED, new ChoiceServiceRequestListener());
                }
                mProgressBar1.setVisibility(View.GONE);
                overrideFonts(getActivity(), radioGroupCars);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);

        if (getActivity() == null)
            return;

        toolbar = setToolbar(getView(), mTitle);

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case FRAGMENT_SERVCES:
                    ArrayList<ChoiceService> l = data.getParcelableArrayListExtra("list");
                    fillChoiceServices(l);
                    break;
                case FRAGMENT_PROFILE_EDIT:
                    mProgressBar1.setVisibility(View.VISIBLE);
                    getSpiceManager().execute(new CarsMakesRequest(""), "carsmakes", DurationInMillis.ONE_HOUR, new CarsRequestListener());
                    break;

                case DIALOG_WASH1:
                    reservation(selected_time);
                    break;

                case DIALOG_WASH2:
                    reservation(first_time);
                    break;

            }
        } else {

            switch (requestCode) {
                case DIALOG_WASH1:
                    clearListSelectors();
                    break;
                case DIALOG_WASH2:
                    clearListSelectors();
                    break;
            }
        }
    }

    //XXX move to base fragment
    private ArrayList<TimePeriods> list_today = new ArrayList<>();
    private ArrayList<TimePeriods> list_tomorrow = new ArrayList<>();
    private TimeRecyclerViewAdapter ad1;
    private TimeRecyclerViewAdapter ad2;

    private void clearListSelectors() {

        Log.d(TAG, "clearListSelectors");

        selected_time = "";
        Handler h = new Handler();
        h.post(new Runnable() {
            @Override
            public void run() {

                ad1.setSelectionItemId(-1);
                ad2.setSelectionItemId(-1);

                ad1.notifyDataSetChanged();
                ad2.notifyDataSetChanged();
            }
        });
    }

    private void fillCalendar(List<TimePeriods> time_periods) {

        boolean today = false;
        boolean tomorrow = false;
        ArrayList<View> pages = new ArrayList<>();

        list_today.clear();
        list_tomorrow.clear();

        if (time_periods != null) {
            Log.d("fillCalendar", "time_periods.size() = " + time_periods.size());
            if (time_periods.size() > 0) {

                //XXX
                //first_time = time_periods.get(0).getTime_from();
                first_time = time_periods.get(0).getTime_from_no_time_zone();

                TimePeriods d;
                for (int i = 0; i < time_periods.size(); i++) {
                    d = time_periods.get(i);
                    //d.setToday(time_periods.get(0).getDate());
                    Calendar c = Calendar.getInstance();
                    d.setToday(c.getTime());

                    if (d.isToday()) {
                        today = true;
                        list_today.add(d);
                        //Log.d("fillCalendar", util.dateToHM(d.getDate()));
                    }
                    if (d.isTomorrow()) {
                        tomorrow = true;
                        list_tomorrow.add(d);
                    }
                }

                if (today) {
                    mSlidingTabLayout.addTab(getActivity().getString(R.string.today), "", true);
                    ad1 = new TimeRecyclerViewAdapter(list_today);
                    RecyclerView rv1 = new RecyclerView(getActivity());
                    rv1.setHasFixedSize(true);
                    rv1.setAdapter(ad1);
                    rv1.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                    rv1.addItemDecoration(new MarginDecoration(getActivity()));
                    pages.add(rv1);
                    ad1.notifyDataSetChanged();
                    ad1.setOnSelectedItem(onsel);
                }

                if (tomorrow) {
                    mSlidingTabLayout.addTab(getActivity().getString(R.string.tomorrow), "", !today);
                    ad2 = new TimeRecyclerViewAdapter(list_tomorrow);
                    RecyclerView rv1 = new RecyclerView(getActivity());
                    rv1.setHasFixedSize(true);
                    rv1.setAdapter(ad2);
                    rv1.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                    rv1.addItemDecoration(new MarginDecoration(getActivity()));
                    pages.add(rv1);
                    ad2.notifyDataSetChanged();
                    ad2.setOnSelectedItem(onsel);
                }

                mCalendar.setVisibility(View.VISIBLE);
                mCalendar.setPages(pages);
                overrideFonts(getActivity(), mSlidingTabLayout);

            } else {
                mSlidingTabLayout.setVisibility(View.GONE);
                mNoTime.setVisibility(View.VISIBLE);
                mCalendar.setVisibility(View.GONE);
            }
        }
    }

    TimeRecyclerViewAdapter.IOnSelectedItem onsel = new TimeRecyclerViewAdapter.IOnSelectedItem() {
        @Override
        public void onSelectedItem(TimePeriods item, int position) {
            //XXX
            //selected_time = item.getTime_from();
            selected_time = item.getTime_from_no_time_zone();

            Log.d(TAG, "selected_time = " + selected_time);

            String s = "Время мойки: " + util.dateToHM(item.getDate()) + "\n";
            s = s + "\n";
            s = s + "Выбранные услуги:" + "\n";
            s = s + getTextServices();
            showDialog(R.string.rec_on_carwash, s, DIALOG_WASH1, DIALOG_WASH1_TAG);
        }
    };

    public final class MapReverceGeocodingListener implements RequestListener<ReverseGeocoding> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            showToastError("Ошибка получения гео данных");
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

    public final class ChoiceServiceRequestListener implements RequestListener<ChoiceServiceResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            showToastError(R.string.error_loading_data);
            mProgressBar2.setVisibility(View.GONE);
            radioGroupCars.setEnabled(true);
            fab.setVisibility(View.VISIBLE);
        }

        @Override
        public void onRequestSuccess(final ChoiceServiceResult result) {
            Log.d("ChoiseServiceRequestListener", "onRequestSuccess = " + result.getStatus() == null ? "null" : result.getStatus());

            if (Constants.SUCCESS.equals(result.getStatus())) {
                Log.d("ChoiseServiceRequestListener", "onRequestSuccess fill data");

                if (list == null)
                    list = new ArrayList<>();
                list.clear();

                if (result.getData() != null) {
                    for (int i = 0; i < result.getData().size(); i++) {
                        ChoiceService b = result.getData().get(i).getClone();
                        if (mService != null && !TextUtils.isEmpty(mService.getService_name()) && mService.getService_name().toLowerCase().trim().equals(b.getName() == null ? "xcv" : b.getName().toLowerCase().trim())) {
                            b.setCheck(1);
                            Log.e("fghfgh", b.getName() == null ? "" : b.getName() + " " + b.getPrice());
                        }
                        list.add(b);
                    }
                }

                fillChoiceServices(list);
            } else {

            }

            fab.setVisibility(View.VISIBLE);
            radioGroupCars.setEnabled(true);
            mProgressBar2.setVisibility(View.GONE);
        }
    }

    //XXX
    private void fillChoiceServices(ArrayList<ChoiceService> in) {
        tableServicesContent.removeAllViews();
        list = in;

        Log.d(TAG, "list = " + (list == null ? "null" : "data"));
        if (list != null) {
            int price = 0;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getCheck() == 1) {
                    TableRow t = (TableRow) mInflater.inflate(R.layout.service_table_row, null);
                    TextView sum = (TextView) t.findViewById(R.id.sum);
                    CheckBox c = (CheckBox) t.findViewById(R.id.checkBox);

                    c.setChecked(true);
                    c.setText(list.get(i).getName());
                    c.setOnClickListener(mOnServicesChange);
                    c.setTag(i);

                    sum.setTypeface(mFont);
                    sum.setText(String.format("%d %s", list.get(i).getPrice(), getActivity().getString(R.string.rubleSymbolJava)));
                    tableServicesContent.addView(t);
                    price = price + list.get(i).getPrice();
                }
            }

            int discount = 0;

            txtPrice.setText(String.format("%d %s", price, getActivity().getString(R.string.rubleSymbolJava)));
            txtDiscountSrv.setText(String.format("%d %s", mDiscount, getActivity().getString(R.string.rubleSymbolJava)));
            txtFullPrice.setText(String.format("%d %s", price, getActivity().getString(R.string.rubleSymbolJava)));
        }

        overrideFonts(getActivity(), tableServicesContent);
    }


    private String getTextServices() {
        String s = "";

        if (list != null) {
            int price = 0;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getCheck() == 1) {
                    s = s + list.get(i).getName() + ":  " + String.format("%d %s", list.get(i).getPrice(), getActivity().getString(R.string.rubleSymbolJava)) + "\n";
                    price = price + list.get(i).getPrice();
                }
            }
            s = s + "\n";
            s = s + getActivity().getString(R.string.include_info_wash_sum_price) + ": " + String.format("%d %s", price, getActivity().getString(R.string.rubleSymbolJava));
            return s;
        }

        return "";
    }

    private View.OnClickListener mOnServicesChange = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                ChoiceService b = list.get((Integer) v.getTag());
                b.setCheck(((CheckBox) v).isChecked() ? 1 : 0);
            }

            int price = 0;

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isCheck()) {
                    price = price + list.get(i).getPrice();
                }
            }

            txtPrice.setText(String.format("%d %s", price, getActivity().getString(R.string.rubleSymbolJava)));
            txtDiscountSrv.setText(String.format("%d %s", mDiscount, getActivity().getString(R.string.rubleSymbolJava)));
            txtFullPrice.setText(String.format("%d %s", price, getActivity().getString(R.string.rubleSymbolJava)));
        }
    };

    private String reserved_time;

    private void reservation(String time) {

        Log.d(TAG, "reservation time = " + time);

        Profile prof = getApp().getProfile();
        if (prof != null && prof.isPhone_verified()) {
            if (list == null || list.size() <= 0) {
                clearListSelectors();
                showToastWarn(R.string.fragment_info_wash_service_no_services_select);
                return;
            } else {
                int s = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isCheck())
                        s++;
                }
                if (s == 0) {
                    showToastWarn(R.string.fragment_info_wash_service_no_services_select);
                    clearListSelectors();
                    return;
                }
            }

            reserved_time = time;

            progressBar.setVisibility(View.VISIBLE);
            int id = radioGroupCars.getCheckedRadioButtonId();
            View v = radioGroupCars.findViewById(id);
            CarsAttributes c = (CarsAttributes) v.getTag();
            getSpiceManager().execute(new ReservationRequest(pref.getSessionID(), mService.getId(), c.getId(), list, time), "reservation", DurationInMillis.ALWAYS_EXPIRED, new ReservationRequestListener());

        } else if (prof != null) {
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    .add(R.id.container, new SendSmsFragment(), SendSmsFragment.TAG)
                    .addToBackStack(TAG).commit();
        }
    }


    public final class ReservationRequestListener implements RequestListener<ReservationResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            showToastError(R.string.fragment_info_wash_service_reserved_fail);
            progressBar.setVisibility(View.GONE);
        }


        @Override
        public void onRequestSuccess(final ReservationResult result) {
            progressBar.setVisibility(View.GONE);

            if (Constants.SUCCESS.equals(result.getStatus())) {

                showToastOk(R.string.fragment_info_wash_service_reserved_succes);
                getActivity().getSupportFragmentManager().popBackStack();
                reservationAction(mService.getId(), mService.getName());

                util.addAlarmScheduleNotify(getActivity(), reserved_time, mService.getId());

                WashServiceInfoFragmentReserved f = WashServiceInfoFragmentReserved.newInstance(mIdService, mLatitude, mLongitude, mTitle, mService, result.getData());
                f.setTargetFragment(getTargetFragment(), getTargetRequestCode());
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .add(R.id.container, f, WashServiceInfoFragmentReserved.TAG)
                        .addToBackStack(null)
                        .commit();

            } else {
                showToastError(result.getData().getResult());
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        //Log.d(TAG, "onLocationChanged");
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

    private class AvailableTimesRequestListener implements RequestListener<wash.rocket.xor.rocketwash.model.AvailableTimesResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            mSlidingTabLayout.setVisibility(View.GONE);
            mNoTime.setVisibility(View.VISIBLE);
            mProgressBar3.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(AvailableTimesResult availableTimesResult) {

            if (availableTimesResult != null) {
                List<TimePeriods> times = new ArrayList<>();
                TimePeriods t;
                for (int i = 0; i < availableTimesResult.getData().size(); i++) {
                    String a = availableTimesResult.getData().get(i);
                    t = new TimePeriods();
                    t.setTime_from(a);
                    t.setTime_from_no_time_zone(a);
                    times.add(t);
                }

                if (times.size() > 0) {
                    //XXX
                    //first_time = times.get(0).getTime_from();
                    first_time = times.get(0).getTime_from_no_time_zone();
                }

                fillCalendar(times);
            }

            fab.setVisibility(View.VISIBLE);
            mProgressBar3.setVisibility(View.GONE);
        }
    }

    private void showDialog(int title, String message, int id, String tag) {
        AlertDialogFragment f = AlertDialogFragment.newInstance(title, message, id, this);
        f.show(getFragmentManager(), tag);
    }

    // change to interface
    @Override
    public void restoreTargets() {
        Fragment f;
        f = getFragmentManager().findFragmentByTag(ChoiceServicesFragment.TAG);
        if (f != null)
            f.setTargetFragment(WashServiceInfoFragment.this, FRAGMENT_SERVCES);
    }
}
