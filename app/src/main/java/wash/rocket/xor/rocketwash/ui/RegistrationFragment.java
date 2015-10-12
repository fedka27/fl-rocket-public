package wash.rocket.xor.rocketwash.ui;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import wash.rocket.xor.rocketwash.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class RegistrationFragment extends BaseFragment {

    private static final int DIALOG_CAR_BRAND = 1;
    private static final int DIALOG_CAR_MODEL = 2;

    private static final String DIALOG_CAR_BRAND_TAG = "dialog.car.brand";
    private static final String DIALOG_CAR_MODEL_TAG = "dialog.car.model";

    private TextView txtCaption;
    private Button btnNext;

    private EditText edFIO;
    private EditText edBrandCar;
    private EditText edModelCar;
    private EditText edNumberCar;

    //private CarsJsonRequest carsJsonRequest;

    private int mCarBrandId = 0;
    private int mCarMoldelId = 0;

    private DialoglistCarBrands dlgCarBrand;
    private DialoglistCarModels dlgModels;


    public RegistrationFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        //carsJsonRequest  = new CarsJsonRequest("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() == null)
            return;

        txtCaption = (TextView) getView().findViewById(R.id.txtCaption);
        btnNext = (Button) getView().findViewById(R.id.btnNext);
        edFIO = (EditText) getView().findViewById(R.id.edFIO);
        edBrandCar = (EditText) getView().findViewById(R.id.edBrandCar);
        edModelCar = (EditText) getView().findViewById(R.id.edModelCar);
        edNumberCar = (EditText) getView().findViewById(R.id.edNumberCar);

        edBrandCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getSpiceManager().execute( carsJsonRequest, "json", DurationInMillis.ONE_MINUTE, new CarsRequestListener() );
                dlgCarBrand = DialoglistCarBrands.newInstance();
                dlgCarBrand.setTargetFragment(RegistrationFragment.this, DIALOG_CAR_BRAND);
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
                    dlgModels.setTargetFragment(RegistrationFragment.this, DIALOG_CAR_MODEL);
                    dlgModels.show(getFragmentManager(), DIALOG_CAR_MODEL_TAG);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .add(R.id.container, new SendSmsFragment(), "SendSmsFragment")
                        .addToBackStack("registration").commit();
            }
        });

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
                    mCarBrandId = data.getIntExtra("id", 0);
                    edBrandCar.setText(data.getStringExtra("name"));
                    mCarMoldelId = 0;
                    edModelCar.setText("");
                    break;
                case DIALOG_CAR_MODEL:
                    mCarMoldelId = data.getIntExtra("id", 0);
                    edModelCar.setText(data.getStringExtra("name"));
                    break;
            }
        }
    }

    private boolean check() {
        boolean b = false;
        return b;
    }



}
