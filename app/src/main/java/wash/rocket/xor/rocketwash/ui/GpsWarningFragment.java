package wash.rocket.xor.rocketwash.ui;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import wash.rocket.xor.rocketwash.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class GpsWarningFragment extends BaseFragment {

    public static final String TAG = "GpsWarningFragment";
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_COARSE = 2;

    private Button btnNext;
    private CheckBox checkBox;

    public GpsWarningFragment() {
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
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gps_warning, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() == null)
            return;

        btnNext = (Button) getView().findViewById(R.id.btnNext);
        checkBox = (CheckBox) getView().findViewById(R.id.checkBox3);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.setShowDialogGps(!checkBox.isChecked());
                //getActivity().getSupportFragmentManager().popBackStack();

                if (mCallback != null)
                    mCallback.onGPSWarningDone();
            }
        });

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            Snackbar.make(getView(), "Нет доступа к GPS , запросить разрешение ?",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                }
            }).show();

        }
        else
        {
            /*
            Snackbar.make(getView(), "ACCESS_FINE_LOCATION ок ?",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                }
            }).show();*/
        }

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            /*
            Snackbar.make(getView(), "Нет доступа к GPS , запросить разрешение ?",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE);
                }
            }).show();*/
        }
        else
        {
            /*
            Snackbar.make(getView(), "ACCESS_COARSE_LOCATION ок ?",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE);
                }
            }).show();*/
        }

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(getView(), "ACCESS_NETWORK_STATE ? ",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, REQUEST_COARSE);
                }
            }).show();
        }

       // android.permission.ACCESS_NETWORK_STATE

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }
}
