package wash.rocket.xor.rocketwash.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.adapters.MenuCursorAdapter;
import wash.rocket.xor.rocketwash.adapters.WashServicesAdapter;
import wash.rocket.xor.rocketwash.model.Profile;
import wash.rocket.xor.rocketwash.model.ProfileResult;
import wash.rocket.xor.rocketwash.model.Reservation;
import wash.rocket.xor.rocketwash.model.ReserveCancelResult;
import wash.rocket.xor.rocketwash.model.ReservedResult;
import wash.rocket.xor.rocketwash.model.WashService;
import wash.rocket.xor.rocketwash.model.WashServiceResult;
import wash.rocket.xor.rocketwash.provider.NavigationMenuContent;
import wash.rocket.xor.rocketwash.requests.AddToFavoriteRequest;
import wash.rocket.xor.rocketwash.requests.NearestWashServiceRequest;
import wash.rocket.xor.rocketwash.requests.ReserveCancelRequest;
import wash.rocket.xor.rocketwash.requests.ReservedRequest;
import wash.rocket.xor.rocketwash.util.Constants;
import wash.rocket.xor.rocketwash.util.util;
import wash.rocket.xor.rocketwash.widgets.BaseSwipeListViewListener;
import wash.rocket.xor.rocketwash.widgets.DividerItemDecoration;
import wash.rocket.xor.rocketwash.widgets.SlideInDownAnimator;
import wash.rocket.xor.rocketwash.widgets.SwipeListView;

