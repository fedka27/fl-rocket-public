package wash.rocket.xor.rocketwash.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.adapters.MenuCursorAdapter;
import wash.rocket.xor.rocketwash.adapters.WashServicesAdapter;
import wash.rocket.xor.rocketwash.model.CarMake;
import wash.rocket.xor.rocketwash.model.CarsAttributes;
import wash.rocket.xor.rocketwash.model.CarsMakes;
import wash.rocket.xor.rocketwash.model.CarsMakesResult;
import wash.rocket.xor.rocketwash.model.Profile;
import wash.rocket.xor.rocketwash.model.ProfileResult;
import wash.rocket.xor.rocketwash.model.Reservation;
import wash.rocket.xor.rocketwash.model.ReservedResult;
import wash.rocket.xor.rocketwash.model.WashService;
import wash.rocket.xor.rocketwash.model.WashServiceResult;
import wash.rocket.xor.rocketwash.provider.NavigationMenuContent;
import wash.rocket.xor.rocketwash.requests.AddToFavoriteRequest;
import wash.rocket.xor.rocketwash.requests.CarsMakesRequest;
import wash.rocket.xor.rocketwash.requests.NearestWashServiceRequest;
import wash.rocket.xor.rocketwash.requests.ProfileRequest;
import wash.rocket.xor.rocketwash.requests.ReservedRequest;
import wash.rocket.xor.rocketwash.services.GSonRocketWashApiService;
import wash.rocket.xor.rocketwash.util.Constants;
import wash.rocket.xor.rocketwash.util.util;
import wash.rocket.xor.rocketwash.widgets.BaseSwipeListViewListener;
import wash.rocket.xor.rocketwash.widgets.SlideInDownAnimator;
import wash.rocket.xor.rocketwash.widgets.SwipeListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class NearestWashServicesFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "NearestWashServices";

    private static final int MENU_LOADER = 1;
    private static final String LIST = "LIST";
    private static final int FRAGMENT_PROFILE = 2;
    private static final String NEAREST_WASH_KEY_CASH = "nearest_wash";
    private static final int FRAGMENT_RESERVED = 3;
    private static final int FRAGMENT_QUICK = 4;

    private static final int DIALOG_FAVORITE = 5;
    private static final int DIALOG_HIDE = 6;
    private static final String DIALOG_HIDE_TAG = "DIALOG_HIDE";
    private static final String DIALOG_FAVORITE_TAG = "DIALOG_FAVORITE";

    private List<WashService> list;

    private double mLatitude;
    private double mLongitude;
    private int mDistance;
    private int mPage;

    private WashServicesAdapter adapter;
    private SwipeListView swipeListView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private MenuCursorAdapter mMenuAdapter;

    private TextView txtFIO;
    private TextView txtCar;
    private TextView txtCarNumber;
    private ProgressBar progressBar;

    private LinearLayout layoutError;
    private LinearLayout layoutWarnEmpty;
    private LinearLayout layoutWarnGPS;

    private SpiceManager spiceManager = new SpiceManager(GSonRocketWashApiService.class);
    private Reservation mReserved;
    private int mPosition;

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

        if (savedInstanceState == null)
            list = new ArrayList<>();
        else
            list = savedInstanceState.getParcelableArrayList(LIST);

        adapter = new WashServicesAdapter(list);
        swipeListView = (SwipeListView) getView().findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        swipeListView.addItemDecoration(new SpacesItemDecoration(1));
        swipeListView.setHasFixedSize(true);
        swipeListView.setLayoutManager(layoutManager);
        swipeListView.setItemAnimator(new SlideInDownAnimator());
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
                for (int position : reverseSortedPositions) {
                    // data.remove(position);
                }
                adapter.notifyDataSetChanged();
            }
        });

        adapter.setOnSelectedItem(new WashServicesAdapter.IOnSelectedItem() {
            @Override
            public void onSelecredItem(WashService item, int position, int button) {
                Log.d("onSelecredItem", String.format("button %d", button));

                WashService s = list.get(position);

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
                        f.setTargetFragment(NearestWashServicesFragment.this, FRAGMENT_RESERVED);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                .add(R.id.container, f, WashServiceInfoFragmentQuick.TAG)
                                .addToBackStack(TAG)
                                .commit();
                        mDrawerLayout.closeDrawers();
                        break;

                    // hide
                    case 3:
                        mPosition = position;
                        showDialogYesNo(R.string.fragment_nearest_wash_services_hide, "После обновления списка моек скрытые мойки вновь появятся.", DIALOG_HIDE, DIALOG_HIDE_TAG);
                        break;
                    // more
                    case 4:
                        mPosition = position;
                        showDialogYesNo(R.string.fragment_nearest_wash_services_add_to_vaf, "", DIALOG_FAVORITE, DIALOG_FAVORITE_TAG);
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

        /*
        swipeListView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Toast.makeText(SwipeListViewExampleActivity.this,""+position,Toast.LENGTH_LONG).show();
                    }
                })
        );*/

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String session = pref.getSessionID();
                mPage = 1;
                getSpiceManager().execute(new NearestWashServiceRequest(mLatitude, mLongitude, mDistance, mPage, session), "wash", DurationInMillis.ALWAYS_EXPIRED, new NearestWashServiceRequestListener());
                list.clear();
                layoutError.setVisibility(View.GONE);
                layoutWarnEmpty.setVisibility(View.GONE);
                layoutWarnGPS.setVisibility(View.GONE);
            }
        });

        // parcel/
        //String session = "kC2EJtXFUfYAab5WSzuc4bkUJAy38lgC6l84bFSmYyjRFmIjSDptLThreL0Q6mVg5rKQ/C2fDAIOanZ77buL77sEewWZYJOrsRc3Taivtt9NC8+h5o1GOjQhDJodEMjdUd/ePw==";
        /*
        String session = pref.getSessionID();
        if (savedInstanceState == null)
            getSpiceManager().execute(new NearestWashServiceRequest(mLatitude, mLongitude, mDistance, mPage, session), NEAREST_WASH_KEY_CASH, 3000, new NearestWashServiceRequestListener());
        else
            getSpiceManager().execute(new NearestWashServiceRequest(mLatitude, mLongitude, mDistance, mPage, session), NEAREST_WASH_KEY_CASH, 3000, new NearestWashServiceRequestListener());
            //getSpiceManager().addListenerIfPending(WashServiceResult.class, NEAREST_WASH_KEY_CASH, new NearestWashServiceRequestListener());
            */

        toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            initNavigationMenu();
        }

        layoutError = (LinearLayout) getView().findViewById(R.id.layoutError);
        layoutError.setVisibility(View.GONE);

        layoutWarnEmpty = (LinearLayout) getView().findViewById(R.id.layoutWarnEmpty);
        layoutWarnEmpty.setVisibility(View.GONE);

        layoutWarnGPS = (LinearLayout) getView().findViewById(R.id.layoutWarnGPS);
        layoutWarnGPS.setVisibility(View.GONE);

        if (mLatitude != 0 && mLongitude != 0) {
            getSpiceManager().execute(new NearestWashServiceRequest(mLatitude, mLongitude, mDistance, mPage, pref.getSessionID()), NEAREST_WASH_KEY_CASH, 3000, new NearestWashServiceRequestListener());
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
        } else {
            layoutWarnGPS.setVisibility(View.VISIBLE);
        }

        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.fragment_nearest_wash_services);
    }

    private void initNavigationMenu() {
        mDrawerLayout = (DrawerLayout) getView().findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer, R.string.drawer) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                Profile profile = pref.getProfile();

                if (profile != null) {
                    txtFIO.setText(profile.getName());
                    txtCar.setText(pref.getCarName());
                    txtCarNumber.setText("");
                } else {
                    txtFIO.setText("");
                    txtCar.setText("");
                    txtCarNumber.setText("");

                    getSpiceManager().execute(new CarsMakesRequest(""), "cars", DurationInMillis.ONE_HOUR, new CarsRequestListener());
                }
            }

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };

        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerViewNav);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        recyclerView.addItemDecoration(new SpacesItemDecoration(0));
        recyclerView.setHasFixedSize(true);
        mMenuAdapter = new MenuCursorAdapter(getActivity(), null);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        mMenuAdapter.setHasStableIds(true);
        mMenuAdapter.setOnSelectedItem(new MenuCursorAdapter.IOnSelectedItem() {
            @Override
            public void onSelectedItem(int position, int id) {

                switch (id) {
                    case 1:

                        ProfileFragment p = new ProfileFragment();
                        p.setTargetFragment(NearestWashServicesFragment.this, FRAGMENT_PROFILE);

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                .add(R.id.container, p, ProfileFragment.TAG)
                                .addToBackStack(TAG).commit();
                        mDrawerLayout.closeDrawers();
                        break;

                    case 2:

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                .add(R.id.container, new FavoritesWashServicesFragment(), FavoritesWashServicesFragment.TAG)
                                .addToBackStack(TAG).commit();
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

        Profile profile = pref.getProfile();

        if (profile != null) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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

    public final class NearestWashServiceRequestListener implements RequestListener<WashServiceResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Ошибка получения данных", Toast.LENGTH_SHORT).show();
            // progressBar.setVisibility(View.GONE);
            layoutError.setVisibility(View.VISIBLE);
            layoutWarnGPS.setVisibility(View.GONE);

            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

        }

        @Override
        public void onRequestSuccess(final WashServiceResult result) {
            // progressBar.setVisibility(View.GONE);
            //Toast.makeText(getActivity(), "login success", Toast.LENGTH_SHORT).show();

            layoutError.setVisibility(View.GONE);
            layoutWarnGPS.setVisibility(View.GONE);

            Log.d("onRequestSuccess", result.getStatus() == null ? "null" : result.getStatus());

            if (Constants.SUCCESS.equals(result.getStatus())) {
                // list.clear();

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

                if (mPage <= 1)
                    getSpiceManager().execute(new ReservedRequest(pref.getSessionID()), NEAREST_WASH_KEY_CASH, 3000, new ReservedRequestListener());
                else {
                    adapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }

                Log.d("onRequestSuccess", "fill data");
            } else {
                //XXX сбросить таймер ?
                Toast.makeText(getActivity(), "Ошибка получения данных", Toast.LENGTH_SHORT).show();

                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            if (parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }

        // stopTracking();
        //doStopLocationService();
    }

    /*
    @Override
    public SpiceManager getSpiceManager() {
        return spiceManager;
    }*/


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Profile profile = pref.getProfile();
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
                    swipeListView.closeAnimate(mPosition);
                    getSpiceManager().execute(new AddToFavoriteRequest(pref.getSessionID(), list.get(mPosition).getId()), NEAREST_WASH_KEY_CASH, 3000, new AddFavoriteRequestListener());
                    break;

                case DIALOG_HIDE:

                    swipeListView.closeAnimate(mPosition);
                    //swipeListView.
                    list.remove(mPosition);
                    adapter.notifyDataSetChanged();

                    break;
            }
        }
    }

    private List<CarsMakes> list_cars;

    public final class ProfileRequestListener implements RequestListener<ProfileResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Ошибка получения данных", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(final ProfileResult result) {
            Log.d("onRequestSuccess", result.getStatus() == null ? "null" : result.getStatus());
            if (Constants.SUCCESS.equals(result.getStatus())) {

                if (result.getData() != null) {
                    List<CarsAttributes> c = result.getData().getCars_attributes();

                    int i = pref.getUseCar();

                    if (i > c.size())
                        i = 0;

                    if (c != null) {
                        CarsAttributes r = c.get(i);

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

                        txtCar.setText(a + " " + b);
                        pref.setCarName(a + " " + b);
                    }

                    txtFIO.setText(result.getData().getName());

                    pref.setProfile(result.getData());
                    progressBar.setVisibility(View.GONE);
                    Log.d("onRequestSuccess", "fill data");
                } else {
                    // final int res = getResources().getIdentifier("login_" + result.getData().getResult(), "string", getActivity().getPackageName());
                    // String error = res == 0 ? result.getData().getResult() : getString(res);
                    // Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    //XXX сбросить таймер ?
                    Toast.makeText(getActivity(), "данные не отдались", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public final class CarsRequestListener implements RequestListener<CarsMakesResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), R.string.error_loading_data, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(final CarsMakesResult result) {
            //progressBar.setVisibility(View.GONE);
            if (result != null) {
                list_cars = result.getData();
                getSpiceManager().execute(new ProfileRequest(pref.getSessionID()), "wash", 30, new ProfileRequestListener());
            }
        }
    }

    public final class ReservedRequestListener implements RequestListener<ReservedResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), R.string.error_loading_data, Toast.LENGTH_SHORT).show();
            //progressBar.setVisibility(View.GONE);

            adapter.notifyDataSetChanged();

            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }

        @Override
        public void onRequestSuccess(final ReservedResult result) {
            //progressBar.setVisibility(View.GONE);
            if (result != null) {

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

            adapter.notifyDataSetChanged();

            if (list.size() <= 0)
                layoutWarnEmpty.setVisibility(View.VISIBLE);

            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");

        if (location != null) {

            Log.d(TAG, "onLocationChanged != null");
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();

            if (list.size() <= 0 && !mSwipeRefreshLayout.isRefreshing()) {
                String session = pref.getSessionID();
                mPage = 1;
                getSpiceManager().execute(new NearestWashServiceRequest(mLatitude, mLongitude, mDistance, mPage, session), "wash", 3000, new NearestWashServiceRequestListener());
                list.clear();
                layoutError.setVisibility(View.GONE);
                layoutWarnEmpty.setVisibility(View.GONE);
                layoutWarnGPS.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(true);
            }
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

    private class AddFavoriteRequestListener implements RequestListener<ProfileResult> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Не удалось добавить", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(ProfileResult profileResult) {
            Toast.makeText(getActivity(), "Добавлена успешно", Toast.LENGTH_SHORT).show();

            //ContentValues cv = new ContentValues();
            //cv.put(taxicallContent.orders.Columns.ORDER_DATE.getName(), time);
            //getActivity().getContentResolver().update( NavigationMenuContent.MENU_CONTENT_URI, cv, NavigationMenuContent.MENU_ID + "=?",
            //      new String[]{String.valueOf(2)});

        }
    }
}
