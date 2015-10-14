package wash.rocket.xor.rocketwash.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.CarMake;
import wash.rocket.xor.rocketwash.model.CarsAttributes;
import wash.rocket.xor.rocketwash.model.CarsMakes;
import wash.rocket.xor.rocketwash.model.CarsMakesResult;
import wash.rocket.xor.rocketwash.model.ProfileResult;
import wash.rocket.xor.rocketwash.requests.CarsMakesRequest;
import wash.rocket.xor.rocketwash.requests.ProfileRequest;
import wash.rocket.xor.rocketwash.util.Constants;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoaderFragment extends BaseFragment {

    public static final String TAG = "LoaderFragment";

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loader, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getSpiceManager().execute(new CarsMakesRequest(""), "cars", DurationInMillis.ONE_SECOND * 30, new CarsRequestListener());
    }


    private List<CarsMakes> list_cars;

    public final class ProfileRequestListener implements RequestListener<ProfileResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), "Ошибка получения данных", Toast.LENGTH_SHORT).show();
            mCallback.onLoading();
        }

        @Override
        public void onRequestSuccess(final ProfileResult result) {
            Log.d("onRequestSuccess", result.getStatus() == null ? "null" : result.getStatus());
            if (Constants.SUCCESS.equals(result.getStatus())) {

                if (result.getData() != null) {
                    List<CarsAttributes> c = result.getData().getCars_attributes();
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
                    }

                    pref.setProfile(result.getData());

                    Log.d("onRequestSuccess", "fill data");
                } else {
                    Toast.makeText(getActivity(), "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                }
            }

            mCallback.onLoading();
        }
    }

    public final class CarsRequestListener implements RequestListener<CarsMakesResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), R.string.error_loading_data, Toast.LENGTH_SHORT).show();
            mCallback.onLoading();
        }

        @Override
        public void onRequestSuccess(final CarsMakesResult result) {
            //progressBar.setVisibility(View.GONE);
            if (result != null) {
                list_cars = result.getData();
                getSpiceManager().execute(new ProfileRequest(pref.getSessionID()), "profile", DurationInMillis.ONE_SECOND, new ProfileRequestListener());
            }
        }
    }


}