@SuppressLint("LongLogTag")
public class NearestWashServicesFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "NearestWashServices";

    private static final int MENU_LOADER = 1;
    private static final String LIST = "LIST";
    private static final int FRAGMENT_PROFILE = 2;
    private static final String NEAREST_WASH_KEY_CASH = "nearest_wash";
    private static final String RESERVED_WASH_KEY_CASH = "reserved_wash";

    public static final int FRAGMENT_RESERVED = 3;
    private static final int FRAGMENT_QUICK = 4;

    private static final int DIALOG_FAVORITE = 5;
    private static final int DIALOG_HIDE = 6;
    private static final int DIALOG_CANCEL_RESERVED = 7;
    private static final String DIALOG_HIDE_TAG = "DIALOG_HIDE";
    private static final String DIALOG_FAVORITE_TAG = "DIALOG_FAVORITE";
    private static final String DIALOG_CANCEL_RESERVED_TAG = "DIALOG_CANCEL_RESERVED";

    private List<WashService> list;

    private double mLatitude;
    private double mLongitude;
    private int mDistance;
    private int mPage;

    private WashServicesAdapter adapter;
    private SwipeListView swipeListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private MenuCursorAdapter mMenuAdapter;

    private TextView txtFIO;
    private TextView txtCar;
    private TextView txtCarNumber;
    private ProgressBar progressBar;

    private LinearLayout layoutWarn;

    // private SpiceManager spiceManager = new SpiceManager(RobospiceService.class);
    private Reservation mReserved;
    private int mPosition;

    private int mLoaderCount = 0;

    public NearestWashServicesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_neares_wash_services, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        // doStartLocationService();

        Location l = getLastLocation();
        if (l != null) {
            mLatitude = l.getLatitude();
            mLongitude = l.getLongitude();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mLatitude = 56.140901;
        //mLongitude = 47.244521;

        mDistance = 20;
        mPage = 1;

        if (getView() == null)
            return;

        if (savedInstanceState == null)
            list = new ArrayList<>();
        else
            list = savedInstanceState.getParcelableArrayList(LIST);

        adapter = new WashServicesAdapter(list);
        swipeListView = (SwipeListView) getView().findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        swipeListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        swipeListView.setHasFixedSize(true);
        swipeListView.setLayoutManager(layoutManager);
        swipeListView.setItemAnimator(new SlideInDownAnimator());
        swipeListView.getItemAnimator().setRemoveDuration(500);

        swipeListView.setAdapter(adapter);

        swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("swipe", String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position) {
                Log.d("swipe", String.format("onClickFrontView %d", position));

                mDrawerLayout.closeDrawers();
                swipeListView.closeAnimateAll();

                if (position > list.size() - 1)
                    return;

                WashService s = list.get(position);

                if (s.getType() == WashServicesAdapter.TYPE_GROUP)
                    return;

                if (s.getType() == WashServicesAdapter.TYPE_RESERVED) {

                    WashServiceInfoFragmentReserved f = WashServiceInfoFragmentReserved.newInstance(0, mReserved.getCarwash().getLatitude(), mReserved.getCarwash().getLongitude(), s.getName(), null, mReserved);
                    f.setTargetFragment(NearestWashServicesFragment.this, FRAGMENT_RESERVED);

                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                            .add(R.id.container, f, WashServiceInfoFragmentReserved.TAG)
                            .addToBackStack(TAG).commit();

                    mDrawerLayout.closeDrawers();
                    return;
                }

                Log.d("lat", "" + s.getLatitude());
                Log.d("lon", "" + s.getLongitude());

                if (s.isActive()) {

                    WashServiceInfoFragment f = WashServiceInfoFragment.newInstance(s.getId(), s.getLatitude(), s.getLongitude(), s.getName(), s);
                    f.setTargetFragment(NearestWashServicesFragment.this, FRAGMENT_RESERVED);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                            .add(R.id.container, f, WashServiceInfoFragment.TAG)
                            .addToBackStack(TAG).commit();
                    mDrawerLayout.closeDrawers();

                } else {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                            .add(R.id.container, WashServiceInfoFragmentCall.newInstance(s.getId(), s.getLatitude(), s.getLongitude(), s.getName(), s), WashServiceInfoFragmentCall.TAG)
                            .addToBackStack(TAG).commit();
                    mDrawerLayout.closeDrawers();
                }
            }

            @Override
            public void onClickBackView(int position, View view) {
                Log.d("swipe !!!", String.format("onClickBackView %d", position));
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                adapter.notifyDataSetChanged();
            }
        });

        adapter.setOnSelectedItem(new WashServicesAdapter.IOnSelectedItem() {
            @Override
            public void onSelectedItem(WashService item, int position, int button) {
                Log.d("onSelectedItem", String.format("button %d", button));

                WashService s = list.get(position);

                mDrawerLayout.closeDrawers();
                swipeListView.closeAnimateAll();

                if (s.getType() == WashServicesAdapter.TYPE_GROUP)
                    return;

                switch (button) {
                    // call
                    case 1:
                        call(s.getPhone());
                        break;
                    // rec apply
                    case 2:
                        WashServiceInfoFragmentQuick f = WashServiceInfoFragmentQuick.newInstance(s.getId(), s.getLatitude(), s.getLongitude(), s.getName(), s);
                        f.setTargetFragment(NearestWashServicesFragment.this, FRAGMENT_QUICK);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                .add(R.id.container, f, WashServiceInfoFragmentQuick.TAG)
                                .addToBackStack(TAG)
                                .commit();
                        break;

                    // hide
                    case 3:
                        mPosition = position;
                        showDialogYesNo(R.string.fragment_nearest_wash_services_hide, getActivity().getString(R.string.after_refresh_washes), DIALOG_HIDE, DIALOG_HIDE_TAG);
                        break;
                    // more
                    case 4:
                        mPosition = position;
                        showDialogYesNo(R.string.fragment_nearest_wash_services_add_to_vaf, "", DIALOG_FAVORITE, DIALOG_FAVORITE_TAG);
                        break;

                    case 5:
                        showDialogYesNo(R.string.fragment_nearest_wash_services_cancel_reserved, "", DIALOG_CANCEL_RESERVED, DIALOG_CANCEL_RESERVED_TAG);
                        break;
                }
            }
        });

        adapter.setOnRequestNextPage(new WashServicesAdapter.IOnRequestNextPage() {
            @Override
            public void onRequestNextPage() {
                String session = pref.getSessionID();
                mPage++;
                getSpiceManager().execute(new NearestWashServiceRequest(mLatitude, mLongitude, mDistance, mPage, session), "wash", DurationInMillis.ALWAYS_EXPIRED, new NearestWashServiceRequestListener());
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLoaderCount = 0;
                swipeListView.closeAnimateAll();
                String session = pref.getSessionID();
                mPage = 1;
                nearest_Loaded = false;
                reserved_Loaded = false;
                getSpiceManager().execute(new ReservedRequest(pref.getSessionID()), RESERVED_WASH_KEY_CASH, 3000, new ReservedRequestListener());
                getSpiceManager().execute(new NearestWashServiceRequest(mLatitude, mLongitude, mDistance, mPage, session), NEAREST_WASH_KEY_CASH, DurationInMillis.ALWAYS_EXPIRED, new NearestWashServiceRequestListener());
                list.clear();
                layoutWarn.setVisibility(View.GONE);
            }
        });

        toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        if (toolbar != null) {
            AppCompatActivity a = (AppCompatActivity) getActivity();
            if (a != null) {
                a.setSupportActionBar(toolbar);
                if (a.getSupportActionBar() != null) {
                    a.getSupportActionBar().setHomeButtonEnabled(true);
                    a.getSupportActionBar().setDisplayShowHomeEnabled(true);
                }
            }
            initNavigationMenu();
        }

        layoutWarn = (LinearLayout) getView().findViewById(R.id.layoutWarn);
        layoutWarn.setVisibility(View.GONE);

        if (mLatitude != 0 && mLongitude != 0) {

            if (list.size() == 0) {

                Log.w(TAG, "first_load");

                getSpiceManager().execute(new ReservedRequest(pref.getSessionID()), NEAREST_WASH_KEY_CASH, 3000, new ReservedRequestListener());
                getSpiceManager().execute(new NearestWashServiceRequest(mLatitude, mLongitude, mDistance, mPage, pref.getSessionID()), NEAREST_WASH_KEY_CASH, 3000, new NearestWashServiceRequestListener());

                //spiceManager.execute(new ReservedRequest(pref.getSessionID()), NEAREST_WASH_KEY_CASH, 3000, new ReservedRequestListener());
                //spiceManager.execute(new NearestWashServiceRequest(mLatitude, mLongitude, mDistance, mPage, pref.getSessionID()), NEAREST_WASH_KEY_CASH, 3000, new NearestWashServiceRequestListener());

                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
            }
        } else {
            showShowGPSWarn();
        }

        restoreTargets();
    }

    private void initNavigationMenu() {

        if (getView() == null)
            return;

        mDrawerLayout = (DrawerLayout) getView().findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer, R.string.drawer) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                swipeListView.closeAnimateAll();

                Profile profile = getApp().getProfile();

                if (profile != null) {
                    txtFIO.setText(profile.getName());
                    txtCar.setText(pref.getCarName());
                    txtCarNumber.setText(String.format("%s %s", getActivity().getString(R.string.car_number), pref.getCarNum()));
                } else {
                    txtFIO.setText("");
                    txtCar.setText("");
                    txtCarNumber.setText("");
                }
            }

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                swipeListView.closeAnimateAll();
            }
        };

        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerViewNav);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);
        mMenuAdapter = new MenuCursorAdapter(getActivity(), null);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        mMenuAdapter.setHasStableIds(true);
        mMenuAdapter.setOnSelectedItem(new MenuCursorAdapter.IOnSelectedItem() {
            @Override
            public void onSelectedItem(int position, int id) {

                mDrawerLayout.closeDrawers();
                swipeListView.closeAnimateAll();

                switch (id) {
                    case 1:
                        ProfileFragment p = new ProfileFragment();
                        p.setTargetFragment(NearestWashServicesFragment.this, FRAGMENT_PROFILE);
                        getFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                .add(R.id.container, p, ProfileFragment.TAG)
                                .addToBackStack(TAG)
                                .commit();
                        mDrawerLayout.closeDrawers();
                        break;

                    case 2:

                        FavoritesWashServicesFragment f = new FavoritesWashServicesFragment();
                        f.setTargetFragment(NearestWashServicesFragment.this, FRAGMENT_RESERVED);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                .add(R.id.container, f, FavoritesWashServicesFragment.TAG)
                                .addToBackStack(TAG)
                                .commit();
                        mDrawerLayout.closeDrawers();
                        break;

                    case 8:
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                .add(R.id.container, new InformationFragment(), InformationFragment.TAG)
                                .addToBackStack(TAG).commit();

                        mDrawerLayout.closeDrawers();

                        break;

                    case 6:
                        //mDrawerLayout.closeDrawers();
                        share();
                        break;

                    case 9:
                        pref.setSessionID("");
                        pref.setProfile(null);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                .replace(R.id.container, new LoginFragment(), LoginFragment.TAG)
                                .commit();
                        mDrawerLayout.closeDrawers();
                        break;
                }
            }
        });

        recyclerView.setAdapter(mMenuAdapter);

        txtFIO = (TextView) getView().findViewById(R.id.txtFIO);
        txtCar = (TextView) getView().findViewById(R.id.txtCar);
        txtCarNumber = (TextView) getView().findViewById(R.id.txtCarNumber);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);

        Profile profile = getApp().getProfile();

        if (profile != null) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        setTargetFragment(null, -1);
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(LIST, (ArrayList<? extends Parcelable>) list);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSupportLoaderManager().initLoader(MENU_LOADER, null, this);
        doBindLocationService();
    }

    @Override
    public void onPause() {
        super.onPause();
        Loader<Object> l = getActivity().getSupportLoaderManager().getLoader(MENU_LOADER);
        if (l != null)
            l.cancelLoad();
        getActivity().getSupportLoaderManager().destroyLoader(MENU_LOADER);

        // stopTracking();
        doUnbindLocationService();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MENU_LOADER:
                Log.d(TAG, "onCreateLoader");
                return new CursorLoader(getActivity(), NavigationMenuContent.MENU_CONTENT_URI, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMenuAdapter.swapCursor(data);
        Log.d(TAG, "onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMenuAdapter.swapCursor(null);
        Log.d(TAG, "onLoaderReset");
    }


    @Override
    public void onStart() {
        super.onStart();
        //spiceManager.start(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        // if (spiceManager.isStarted()) {
        //     spiceManager.shouldStop();
        // }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Profile profile = getApp().getProfile();
        if (profile != null) {
            txtFIO.setText(profile.getName());
            txtCar.setText("");
            txtCarNumber.setText("");
        } else {
            txtFIO.setText("");
            txtCar.setText("");
            txtCarNumber.setText("");
        }

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case FRAGMENT_QUICK:
                case FRAGMENT_RESERVED:

                    if (data != null) {
                        adapter.remove_by_type(WashServicesAdapter.TYPE_RESERVED, true);
                        adapter.remove_by_type(WashServicesAdapter.TYPE_GROUP, true);
                        adapter.notifyDataSetChanged();
                    } else {
                        if (list.size() > 0 && list.get(0).getType() != WashServicesAdapter.TYPE_GROUP) {
                            mSwipeRefreshLayout.setRefreshing(true);
                            getSpiceManager().execute(new ReservedRequest(pref.getSessionID()), NEAREST_WASH_KEY_CASH, 3000, new ReservedRequestListener());
                        }
                    }
                    break;

                case DIALOG_FAVORITE:
                    swipeListView.closeAnimateAll();
                    getSpiceManager().execute(new AddToFavoriteRequest(pref.getSessionID(), list.get(mPosition).getId()), NEAREST_WASH_KEY_CASH, 3000, new AddFavoriteRequestListener());
                    break;

                case DIALOG_HIDE:
                    swipeListView.closeAnimateAll();
                    adapter.remove(mPosition);
                    break;

                case DIALOG_CANCEL_RESERVED:
                    swipeListView.closeAnimateAll();
                    mSwipeRefreshLayout.setRefreshing(true);
                    getSpiceManager().execute(new ReserveCancelRequest(pref.getSessionID(), mReserved.getId()), "cancel", DurationInMillis.ALWAYS_EXPIRED, new CancelRequestListener());
                    break;
            }
        }
    }

    private long last_time = 0;

    @Override
    public void onLocationChanged(Location location) {
        //Log.d(TAG, "onLocationChanged");

        if (location != null) {
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            // mLatitude = 56.138654;
            // mLongitude = 47.239894;

            if (list.size() <= 0 && !mSwipeRefreshLayout.isRefreshing() && last_time == 0) {
                String session = pref.getSessionID();
                mPage = 1;
                getSpiceManager().execute(new ReservedRequest(pref.getSessionID()), NEAREST_WASH_KEY_CASH, 3000, new ReservedRequestListener());
                getSpiceManager().execute(new NearestWashServiceRequest(mLatitude, mLongitude, mDistance, mPage, session), "wash", 3000, new NearestWashServiceRequestListener());
                list.clear();
                layoutWarn.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(true);

                last_time = System.currentTimeMillis();
            }

            // refresh per 30 sec
            if (last_time > 0)
                if (System.currentTimeMillis() - last_time > 30000)
                    last_time = 0;

        }
    }

    @Override
    public void onRegisteredClient() {
        Log.d(TAG, "onRegisteredClient");
        startTracking();
    }

    private void showDialogYesNo(int title, String message, int id, String tag) {
        AlertDialogFragment f = AlertDialogFragment.newInstance(title, message, id, this);
        f.show(getFragmentManager(), tag);
    }

    private void showShowGPSWarn() {
        layoutWarn.setVisibility(View.VISIBLE);
        TextView t = (TextView) layoutWarn.findViewById(R.id.txtWarn);
        ImageView i = (ImageView) layoutWarn.findViewById(R.id.imgLogo);
        t.setText(R.string.gps_warn);
        i.setImageResource(R.drawable.location_wash);
    }

    private void showNoDataWarn() {
        layoutWarn.setVisibility(View.VISIBLE);
        TextView t = (TextView) layoutWarn.findViewById(R.id.txtWarn);
        ImageView i = (ImageView) layoutWarn.findViewById(R.id.imgLogo);
        t.setText(R.string.empty_ex);
        i.setImageResource(R.drawable.location_error1);
    }

    private void showError() {
        layoutWarn.setVisibility(View.VISIBLE);
        TextView t = (TextView) layoutWarn.findViewById(R.id.txtWarn);
        ImageView i = (ImageView) layoutWarn.findViewById(R.id.imgLogo);
        t.setText(R.string.network_error);
        i.setImageResource(R.drawable.location_error1);
    }

    private void stopRefrash() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                swipeListView.closeAnimateAll();
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
                showToastOk("Запись отменена");

                adapter.remove_by_type(WashServicesAdapter.TYPE_RESERVED, true);
                adapter.remove_by_type(WashServicesAdapter.TYPE_GROUP, true);
                adapter.notifyDataSetChanged();

                stopRefrash();

            } else
                showToastOk("Не удалось отменить запись");
            progressBar.setVisibility(View.GONE);
        }
    }

    private class AddFavoriteRequestListener implements RequestListener<ProfileResult> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            showToastError(R.string.favorite_add_fail);
        }

        @Override
        public void onRequestSuccess(ProfileResult profileResult) {
            showToastOk(R.string.favorite_add_success);
        }
    }

    private boolean nearest_Loaded = false;
    private boolean reserved_Loaded = false;

    public final class NearestWashServiceRequestListener implements RequestListener<WashServiceResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            showError();
            stopRefrash();
            nearest_Loaded = true;
        }

        @Override
        public void onRequestSuccess(final WashServiceResult result) {
            layoutWarn.setVisibility(View.GONE);
            Log.d("NearestWashServiceRequestListener", "res");

            nearest_Loaded = true;
            if (mPage <= 1 && !reserved_Loaded) {
                list.clear();
            }

            if (Constants.SUCCESS.equals(result.getStatus())) {
                adapter.remove_by_type(WashServicesAdapter.TYPE_LOADER, true); //XXX

                if (result.getData() != null) {
                    for (int i = 0; i < result.getData().size(); i++) {
                        list.add(result.getData().get(i).getClone());
                    }

                    if (result.getData().size() >= 10) {
                        WashService loader = new WashService();
                        loader.setType(WashServicesAdapter.TYPE_LOADER); //XXX
                        list.add(loader);
                    }
                }

                if (mPage <= 1) {
                    if (reserved_Loaded) {
                        adapter.notifyDataSetChanged();
                        stopRefrash();
                    }

                    if (reserved_Loaded && list.size() <= 0)
                        showNoDataWarn();

                } else {
                    adapter.notifyDataSetChanged();
                    stopRefrash();
                }

                Log.d("NearestWashServiceRequestListener", "fill data");


            } else {
                showError();
                stopRefrash();
            }

        }
    }

    public final class ReservedRequestListener implements RequestListener<ReservedResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            adapter.notifyDataSetChanged();
            stopRefrash();
            reserved_Loaded = true;
        }

        @Override
        public void onRequestSuccess(final ReservedResult result) {

            reserved_Loaded = true;

            if (result != null) {

                if (mPage <= 1 && !nearest_Loaded) {
                    list.clear();
                }

                if (result.getData() != null && result.getData().size() > 0) {
                    WashService w = new WashService();
                    w.setType(WashServicesAdapter.TYPE_GROUP);
                    w.setName(getActivity().getString(R.string.group_name_reserved));
                    list.add(0, w);

                    Reservation cw = result.getData().get(0);
                    mReserved = cw;

                    w = new WashService();
                    w.setType(WashServicesAdapter.TYPE_RESERVED);
                    w.setName(cw.getCarwash().getName());
                    w.setAddress(cw.getCarwash().getAddress());
                    w.setrDate(util.getDate(cw.getTime_from()));
                    list.add(1, w);

                    w = new WashService();
                    w.setType(WashServicesAdapter.TYPE_GROUP);
                    w.setName(getActivity().getString(R.string.group_name_near));
                    list.add(2, w);
                }
            }

            if (nearest_Loaded) {
                adapter.notifyDataSetChanged();
                stopRefrash();
            }

            if (nearest_Loaded && list.size() <= 0)
                showNoDataWarn();

            Log.d("ReservedRequestListener", "fill data");
        }
    }



    @Override
    public void restoreTargets()
    {
        Log.d(TAG, "restoreTargets");

        Fragment f;
        f = getFragmentManager().findFragmentByTag(WashServiceInfoFragment.TAG);
        if (f != null)
            f.setTargetFragment(NearestWashServicesFragment.this, FRAGMENT_RESERVED);

        f = getFragmentManager().findFragmentByTag(ProfileFragment.TAG);
        if (f != null)
            f.setTargetFragment(NearestWashServicesFragment.this, FRAGMENT_PROFILE);


        f = getFragmentManager().findFragmentByTag(WashServiceInfoFragmentReserved.TAG);
        if (f != null)
            f.setTargetFragment(NearestWashServicesFragment.this, FRAGMENT_PROFILE);


        f = getFragmentManager().findFragmentByTag(WashServiceInfoFragmentQuick.TAG);
        if (f != null)
            f.setTargetFragment(NearestWashServicesFragment.this, FRAGMENT_QUICK);
    }
}
