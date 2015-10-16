package wash.rocket.xor.rocketwash.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.CarsMakes;
import wash.rocket.xor.rocketwash.model.ChoiseService;
import wash.rocket.xor.rocketwash.model.ChoiseServiceResult;
import wash.rocket.xor.rocketwash.model.Profile;
import wash.rocket.xor.rocketwash.model.ReservationResult;
import wash.rocket.xor.rocketwash.model.TimePeriods;
import wash.rocket.xor.rocketwash.model.WashService;
import wash.rocket.xor.rocketwash.requests.ChoiseServiceRequest;
import wash.rocket.xor.rocketwash.requests.ReservationRequest;
import wash.rocket.xor.rocketwash.util.Constants;
import wash.rocket.xor.rocketwash.util.util;
import wash.rocket.xor.rocketwash.widgets.ButtonWithState;
import wash.rocket.xor.rocketwash.widgets.CalendarScrollWidget;
import wash.rocket.xor.rocketwash.widgets.SlidingTabLayout;


@SuppressLint("LongLogTag")
public class WashServiceInfoFragmentQuick extends BaseFragment {

    public static final String TAG = "WashServiceInfoFragmentQuick";

    private static final String POINTS = "points";
    private static final String ID_SERVICE = "id_service";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";
    private static final String TITLE = "title";
    private static final String SERVICE = "service";

    private static final int FRAGNEBT_SERVCES = 1;

    private static final int DIALOG_WASH1 = 3;
    private static final int DIALOG_WASH2 = 4;
    private static final String DIALOG_WASH1_TAG = "DIALOG_WASH1";
    private static final String DIALOG_WASH2_TAG = "DIALOG_WASH2";

    private Button mDisconnect;
    private Intent mServiceIntent;
    private boolean isBoundService;
    private NestedScrollView mScrollView1;
    private ScrollView mScrollView;
    private FrameLayout time_content;

    // private LockedScrollView mContent;
    private LinearLayout mContent;
    private RelativeLayout actionWash;

    private ActionButton fab;
    private Toolbar toolbar;

    private int mIdService;
    private double mLatitude;
    private double mLongitude;
    private String mTitle;

    private TableLayout tableServicesContent;

    private LayoutInflater mInflater;
    private List<CarsMakes> list_cars;
    private SlidingTabLayout mSlidingTabLayout = null;
    private ArrayList<ChoiseService> list;
    private CalendarScrollWidget mCalendar;
    private WashService mService;
    private Typeface mFont;
    private TextView txtPrice;
    private TextView txtDiscount;
    private TextView txtDiscountSrv;
    private TextView txtFullPrice;
    private TextView txtStub;
    private TextView txtInfoTitile;
    private TextView txtInfoDistance;
    private TextView txtCarName;

    private TextView txtWashTitle;
    private ProgressBar infoProgressBar;
    private Button infoBtnPath;
    private GoogleMap.InfoWindowAdapter infoBaloon;
    private Marker mMarker;
    private LinearLayout mNoTime;

    private ProgressBar progressBar;
    private ProgressBar mProgressBar2;
    private ProgressBar mProgressBar3; // times

    private String selected_time;
    private String first_time;

    private ActionButton fab1;
    private Polyline mPolyLines;

    public static WashServiceInfoFragmentQuick newInstance(int id_service, double lat, double lon, String title, WashService service) {
        WashServiceInfoFragmentQuick fragment = new WashServiceInfoFragmentQuick();
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


        mIdService = getArguments().getInt(ID_SERVICE);
        // mLatitude = getArguments().getDouble(LATITUDE);
        // mLongitude = getArguments().getDouble(LONGITUDE);
        mTitle = getArguments().getString(TITLE);
        mService = getArguments().getParcelable(SERVICE);

        if (mService != null)
            mService.var_dump();


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
        View rootView = inflater.inflate(R.layout.fragment_wash_service_quick_apply, container, false);

        mInflater = inflater;

        mContent = (LinearLayout) rootView.findViewById(R.id.content_car);
        /*
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //toolbar.setTitle(mTitle);
        }*/

        setToolbar(rootView, mTitle);

        initControls(rootView);


        return rootView;
    }

    private void initControls(View rootView) {
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarMain);
        progressBar.setVisibility(View.GONE);

        CalendarScrollWidget.LayoutParams lp = new CalendarScrollWidget.LayoutParams();

