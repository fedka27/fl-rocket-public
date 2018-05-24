package wash.rocket.xor.rocketwash.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.CarMake;
import wash.rocket.xor.rocketwash.model.CarsAttributes;
import wash.rocket.xor.rocketwash.model.CarsMakes;
import wash.rocket.xor.rocketwash.model.CarsMakesResult;
import wash.rocket.xor.rocketwash.model.CarsProfileResult;
import wash.rocket.xor.rocketwash.model.ProfileResult;
import wash.rocket.xor.rocketwash.requests.CarsMakesRequest;
import wash.rocket.xor.rocketwash.requests.ProfileRequest;
import wash.rocket.xor.rocketwash.util.Constants;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoaderFragment extends BaseFragment {

    public static final String TAG = "LoaderFragment";

    private List<CarsAttributes> pcars;

    private boolean mProfileLoaded = false;
    private boolean mCarsLoaded = false;


    public LoaderFragment() {
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
        setEventKeyboard(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loader, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getSpiceManager().execute(new CarsMakesRequest(), "carsmakes", DurationInMillis.ONE_HOUR, new CarsRequestListener());
        getSpiceManager().execute(new ProfileRequest(pref.getSessionID()), "profile", DurationInMillis.ALWAYS_EXPIRED, new ProfileRequestListener());
        //getSpiceManager().execute(new CarsProfileRequest(pref.getSessionID()), "profile", DurationInMillis.ONE_SECOND, new CarProfileRequestListener());
    }

    private List<CarsMakes> list_cars;

    public final class ProfileRequestListener implements RequestListener<ProfileResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            showToastError(R.string.error_loading_profile_session_data);
            //mCallback.onErrorLoading();
            Log.e(TAG, spiceException.getMessage());
            pref.setSessionID("");
            pref.setProfile(null);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    .replace(R.id.container, new LoginFragment(), LoginFragment.TAG)
                    .commit();
        }

        @Override
        public void onRequestSuccess(final ProfileResult result) {
            Log.d("onRequestSuccess", result.getStatus() == null ? "null" : result.getStatus());
            if (Constants.SUCCESS.equals(result.getStatus())) {

                if (result.getData() != null) {

                    pref.setProfile(result.getData());
                    getApp().setProfile(result.getData());

                    List<CarsAttributes> a = result.getData().getCars_attributes();

                    if (a == null)
                        Log.w("onRequestSuccess", "esult.getData().getCars_attributes() = null");
                    else
                        Log.w("onRequestSuccess", "esult.getData().getCars_attributes() =  " + result.getData().getCars_attributes().size());

                    mProfileLoaded = true;
                    result.getData().var_dump();
                    if (mCarsLoaded)
                        initCars(result.getData().getCars_attributes());

                    Log.d("onRequestSuccess", "fill data");
                } else {
                    showToastError(R.string.error_loading_data);
                }
            }

            mCallback.onLoading();
        }
    }

    public final class CarsRequestListener implements RequestListener<CarsMakesResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            showToastError(R.string.error_loading_data);
            mCallback.onLoading();
        }

        @Override
        public void onRequestSuccess(final CarsMakesResult result) {
            //progressBar.setVisibility(View.GONE);
            if (result != null) {
                list_cars = result.getData();
                mCarsLoaded = true;

                if (mProfileLoaded)
                    initCars(getApp().getProfile().getCars_attributes());
            }
        }
    }

    private class CarProfileRequestListener implements RequestListener<wash.rocket.xor.rocketwash.model.CarsProfileResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {

        }

        @Override
        public void onRequestSuccess(CarsProfileResult carsProfileResult) {
            pcars = carsProfileResult.getData();
            getSpiceManager().execute(new ProfileRequest(pref.getSessionID()), "profile", DurationInMillis.ONE_SECOND, new ProfileRequestListener());
        }
    }

    synchronized private void initCars(List<CarsAttributes> c) {
        if (c != null) {
            int i = pref.getUseCar();
            if (i > c.size())
                i = 0;

            CarsAttributes r = c.get(i);
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

            pref.setCarName(a + " " + b);
            pref.setCarNum(r.getTag());
        }
    }
}
