package wash.rocket.xor.rocketwash.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import ru.tinkoff.acquiring.sdk.Money;
import ru.tinkoff.acquiring.sdk.OnPaymentListener;
import ru.tinkoff.acquiring.sdk.PayFormActivity;
import ru.tinkoff.acquiring.sdk.inflate.pay.PayCellType;
import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.UserAttributes;
import wash.rocket.xor.rocketwash.model.WashService;
import wash.rocket.xor.rocketwash.widgets.NiceSupportMapFragment;

public abstract class WashServiceInfoBaseFragment extends BaseFragment {

    private static final String TAG = WashServiceInfoBaseFragment.class.getSimpleName();
    private static final int REQUEST_PAY = 245;

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

    final protected void payReservation(int orderId,
                                        double amount,
                                        String title,
                                        String description) {
        PayFormActivity.init
                (
                        "1509384921522DEMO",
                        "eo8bv0zyqde8c6cn",
                        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv5yse9ka3ZQE0feuGtemYv3IqOlLck8zHUM7lTr0za6lXTszRSXfUO7jMb+L5C7e2QNFs+7sIX2OQJ6a+HG8kr+jwJ4tS3cVsWtd9NXpsU40PE4MeNr5RqiNXjcDxA+L4OsEm/BlyFOEOh2epGyYUd5/iO3OiQFRNicomT2saQYAeqIwuELPs1XpLk9HLx5qPbm8fRrQhjeUD5TLO8b+4yCnObe8vy/BMUwBfq+ieWADIjwWCMp2KTpMGLz48qnaD9kdrYJ0iyHqzb2mkDhdIzkim24A3lWoYitJCBrrB2xM05sm9+OdCI1f7nPNJbl5URHobSwR94IRGT7CJcUjvwIDAQAB"
                )
                .prepare(
                        String.valueOf(orderId),
                        Money.ofRubles(amount),
                        title,
                        description,
                        null,
                        null,
                        false,
                        true)
                .setCustomerKey(pref.getProfile().getFull_name())
                .useFirstAttachedCard(true)
                .setChargeMode(false)
                .setDesignConfiguration(PayCellType.SECURE_LOGOS, PayCellType.PAY_BUTTON, PayCellType.PAYMENT_CARD_REQUISITES)
                .startActivityForResult(getActivity(), REQUEST_PAY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_PAY == requestCode) {
            PayFormActivity.dispatchResult(resultCode, data, new OnPaymentListener() {
                @Override
                public void onSuccess(long l) {
                    Toast.makeText(getContext(), R.string.payment_successful, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled() {
                    Toast.makeText(getContext(), R.string.payment_canceled, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), getString(R.string.payment_error, e.getLocalizedMessage()), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
