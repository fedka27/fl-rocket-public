package wash.rocket.xor.rocketwash.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.adapters.FavoritesWashServicesAdapter;
import wash.rocket.xor.rocketwash.adapters.WashServicesAdapter;
import wash.rocket.xor.rocketwash.model.RemoveFavoriteResult;
import wash.rocket.xor.rocketwash.model.WashService;
import wash.rocket.xor.rocketwash.model.WashServiceResult;
import wash.rocket.xor.rocketwash.requests.FavoritesWashServiceRequest;
import wash.rocket.xor.rocketwash.requests.RemoveFavoriteRequest;
import wash.rocket.xor.rocketwash.util.Constants;
import wash.rocket.xor.rocketwash.widgets.BaseSwipeListViewListener;
import wash.rocket.xor.rocketwash.widgets.DividerItemDecoration;
import wash.rocket.xor.rocketwash.widgets.SlideInDownAnimator;
import wash.rocket.xor.rocketwash.widgets.SwipeListView;

/**
 * A placeholder fragment containing a simple view.
 */
@SuppressLint("LongLogTag")
public class FavoritesWashServicesFragment extends BaseFragment {

    public static final String TAG = "FavoritesWashServicesFragment";

    private static final String LIST = "LIST";
    private static final int DIALOG_REMOVE = 1;
    private static final String DIALOG_REMOVE_TAG = "DIALOG_REMOVE";

    private List<WashService> list;

    private double mLatitude;
    private double mLongitude;
    private int mDistance;
    private int mPage;

    private FavoritesWashServicesAdapter adapter;
    private SwipeListView swipeListView;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar toolbar;
    private ProgressBar progressBar;

    //private SpiceManager spiceManager = new SpiceManager(GSonRocketWashApiService.class);

    private LinearLayout layoutWarn;

    private int mPosition = -1;

    public FavoritesWashServicesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites_wash_services, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLatitude = 56.140901;
        mLongitude = 47.244521;
        mDistance = 20;
        mPage = 1;

        if (savedInstanceState == null)
            list = new ArrayList<>();
        else
            list = savedInstanceState.getParcelableArrayList(LIST);

        adapter = new FavoritesWashServicesAdapter(list);
        swipeListView = (SwipeListView) getView().findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

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
                WashService s = list.get(position);

                Log.d("lat", "" + s.getLatitude());
                Log.d("lon", "" + s.getLongitude());

