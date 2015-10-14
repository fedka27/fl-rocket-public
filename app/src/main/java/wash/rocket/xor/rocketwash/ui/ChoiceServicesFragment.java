package wash.rocket.xor.rocketwash.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.adapters.ChoiceServicesRecyclerViewAdapter;
import wash.rocket.xor.rocketwash.model.ChoiseService;
import wash.rocket.xor.rocketwash.model.ChoiseServiceResult;
import wash.rocket.xor.rocketwash.requests.ChoiseServiceRequest;
import wash.rocket.xor.rocketwash.util.Constants;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChoiceServicesFragment extends BaseFragment {

    public static final String TAG = "NearestWashServices";
    private static final String LIST = "LIST";
    private static final String ID_SERVICE = "id_service";
    private static final String ID_CAR_MODEL = "id_car_model";

    private ArrayList<ChoiseService> list;
    private ArrayList<ChoiseService> list_in;

    protected RecyclerView recyclerView;
    protected ProgressBar progressBar;
    private ChoiceServicesRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar toolbar;

    private int mIdService;
    private int mIdCarModel;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (IFragmentCallbacksInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IFragmentCallbacksInterface");
        }
    }

    public ChoiceServicesFragment() {
    }

    public static ChoiceServicesFragment newInstance(int id_service, int id_car_model, ArrayList<ChoiseService> list) {
        ChoiceServicesFragment fragment = new ChoiceServicesFragment();
        Bundle args = new Bundle();
        args.putInt(ID_SERVICE, id_service);
        args.putInt(ID_CAR_MODEL, id_car_model);
        args.putParcelableArrayList(LIST, list);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_services_choise, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mIdService = getArguments().getInt(ID_SERVICE);
        mIdCarModel = getArguments().getInt(ID_CAR_MODEL);
        list_in = getArguments().getParcelableArrayList(LIST);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null)
            list = new ArrayList<>();
        else
            list = savedInstanceState.getParcelableArrayList(LIST);

        adapter = new ChoiceServicesRecyclerViewAdapter(list);

        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.addItemDecoration(new SpacesItemDecoration(0));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        recyclerView.setFocusableInTouchMode(true);
        recyclerView.requestFocus();

        // parcel/
        //String session = "kC2EJtXFUfYAab5WSzuc4bkUJAy38lgC6l84bFSmYyjRFmIjSDptLThreL0Q6mVg5rKQ/C2fDAIOanZ77buL77sEewWZYJOrsRc3Taivtt9NC8+h5o1GOjQhDJodEMjdUd/ePw==";
        String session = pref.getSessionID();
        if (savedInstanceState == null)
            getSpiceManager().execute(new ChoiseServiceRequest(mIdService, mIdCarModel, session), "wash", DurationInMillis.ALWAYS_EXPIRED, new ChoiseServiceRequestListener());

        toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(LIST, list);
    }

    public final class ChoiseServiceRequestListener implements RequestListener<ChoiseServiceResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Ошибка получения данных", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            // mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onRequestSuccess(final ChoiseServiceResult result) {
            progressBar.setVisibility(View.GONE);
            //Toast.makeText(getActivity(), "login success", Toast.LENGTH_SHORT).show();
            Log.d("onRequestSuccess", result.getStatus() == null ? "null" : result.getStatus());

            if (Constants.SUCCESS.equals(result.getStatus())) {
                list.clear();
                if (result.getData() != null) {
                    for (int i = 0; i < result.getData().size(); i++) {
                        ChoiseService b = result.getData().get(i).getClone();

                        if (list_in != null && list_in.size() > 0) {
                            for (int k = 0; k < list_in.size(); k++) {
                                ChoiseService c = list_in.get(k);
                                if (c.getId() == b.getId() && c.isCheck())
                                    b.setCheck(1);
                            }
                        }
                        list.add(b);
                    }
                }
                adapter.notifyDataSetChanged();
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
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.done_with_text, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private boolean manual = false;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            case R.id.action_done:
                manual = true;
                Intent intent = new Intent();
                intent.putExtra("list", list);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (!manual)
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
    }
}
