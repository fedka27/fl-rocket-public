package wash.rocket.xor.rocketwash.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.EmptyUserResult;
import wash.rocket.xor.rocketwash.model.PostCarResult;
import wash.rocket.xor.rocketwash.requests.CreateEmptyUserRequest;
import wash.rocket.xor.rocketwash.requests.PostCarRequest;
import wash.rocket.xor.rocketwash.util.Constants;

/**
 * A placeholder fragment containing a simple view.
 */
@SuppressLint("LongLogTag")
public class WithoutRegistrationFragment extends BaseFragment {

    public static final String TAG = "WithoutRegistrationFragment";

    private static final int DIALOG_CAR_BRAND = 1;
    private static final int DIALOG_CAR_MODEL = 2;

    private static final String DIALOG_CAR_BRAND_TAG = "dialog.car.brand";
    private static final String DIALOG_CAR_MODEL_TAG = "dialog.car.model";

    private TextView txtCaption;
    private Button btnNext;

    private EditText edBrandCar;
    private EditText edModelCar;
    private EditText edNumCar;

    private int mCarBrandId = 0;
    private int mCarMoldelId = 0;

    private DialoglistCarBrands dlgCarBrand;
    private DialoglistCarModels dlgModels;

    private ProgressBar progressBar;

    public WithoutRegistrationFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (IFragmentCallbacksInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IFragmentCallbacksInterface");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_without_registration, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() == null)
            return;

        edBrandCar = (EditText) getView().findViewById(R.id.edBrandCar);
        edModelCar = (EditText) getView().findViewById(R.id.edModelCar);
        txtCaption = (TextView) getView().findViewById(R.id.txtCaption);

        btnNext = (Button) getView().findViewById(R.id.btnNext);

        edBrandCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getSpiceManager().execute( carsJsonRequest, "json", DurationInMillis.ONE_MINUTE, new CarsRequestListener() );
                dlgCarBrand = DialoglistCarBrands.newInstance();
                dlgCarBrand.setTargetFragment(WithoutRegistrationFragment.this, DIALOG_CAR_BRAND);
                dlgCarBrand.show(getFragmentManager(), DIALOG_CAR_BRAND_TAG);
            }
        });

        edModelCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edBrandCar.getText().toString()))
                    Toast.makeText(getActivity(), R.string.need_first_select_carbrand, Toast.LENGTH_SHORT).show();
                else {
                    dlgModels = DialoglistCarModels.newInstance(mCarBrandId);
                    dlgModels.setTargetFragment(WithoutRegistrationFragment.this, DIALOG_CAR_MODEL);
                    dlgModels.show(getFragmentManager(), DIALOG_CAR_MODEL_TAG);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                if (TextUtils.isEmpty(edBrandCar.getText().toString())) {
                    showToastWarn(R.string.fragment_without_registration_brand_warn);
                    return;
                }

                if (TextUtils.isEmpty(edModelCar.getText().toString())) {
                    showToastWarn(R.string.fragment_without_registration_model_warn);
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                getSpiceManager().execute(new CreateEmptyUserRequest(), "empty_user", DurationInMillis.ALWAYS_EXPIRED, new CreateEmptyUserListener());
            }
        });

        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case DIALOG_CAR_BRAND:
                    mCarBrandId = data.getIntExtra("id", 0);
                    edBrandCar.setText(data.getStringExtra("name"));
                    mCarMoldelId = 0;
                    edModelCar.setText("");

                    dlgModels = DialoglistCarModels.newInstance(mCarBrandId);
                    dlgModels.setTargetFragment(WithoutRegistrationFragment.this, DIALOG_CAR_MODEL);
                    dlgModels.show(getFragmentManager(), DIALOG_CAR_MODEL_TAG);

                    break;
                case DIALOG_CAR_MODEL:
                    mCarMoldelId = data.getIntExtra("id", 0);
                    edModelCar.setText(data.getStringExtra("name"));
                    break;
            }
        }
    }

    @TargetApi(3)
    private void hideKeyboard() {
        if (Build.VERSION.SDK_INT < 3) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edBrandCar.getWindowToken(), 0);
        RelativeLayout rl = (RelativeLayout) getView().findViewById(R.id.main);
        rl.requestFocus();
    }

    public final class CreateEmptyUserListener implements RequestListener<EmptyUserResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            showToastError(R.string.error_loading_data);
        }

        @Override
        public void onRequestSuccess(final EmptyUserResult result) {
            if (result != null && Constants.SUCCESS.equals(result.getStatus())) {
                Log.d("CreateEmptyUserListener", "getSession_id = " + result.getData().getSession_id());
                pref.setSessionID(result.getData().getSession_id());
                getSpiceManager().execute(new PostCarRequest(mCarBrandId, mCarMoldelId, "", result.getData().getSession_id()), "create_car", DurationInMillis.ALWAYS_EXPIRED, new CreateCarListener());

            } else {
                showToastError(R.string.error_creating_account);
            }
        }
    }

    public final class CreateCarListener implements RequestListener<PostCarResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            showToastError(R.string.error_loading_data);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(final PostCarResult result) {
            progressBar.setVisibility(View.GONE);

            if (result != null && Constants.SUCCESS.equals(result.getStatus())) {
                if (mCallback != null)
                    mCallback.onLogged();
            } else {
                showToastError(R.string.error_loading_data);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