                if (s.isActive()) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                            .add(R.id.container, WashServiceInfoFragment.newInstance(s.getId(), s.getLatitude(), s.getLongitude(), s.getName(), s), WashServiceInfoFragment.TAG)
                            .addToBackStack(TAG).commit();

                } else {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                            .add(R.id.container, WashServiceInfoFragmentCall.newInstance(s.getId(), s.getLatitude(), s.getLongitude(), s.getName(), s), WashServiceInfoFragmentCall.TAG)
                            .addToBackStack(TAG).commit();
                }
            }

            @Override
            public void onClickBackView(int position, View view) {

            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
            }
        });

        adapter.setOnSelectedItem(new WashServicesAdapter.IOnSelectedItem() {
            @Override
            public void onSelecredItem(WashService item, int position, int button) {
                Log.d("onSelecredItem", String.format("button %d", button));

                WashService s = list.get(position);

                switch (button) {
                    // call
                    case 1:
                        break;
                    // rec apply
                    case 2:
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                .add(R.id.container, WashServiceInfoFragmentQuick.newInstance(s.getId(), s.getLatitude(), s.getLongitude(), s.getName(), s), WashServiceInfoFragmentQuick.TAG)
                                .addToBackStack(TAG)
                                .commit();
                        break;

                    // hide
                    case 3:
                        break;
                    // more
                    case 4:

                        mPosition = position;
                        showDialogYesNo(R.string.fragment_nearest_wash_services_remove_to_vaf, "", DIALOG_REMOVE, DIALOG_REMOVE_TAG);

                        break;
                }
            }
        });

        adapter.setOnRequestNextPage(new WashServicesAdapter.IOnRequestNextPage() {
            @Override
            public void onRequestNextPage() {
                String session = pref.getSessionID();
                // mPage++;
                // getSpiceManager().execute(new NearestWashServiceRequest(mLatitude, mLongitude, mDistance, mPage, session), "wash", DurationInMillis.ALWAYS_EXPIRED, new NearestWashServiceRequestListener());
            }
        });

        if (savedInstanceState == null)
            getSpiceManager().execute(new FavoritesWashServiceRequest(pref.getSessionID(), mPage), "wash", DurationInMillis.ALWAYS_EXPIRED, new FavoritesWashServiceRequestListenner());

        toolbar = setToolbar(getView());

        layoutWarn = (LinearLayout) getView().findViewById(R.id.layoutWarn);
        layoutWarn.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(LIST, (ArrayList<? extends Parcelable>) list);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public final class FavoritesWashServiceRequestListenner implements RequestListener<WashServiceResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            showToastError("Ошибка получения данных");
            // progressBar.setVisibility(View.GONE);
            showError();
        }

        @Override
        public void onRequestSuccess(final WashServiceResult result) {
            // progressBar.setVisibility(View.GONE);
            layoutWarn.setVisibility(View.GONE);
            Log.d("onRequestSuccess", result.getStatus() == null ? "null" : result.getStatus());

            if (Constants.SUCCESS.equals(result.getStatus())) {
                list.clear();
                adapter.remove_by_type(WashServicesAdapter.TYPE_LOADER, true); //XXX

                if (result.getData() != null) {
                    for (int i = 0; i < result.getData().size(); i++) {

                        WashService item = result.getData().get(i).getClone();
                        Location loc = getLastLocation();

                        if (loc != null) {
                            float[] results = new float[1];
                            results[0] = 0;
                            Location.distanceBetween(loc.getLatitude(), loc.getLongitude(), item.getLatitude(), item.getLongitude(), results);
                            item.setDistance( results[0] / 1000);
                        }

                        list.add(item);
                    }

                    if (result.getData().size() >= 10) {
                        WashService loader = new WashService();
                        //loader.setType(WashServicesAdapter.TYPE_LOADER); //XXX
                        //list.add(loader);
                    }

                    if (list.size() <= 0)
                        showNoDataWarn();
                }
                adapter.notifyDataSetChanged();

                Log.d("onRequestSuccess", "fill data");
            } else {
                showToastError("Ошибка получения данных");
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // spiceManager.start(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        /*
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case DIALOG_REMOVE:
                    swipeListView.closeAnimate(mPosition);
                    getSpiceManager().execute(new RemoveFavoriteRequest(pref.getSessionID(), list.get(mPosition).getFavorite_id()), "remove", DurationInMillis.ALWAYS_EXPIRED, new RemoveFavoiteListener());
                    adapter.remove(mPosition);
                    break;
            }
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

    private void showDialogYesNo(int title, String message, int id, String tag) {
        AlertDialogFragment f = AlertDialogFragment.newInstance(title, message, id, this);
        f.show(getFragmentManager(), tag);
    }

    private class RemoveFavoiteListener implements RequestListener<RemoveFavoriteResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {

        }

        @Override
        public void onRequestSuccess(RemoveFavoriteResult profileResult) {

        }
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
        t.setText(R.string.empty_favorites);
        i.setImageResource(R.drawable.location_wash);
    }

    private void showError() {
        layoutWarn.setVisibility(View.VISIBLE);
        TextView t = (TextView) layoutWarn.findViewById(R.id.txtWarn);
        ImageView i = (ImageView) layoutWarn.findViewById(R.id.imgLogo);
        t.setText(R.string.network_error);
        i.setImageResource(R.drawable.location_error1);
    }
}
