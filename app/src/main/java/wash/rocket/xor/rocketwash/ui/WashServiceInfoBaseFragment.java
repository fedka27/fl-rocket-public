package wash.rocket.xor.rocketwash.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import ru.tinkoff.acquiring.sdk.Money;
import ru.tinkoff.acquiring.sdk.PayFormActivity;
import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.UserAttributes;
import wash.rocket.xor.rocketwash.model.WashService;
import wash.rocket.xor.rocketwash.widgets.NiceSupportMapFragment;

import static android.app.Activity.RESULT_OK;

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
//    {
//        mMap = map;
//        if (mMap != null) {
////            mMap.setMyLocationEnabled(true); //TODO Location ?
//            mMap.getUiSettings().setMyLocationButtonEnabled(true);
//
//            int mp = (int) getActivity().getResources().getDimension(R.dimen.map_padding);
//            mMap.setPadding(mp, mp, mp, mp);
//
//            infoBaloon = new GoogleMap.InfoWindowAdapter() {
//                @Override
//                public View getInfoWindow(Marker marker) {
//
//                    if (marker.equals(mPositionMarker))
//                        return null;
//
//                    String s = "";
//                    String d = "";
//                    if (mMarker != null) {
//                        s = txtInfoTitile.getText().toString();
//                        d = txtInfoDistance.getText().toString();
//                    } else
//                        getSpiceManager().execute(new MapReverceGeocodingRequest(mService.getLatitude(), mService.getLongitude()), "direction", DurationInMillis.ALWAYS_EXPIRED, new MapReverceGeocodingListener());
//
//                    mMarker = marker;
//
//                    // Getting view from the layout file
//                    View v = getActivity().getLayoutInflater().inflate(R.layout.info_windows, null);
//                    txtInfoTitile = (TextView) v.findViewById(R.id.address);
//                    txtInfoDistance = (TextView) v.findViewById(R.id.distance);
//                    infoProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
//                    infoBtnPath = (Button) v.findViewById(R.id.btnPath);
//                    infoBtnPath.setVisibility(View.GONE);
//
//                    if (!TextUtils.isEmpty(s)) {
//                        txtInfoTitile.setVisibility(View.VISIBLE);
//                        txtInfoDistance.setVisibility(View.VISIBLE);
//                        infoBtnPath.setVisibility(View.VISIBLE);
//                        infoProgressBar.setVisibility(View.GONE);
//
//                        txtInfoTitile.setText(s);
//                        txtInfoDistance.setText(d);
//                    } else {
//
//                        txtInfoTitile.setVisibility(View.GONE);
//                        txtInfoDistance.setVisibility(View.GONE);
//                        infoBtnPath.setVisibility(View.GONE);
//                        infoProgressBar.setVisibility(View.VISIBLE);
//                    }
//
//                    infoBtnPath.setVisibility(View.GONE);
//                    infoBtnPath.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            getSpiceManager().execute(new MapDirectionRequest(new LatLng(mService.getLatitude(), mService.getLongitude()), new LatLng(mService.getLatitude(), mService.getLongitude())), "direction", DurationInMillis.ONE_SECOND * 5, new MapDirectionRouteListener());
//                        }
//                    });
//
//
//                    return v;
//                }
//
//                @Override
//                public View getInfoContents(Marker marker) {
//                    return null;
//                }
//            };
//
//            mMap.setInfoWindowAdapter(infoBaloon);
//        }
//
//        setUpMapIfNeeded();
//
//        Handler h = new Handler();
//        h.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                LatLng loc = new LatLng(mService.getLatitude(), mService.getLongitude());
//                if (mMap != null) {
//                    mMap.addMarker(new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)));
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f), 1000, null);
//                }
//            }
//        }, 200);
//    }

    final protected void buy(double amount,
                             String title,
                             String description) {
        PayFormActivity.init("1509384921522DEMO ", "eo8bv0zyqde8c6cn ", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv5yse9ka3ZQE0feuGtemYv3IqOlLck8zHUM7lTr0za6lXTszRSXfUO7jMb+L5C7e2QNFs+7sIX2OQJ6a+HG8kr+jwJ4tS3cVsWtd9NXpsU40PE4MeNr5RqiNXjcDxA+L4OsEm/BlyFOEOh2epGyYUd5/iO3OiQFRNicomT2saQYAeqIwuELPs1XpLk9HLx5qPbm8fRrQhjeUD5TLO8b+4yCnObe8vy/BMUwBfq+ieWADIjwWCMp2KTpMGLz48qnaD9kdrYJ0iyHqzb2mkDhdIzkim24A3lWoYitJCBrrB2xM05sm9+OdCI1f7nPNJbl5URHobSwR94IRGT7CJcUjvwIDAQAB")
                .prepare("id", Money.ofRubles(amount), title, description, null, null, false, true)
                //todo customer key
                .setCustomerKey(pref.getProfile().getFull_name())
                .startActivityForResult(getActivity(), REQUEST_PAY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PAY) {
            if (resultCode == RESULT_OK) {
                Log.e(TAG, "Pay success");
            } else {
                Log.e(TAG, "Pay success");
            }
        }

    }
}
