package wash.rocket.xor.rocketwash.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.adapters.WashServicesAdapter;
import wash.rocket.xor.rocketwash.model.WashService;
import wash.rocket.xor.rocketwash.model.WashServiceResult;
import wash.rocket.xor.rocketwash.requests.FavoritesWashServiceRequest;
import wash.rocket.xor.rocketwash.services.GSonRocketWashApiService;
import wash.rocket.xor.rocketwash.util.Constants;
import wash.rocket.xor.rocketwash.widgets.BaseSwipeListViewListener;
import wash.rocket.xor.rocketwash.widgets.SwipeListView;

/**
 * A placeholder fragment containing a simple view.
 */
@SuppressLint("LongLogTag")
public class FavoritesWashServicesFragment extends BaseFragment {

    public static final String TAG = "FavoritesWashServicesFragment";

    private static final String LIST = "LIST";

    private List<WashService> list;

    private double mLatitude;
    private double mLongitude;
    private int mDistance;
    private int mPage;

    private WashServicesAdapter adapter;
    private SwipeListView swipeListView;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private LinearLayout layoutError;
    private LinearLayout layoutEmpty;

    private SpiceManager spiceManager = new SpiceManager(GSonRocketWashApiService.class);

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

        adapter = new WashServicesAdapter(list);
        swipeListView = (SwipeListView) getView().findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        swipeListView.addItemDecoration(new SpacesItemDecoration(1));
        swipeListView.setHasFixedSize(true);
        swipeListView.setLayoutManager(layoutManager);
        swipeListView.setItemAnimator(itemAnimator);
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

        // parcel/
        //String session = "kC2EJtXFUfYAab5WSzuc4bkUJAy38lgC6l84bFSmYyjRFmIjSDptLThreL0Q6mVg5rKQ/C2fDAIOanZ77buL77sEewWZYJOrsRc3Taivtt9NC8+h5o1GOjQhDJodEMjdUd/ePw==";
        String session = pref.getSessionID();
        if (savedInstanceState == null)
            getSpiceManager().execute(new FavoritesWashServiceRequest(session, mPage), "wash", DurationInMillis.ALWAYS_EXPIRED, new FavoritesWashServiceRequestListenner());

        toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        layoutError = (LinearLayout) getView().findViewById(R.id.layoutError);
        layoutEmpty = (LinearLayout) getView().findViewById(R.id.layoutEmpty);

        layoutError.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.fragment_nearest_wash_services);
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
            Toast.makeText(getActivity(), "Ошибка получения данных", Toast.LENGTH_SHORT).show();
            // progressBar.setVisibility(View.GONE);
            layoutError.setVisibility(View.VISIBLE);
        }

        @Override
        public void onRequestSuccess(final WashServiceResult result) {
            // progressBar.setVisibility(View.GONE);
            //Toast.makeText(getActivity(), "login success", Toast.LENGTH_SHORT).show();
            layoutError.setVisibility(View.GONE);
            Log.d("onRequestSuccess", result.getStatus() == null ? "null" : result.getStatus());

            if (Constants.SUCCESS.equals(result.getStatus())) {
                list.clear();
                adapter.remove_by_type(WashServicesAdapter.TYPE_LOADER, true); //XXX

                if (result.getData() != null) {
                    for (int i = 0; i < result.getData().size(); i++) {
                        list.add(result.getData().get(i).getClone());
                    }

                    if (result.getData().size() >= 10) {
                        WashService loader = new WashService();
                        //loader.setType(WashServicesAdapter.TYPE_LOADER); //XXX
                        //list.add(loader);
                    }

                    if (list.size() <= 0)
                        layoutEmpty.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();

                Log.d("onRequestSuccess", "fill data");
            } else {
                //XXX сбросить таймер ?
                Toast.makeText(getActivity(), "Ошибка получения данных", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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


}
