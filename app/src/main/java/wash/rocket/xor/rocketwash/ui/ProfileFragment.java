package wash.rocket.xor.rocketwash.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.CarMake;
import wash.rocket.xor.rocketwash.model.CarsAttributes;
import wash.rocket.xor.rocketwash.model.CarsMakes;
import wash.rocket.xor.rocketwash.model.CarsMakesResult;
import wash.rocket.xor.rocketwash.model.Profile;
import wash.rocket.xor.rocketwash.model.ProfileResult;
import wash.rocket.xor.rocketwash.requests.CarsMakesRequest;
import wash.rocket.xor.rocketwash.requests.ProfileRequest;
import wash.rocket.xor.rocketwash.util.Constants;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends BaseFragment {

    public static final String TAG = "ProfileFragment";

    private static final int DIALOG_CAR_BRAND = 1;
    private static final int DIALOG_CAR_MODEL = 2;

    private TextView txtFullName;
    private TextView txtPhone;
    private TextView txtPromo;
    private Button btnShare;

    private DialoglistCarBrands dlgCarBrand;
    private DialoglistCarModels dlgModels;

    protected ProgressBar progressBar;
    protected Toolbar toolbar;
    private RadioGroup radioGroupCars;
    private RelativeLayout actionChange;

    private LayoutInflater mInflater;
    private List<CarsMakes> list_cars;
    private Profile mProfile;


    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getView() == null)
            return;

        setToolbar(getView());

        //toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        //((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        radioGroupCars = (RadioGroup) getView().findViewById(R.id.radioGroupCars);
        radioGroupCars.removeAllViews();

        radioGroupCars.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);

                int index = radioGroup.indexOfChild(radioButton);

                CarsAttributes r = (CarsAttributes) radioButton.getTag();

                pref.setCarName(r.getBrandName() + " " + r.getModelName());
                pref.setUseCar(index);
                pref.setCarModelId(r.getCar_model_id());
                pref.setCarNum(r.getTag());
                //Log.d("dfgdfg", "" + index);
            }
        });

        actionChange = (RelativeLayout) getView().findViewById(R.id.actionChange);

        actionChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .replace(R.id.container, ProfileEditFragment.newInstance(mProfile, false), "profileedit")
                        .addToBackStack("profile").commit();
            }
        });

        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        txtFullName = (TextView) getView().findViewById(R.id.txtFullName);
        txtPhone = (TextView) getView().findViewById(R.id.txtPhone);
        txtPromo = (TextView) getView().findViewById(R.id.txtPromo);
        btnShare = (Button) getView().findViewById(R.id.btnShare);

        getSpiceManager().execute(new CarsMakesRequest(""), "cars", DurationInMillis.ONE_HOUR, new CarsRequestListener());
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
        // imm.hideSoftInputFromWindow(edFIO.getWindowToken(), 0);
        // imm.hideSoftInputFromWindow(edPinCode.getWindowToken(), 0);
        RelativeLayout rl = (RelativeLayout) getView().findViewById(R.id.main);
        rl.requestFocus();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.change_with_text, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            case R.id.action_change:

                //String session = pref.getSessionID();
                //getSpiceManager().execute(new ProfileSaveRequest(session, mProfile), "wash", DurationInMillis.ALWAYS_EXPIRED, new SaveProfileRequestListener());

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int res = 0;
    public final class ProfileRequestListener implements RequestListener<ProfileResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            showToastError(R.string.error_loading_profile_data);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(final ProfileResult result) {
            Log.d("onRequestSuccess", result.getStatus() == null ? "null" : result.getStatus());
            if (Constants.SUCCESS.equals(result.getStatus())) {

                res = res + 1000;

                radioGroupCars.removeAllViews();

                if (result.getData() != null) {

                    List<CarsAttributes> c = result.getData().getCars_attributes();

                    int selected = pref.getUseCar();
                    if (selected > (c.size() - 1))
                        selected = 0;

                    for (int i = 0; i < c.size(); i++) {
                        CarsAttributes r = c.get(i);
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
                        rb.setId(i + res);
                        radioGroupCars.addView(rb);
                        rb.setChecked(i == selected);
                    }

                    txtFullName.setText(result.getData().getName());
                    txtPhone.setText(result.getData().getPhone());

                    mProfile = result.getData();
                    pref.setProfile(mProfile);
                }

                progressBar.setVisibility(View.GONE);
                Log.d("onRequestSuccess", "fill data");
            } else {
                showToastError(R.string.error_loading_profile_data);
            }
        }
    }

    public final class CarsRequestListener implements RequestListener<CarsMakesResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
          //  Toast.makeText(getActivity(), R.string.error_loading_data, Toast.LENGTH_SHORT).show();
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
}
