package wash.rocket.xor.rocketwash.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.adapters.ProfileRecyclerViewAdapter;
import wash.rocket.xor.rocketwash.model.CarsAttributes;
import wash.rocket.xor.rocketwash.model.Profile;
import wash.rocket.xor.rocketwash.model.ProfileResult;
import wash.rocket.xor.rocketwash.requests.ProfileSaveRequest;
import wash.rocket.xor.rocketwash.util.Constants;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileEditFragment extends BaseFragment {

    private static final int DIALOG_CAR_BRAND = 1;
    private static final int DIALOG_CAR_MODEL = 2;

    private static final String DIALOG_CAR_BRAND_TAG = "dialog.car.brand";
    private static final String DIALOG_CAR_MODEL_TAG = "dialog.car.model";

    private static final String PROFILE = "dialog.car.brand";

    private EditText edFIO;
    private TextView edPhone;
    //private CarsJsonRequest carsJsonRequest;

    private int mCarBrandId = 0;
    private int mCarMoldelId = 0;

    private DialoglistCarBrands dlgCarBrand;
    private DialoglistCarModels dlgModels;

    private RecyclerView recyclerView;
    private ProfileRecyclerViewAdapter adapter;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    private Profile mProfile;
    private ArrayList<CarsAttributes> list;
    private int mPosition;

    public ProfileEditFragment() {
    }

    public static ProfileEditFragment newInstance(Profile profile) {
        ProfileEditFragment fragment = new ProfileEditFragment();
        Bundle args = new Bundle();
        args.putParcelable(PROFILE, profile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        //carsJsonRequest  = new CarsJsonRequest("");
        mProfile = getArguments().getParcelable(PROFILE);

        list = new ArrayList<>();

        if (mProfile != null && mProfile.getCars_attributes() != null) {
            for (int i = 0; i < mProfile.getCars_attributes().size(); i++) {
                Log.w("onCreate", "????");
                CarsAttributes p = mProfile.getCars_attributes().get(i).copy();
                p.setType(0);
                list.add(p);
            }
            CarsAttributes c = new CarsAttributes();
            c.setType(1);
            list.add(c);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_edit, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() == null)
            return;

        edFIO = (EditText) getView().findViewById(R.id.edFIO);
        edPhone = (TextView) getView().findViewById(R.id.edPhone);

        edFIO.setText(mProfile.getName());
        edPhone.setText(mProfile.getPhone());

        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        adapter = new ProfileRecyclerViewAdapter(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.addItemDecoration(new SpacesItemDecoration(1));
        //recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        //recyclerView.setFocusableInTouchMode(true);
        //recyclerView.requestFocus();

        adapter.notifyDataSetChanged();

        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        adapter.setOnSelectedItem(new ProfileRecyclerViewAdapter.IOnSelectedItem() {
            @Override
            public void onSelecredItem(CarsAttributes item, int position, int type) {

                mPosition = position;

                switch (type) {
                    case 0:
                        adapter.remove_by_type(1, true);
                        CarsAttributes c = new CarsAttributes();
                        c.setType(0);
                        list.add(c);

                        c = new CarsAttributes();
                        c.setType(1);
                        list.add(c);
                        adapter.notifyDataSetChanged();
                        break;

                    case 1:
                        dlgCarBrand = DialoglistCarBrands.newInstance();
                        dlgCarBrand.setTargetFragment(ProfileEditFragment.this, DIALOG_CAR_BRAND);
                        dlgCarBrand.show(getFragmentManager(), DIALOG_CAR_BRAND_TAG);
                        break;

                    case 2:
                        int m = list.get(position).getCar_make_id();
                        if (m != 0) {
                            dlgModels = DialoglistCarModels.newInstance(m);
                            dlgModels.setTargetFragment(ProfileEditFragment.this, DIALOG_CAR_MODEL);
                            dlgModels.show(getFragmentManager(), DIALOG_CAR_MODEL_TAG);
                        } else
                            Toast.makeText(getActivity(), R.string.need_first_select_carbrand, Toast.LENGTH_SHORT).show();
                        break;
                    case 3:

                        if (list.get(position).getId() == 0)
                            list.remove(position);
                        else
                            list.get(position).setType(2);

                        adapter.notifyDataSetChanged();

                        break;
                }
            }
        });

        toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        AppCompatActivity a = (AppCompatActivity) getActivity();
        if (a != null) {
            a.setSupportActionBar(toolbar);
            if (a.getSupportActionBar() != null)
                a.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @TargetApi(3)
    private void hideKeyboard() {
        if (Build.VERSION.SDK_INT < 3) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edFIO.getWindowToken(), 0);
        // imm.hideSoftInputFromWindow(edPinCode.getWindowToken(), 0);
        RelativeLayout rl = (RelativeLayout) getView().findViewById(R.id.main);
        rl.requestFocus();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case DIALOG_CAR_BRAND:

                    list.get(mPosition).setBrandName(data.getStringExtra("name"));
                    list.get(mPosition).setCar_make_id(data.getIntExtra("id", 0));

                    //mCarBrandId = data.getIntExtra("id", 0);
                    //edBrandCar.setText(data.getStringExtra("name"));
                    //mCarMoldelId = 0;
                    //edModelCar.setText("");

                    adapter.notifyDataSetChanged();

                    break;
                case DIALOG_CAR_MODEL:
                    //mCarMoldelId = data.getIntExtra("id", 0);
                    //edModelCar.setText(data.getStringExtra("name"));
                    list.get(mPosition).setModelName(data.getStringExtra("name"));
                    list.get(mPosition).setCar_model_id(data.getIntExtra("id", 0));
                    adapter.notifyDataSetChanged();

                    break;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.done_with_text, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private boolean manual = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                manual = true;
                if (getTargetFragment() != null)
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);

                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            case R.id.action_done:

                hideKeyboard();
                String session = pref.getSessionID();
                mProfile.setCars_attributes(list);
                mProfile.setName(edFIO.getText().toString().trim());
                getSpiceManager().execute(new ProfileSaveRequest(session, mProfile), "save_profile", DurationInMillis.ALWAYS_EXPIRED, new SaveProfileRequestListener());
                progressBar.setVisibility(View.VISIBLE);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public final class SaveProfileRequestListener implements RequestListener<ProfileResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Ошибка сохранения данных", Toast.LENGTH_SHORT).show();
            // progressBar.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(final ProfileResult result) {
            progressBar.setVisibility(View.GONE);
            if (Constants.SUCCESS.equals(result.getStatus())) {
                Toast.makeText(getActivity(), "данные успешно сохраненны", Toast.LENGTH_SHORT).show();

                Log.d("dfsf", result.getData().getString());

                pref.setProfile(result.getData());

                if (getTargetFragment() != null)
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);

                getActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(getActivity(), "Ошибка при записи", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (!manual && getTargetFragment() != null)
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
    }
}