        mCalendar = new CalendarScrollWidget(getActivity(), null);
        mCalendar.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        time_content = (FrameLayout) rootView.findViewById(R.id.time_content);
        time_content.addView(mCalendar);

        View v = rootView.findViewById(R.id.time_content_root);
        mNoTime = (LinearLayout) v.findViewById(R.id.notime);
        mNoTime.setVisibility(View.GONE);

        mProgressBar3 = (ProgressBar) v.findViewById(R.id.progressBar3);
        mProgressBar3.setVisibility(View.GONE);

        mSlidingTabLayout = (SlidingTabLayout) rootView.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.tab_time, R.id.title);

        Resources res = getResources();
        mSlidingTabLayout.setSelectedIndicatorColors(res.getColor(R.color.green_rocket));
        mSlidingTabLayout.setDistributeEvenly(true);

        mSlidingTabLayout.setOnTabSelected(new SlidingTabLayout.OnTabSelected() {
            @Override
            public void onTabSlected(int item) {
                mCalendar.selected(item);
            }
        });

        mCalendar.setOnPagetChange(new CalendarScrollWidget.IOnPageChanged() {
            @Override
            public void onPagetChange(int page) {
                mSlidingTabLayout.selected(page);
            }
        });

        if (mService != null) {
            fillCalendar(mService.getTime_periods());
        }

        tableServicesContent = (TableLayout) rootView.findViewById(R.id.tableServicesContent);
        tableServicesContent.removeAllViews();

        mFont = Typeface.createFromAsset(getActivity().getAssets(), "roboto.ttf");


        txtPrice = (TextView) rootView.findViewById(R.id.txtPrice);
        // txtDiscount = (TextView) rootView.findViewById(R.id.txtDiscount);
        txtDiscountSrv = (TextView) rootView.findViewById(R.id.txtDiscountSrv);
        txtFullPrice = (TextView) rootView.findViewById(R.id.txtFullPrice);

        txtPrice.setTypeface(mFont);
        //    txtDiscount.setTypeface(mFont);
        txtDiscountSrv.setTypeface(mFont);

        txtFullPrice.setTypeface(mFont);

        txtPrice.setText(String.format("%d %s", 0, getActivity().getString(R.string.rubleSymbolJava)));
        txtDiscountSrv.setText(String.format("%d %s", 0, getActivity().getString(R.string.rubleSymbolJava)));
        txtFullPrice.setText(String.format("%d %s", 0, getActivity().getString(R.string.rubleSymbolJava)));

        txtCarName = (TextView) rootView.findViewById(R.id.txtCarName);

        txtWashTitle = (TextView) rootView.findViewById(R.id.txtWashTitle);
        txtWashTitle.setText(mService.getName());

        txtStub = (TextView) rootView.findViewById(R.id.txtStub);
        txtStub.setText(mService.getAddress());

        v = rootView.findViewById(R.id.content_choise_services);
        mProgressBar2 = (ProgressBar) v.findViewById(R.id.progressBar1);
        mProgressBar2.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txtCarName.setText(pref.getCarName() + " (" + pref.getCarNum() + ")");
        getSpiceManager().execute(new ChoiseServiceRequest(mIdService, pref.getCarModelId(), pref.getSessionID()), mIdService + "_services_chose_" + pref.getUseCar(), DurationInMillis.ALWAYS_EXPIRED, new ChoiseServiceRequestListener());
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
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if (resultCode == Activity.RESULT_OK) {

            /*

            tableServicesContent.removeAllViews();
            list = data.getParcelableArrayListExtra("list");

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
                        sum.setTypeface(mFont);
                        sum.setText(String.format("%d %s", list.get(i).getPrice(), getActivity().getString(R.string.rubleSymbolJava)));
                        tableServicesContent.addView(t);
                        price = price + list.get(i).getPrice();
                    }
                }

                int discount = 0;
                txtPrice.setText(String.format("%d %s", price, getActivity().getString(R.string.rubleSymbolJava)));
                txtDiscount.setText(String.format("%d %s", discount, getActivity().getString(R.string.rubleSymbolJava)));
                txtFullPrice.setText(String.format("%d %s", price, getActivity().getString(R.string.rubleSymbolJava)));

            }*/

            switch (requestCode) {
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
                    if (old_b != null)
                        old_b.setSelected(false);
                    break;
                case DIALOG_WASH2:
                    //old_b.setSelected(false);
                    break;
            }
        }
    }

    private void fillCalendar(List<TimePeriods> time_periods) {

        Log.d("fillCalendar", "start");
        if (time_periods != null) {
            Log.d("fillCalendar", "time_periods.size() = " + time_periods.size());
            if (time_periods.size() > 0) {

                mNoTime.setVisibility(View.GONE);
                mSlidingTabLayout.setVisibility(View.VISIBLE);

                ArrayList<View> pages = new ArrayList<>();

                int k = 0;
                ViewGroup cnt = null;
                Button b1 = null;
                Button b2 = null;

                first_time = time_periods.get(0).getTime_from();

                for (int i = 0; i < time_periods.size(); i++) {
                    TimePeriods d = time_periods.get(i);

                    if (d.isToday()) {
                        if (k % 2 != 0) {

                            if (cnt == null)
                                cnt = (ViewGroup) (mInflater.inflate(R.layout.calendar_content, null));

                            ViewGroup gcnt = (ViewGroup) cnt.findViewById(R.id.content_calendar);
                            View b = mInflater.inflate(R.layout.calendar_row_buttons, null);

                            b1 = (ButtonWithState) b.findViewById(R.id.left);
                            b2 = (ButtonWithState) b.findViewById(R.id.right);

                            b1.setOnClickListener(mOnTime);
                            b2.setOnClickListener(mOnTime);

                            b2.setVisibility(View.INVISIBLE);
                            b1.setText(util.dateToHM(d.getDate()));
                            b1.setTag(d.getTime_from());
                            gcnt.addView(b);
                        } else {
                            if (b2 != null) {
                                b2.setVisibility(View.VISIBLE);
                                b2.setText(util.dateToHM(d.getDate()));
                                b2.setTag(d.getTime_from());
                            }
                        }
                        Log.d("fillCalendar", "date = " + util.dateToHM(d.getDate()));
                        k++;
                    }

                    Log.d("fillCalendar", "i%2 = " + i % 2);
                    Log.d("fillCalendar", "date = " + util.dateToHM(d.getDate()));
                    Log.d("fillCalendar", "date = " + d.getDate().toString());
                }

                if (cnt != null) {
                    mSlidingTabLayout.addTab("Сегодня", "", true);
                    //mSlidingTabLayout.addTab("Завтра", "", false);
                    pages.add(cnt);
                }

                k = 0;
                cnt = null;
                b1 = null;
                b2 = null;
                for (int i = 0; i < time_periods.size(); i++) {
                    TimePeriods d = time_periods.get(i);

                    if (d.isTomorrow()) {
                        if (k % 2 != 0) {
                            if (cnt == null)
                                cnt = (ViewGroup) mInflater.inflate(R.layout.calendar_content, null);

                            ViewGroup gcnt = (ViewGroup) cnt.findViewById(R.id.content_calendar);
                            View b = mInflater.inflate(R.layout.calendar_row_buttons, null);

                            b1 = (ButtonWithState) b.findViewById(R.id.left);
                            b2 = (ButtonWithState) b.findViewById(R.id.right);

                            b1.setOnClickListener(mOnTime);
                            b2.setOnClickListener(mOnTime);

                            b2.setVisibility(View.INVISIBLE);
                            b1.setText(util.dateToHM(d.getDate()));
                            b1.setTag(d.getTime_from());
                            gcnt.addView(b);
                        } else {
                            if (b2 != null) {
                                b2.setVisibility(View.VISIBLE);
                                b2.setText(util.dateToHM(d.getDate()));
                                b2.setTag(d.getTime_from());
                            }
                        }
                        k++;
                    }
                }

                if (cnt != null) {
                    //mSlidingTabLayout.addTab("Сегодня", "", true);
                    mSlidingTabLayout.addTab("Завтра", "", false);
                    pages.add(cnt);
                }

                mCalendar.setPages(pages);
            } else {
                mSlidingTabLayout.setVisibility(View.GONE);
                mNoTime.setVisibility(View.VISIBLE);
            }
        }
    }

    ButtonWithState old_b = null;

    private View.OnClickListener mOnTime = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selected_time = (String) v.getTag();
            if (old_b != null && v != old_b)
                old_b.setSelected(false);
            ((ButtonWithState) v).setSelected(!((ButtonWithState) v).isSelected());
            old_b = (ButtonWithState) v;

            String s = "Время мойки: " + old_b.getText().toString() + "\n";
            s = s + "\n";
            s = s + "Выбранные услуги:" + "\n";
            s = s + getTextServices();
            showDialog(R.string.rec_on_carwash, s, DIALOG_WASH1, DIALOG_WASH1_TAG);
        }
    };

    /*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.done_with_text, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            case R.id.action_done:

                //getActivity().getSupportFragmentManager().popBackStack();
                //reservation();
                Date d1 = util.getDate(first_time);
                if (d1 == null)
                    d1 = new Date();
                String s = "Время мойки: " + util.dateToHM(d1) + "\n";
                s = s + "\n";
                s = s + "Выбранные услуги:" + "\n";
                s = s + getTextServices();
                showDialog(R.string.rec_on_carwash, s, DIALOG_WASH2, DIALOG_WASH2_TAG);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void reservation(String time) {

        Profile prof = pref.getProfile();
        if (prof != null && prof.isPhone_verified()) {

            if (TextUtils.isEmpty(selected_time)) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.fragment_info_wash_service_no_tme_select), Toast.LENGTH_SHORT).show();
                return;
            }

            if (list == null || list.size() <= 0) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.fragment_info_wash_service_no_services_select), Toast.LENGTH_SHORT).show();
                return;
            } else {
                int s = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isCheck())
                        s++;
                }
                if (s == 0) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.fragment_info_wash_service_no_services_select), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            progressBar.setVisibility(View.VISIBLE);
            getSpiceManager().execute(new ReservationRequest(pref.getSessionID(), mService.getId(), pref.getCarModelId(), list, time), "reservation", DurationInMillis.ALWAYS_EXPIRED, new ReservationRequestListener());

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
                //Toast.makeText(getActivity(), result.getData().getResult(), Toast.LENGTH_SHORT).show();
                showToastError(result.getData().getResult());
            }
        }
    }

    public final class ChoiseServiceRequestListener implements RequestListener<ChoiseServiceResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            //Toast.makeText(getActivity(), "Ошибка получения данных", Toast.LENGTH_SHORT).show();
            mProgressBar2.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(final ChoiseServiceResult result) {
            Log.d("ChoiseServiceRequestListener", "onRequestSuccess = " + result.getStatus() == null ? "null" : result.getStatus());

            if (Constants.SUCCESS.equals(result.getStatus())) {

                Log.d("ChoiseServiceRequestListener", "onRequestSuccess fill data");

                if (list == null)
                    list = new ArrayList<>();
                list.clear();

                if (result.getData() != null) {
                    for (int i = 0; i < result.getData().size(); i++) {
                        ChoiseService b = result.getData().get(i).getClone();
                        if (mService != null && !TextUtils.isEmpty(mService.getService_name()) && mService.getService_name().toLowerCase().trim().equals(b.getName() == null ? "xcv" : b.getName().toLowerCase().trim())) {
                            b.setCheck(1);
                            Log.e("fghfgh", b.getName() == null ? "" : b.getName() + " " + b.getPrice());
                        }
                        list.add(b);
                    }
                }

                fillChoiseServises(list);
            } else {
            }

            mProgressBar2.setVisibility(View.GONE);
        }
    }

    private void fillChoiseServises(ArrayList<ChoiseService> in) {
        /*
        for (int i = 0; i < tableServicesContent.getChildCount(); i++) {
            View v = tableServicesContent.getChildAt(i);
            if (v.getTag() == null)
                tableServicesContent.removeViewAt(i);
        }*/

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
                    //c.setOnClickListener(mOnServicesChange);
                    c.setTag(i);

                    sum.setTypeface(mFont);
                    sum.setText(String.format("%d %s", list.get(i).getPrice(), getActivity().getString(R.string.rubleSymbolJava)));
                    tableServicesContent.addView(t);
                    price = price + list.get(i).getPrice();
                }
            }

            int discount = 0;

            txtPrice.setText(String.format("%d %s", price, getActivity().getString(R.string.rubleSymbolJava)));
            txtDiscountSrv.setText(String.format("%d %s", discount, getActivity().getString(R.string.rubleSymbolJava)));
            txtFullPrice.setText(String.format("%d %s", price, getActivity().getString(R.string.rubleSymbolJava)));
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
        }
    }

    @Override
    public void onRegisteredClient() {
        startTracking();
    }

    private void showDialog(int title, String message, int id, String tag) {
        AlertDialogFragment f = AlertDialogFragment.newInstance(title, message, id, this);
        f.show(getFragmentManager(), tag);
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
}
