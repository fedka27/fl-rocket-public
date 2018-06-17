package wash.rocket.xor.rocketwash.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.UserAttributes;
import wash.rocket.xor.rocketwash.model.WashService;
import wash.rocket.xor.rocketwash.widgets.NiceSupportMapFragment;

public abstract class WashServiceInfoBaseFragment extends BaseFragment {

    private static final String TAG = WashServiceInfoBaseFragment.class.getSimpleName();

    protected WashService mService;

    protected UserAttributes userAttributes;

    private TextView txtBal;
    private TextView txtDiscount;

    @Override
    public void onAttach(Activity activity) {

        Log.w(TAG, "onAttach");

        super.onAttach(activity);
        try {
            mCallback = (IFragmentCallbacksInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IFragmentCallbacksInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        mService = getWashService();

        userAttributes = mService.getTenant_user_attributes();

        if (mService != null) {
            mService.var_dump();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtDiscount = (TextView) view.findViewById(R.id.my_discount_text_view);
        txtBal = (TextView) view.findViewById(R.id.my_bonuses_text_view);

        initPointsAndDiscounts();

        NiceSupportMapFragment mapFragment = (NiceSupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                initMap(googleMap);

            }
        });
    }

    private void initPointsAndDiscounts() {

        //todo point and discount
        txtDiscount.setText(String.format(getActivity().getString(R.string.my_discount_),
                userAttributes.getDiscount()));
        txtBal.setText(String.format(getActivity().getString(R.string.my_bonuses_),
                userAttributes.getFinancial_center_user_bonuses_balance().getAmount()));

    }

    @Nullable
    protected abstract WashService getWashService();

    abstract protected void initMap(GoogleMap map);
}
