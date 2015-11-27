package wash.rocket.xor.rocketwash.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.adapters.DialogRecyclerViewAdapter;
import wash.rocket.xor.rocketwash.model.CarMake;
import wash.rocket.xor.rocketwash.model.CarsMakes;
import wash.rocket.xor.rocketwash.model.CarsMakesResult;
import wash.rocket.xor.rocketwash.model.DialogItem;
import wash.rocket.xor.rocketwash.requests.CarsMakesRequest;

public class DialoglistCarModels extends Dialoglist {

    private static final String CAR_BRAND_ID = "carBrandid";

    private CarsMakesRequest carsJsonRequest;

    private int mIDBrand;

    public static DialoglistCarModels newInstance(int idBrand) {
        DialoglistCarModels d = new DialoglistCarModels();
        d.setStyle(DialogFragment.STYLE_NO_FRAME, 0);

        Bundle data = new Bundle();
        data.putInt(CAR_BRAND_ID, idBrand);
        d.setArguments(data);
        return d;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        carsJsonRequest = new CarsMakesRequest("");
        mIDBrand = getArguments().getInt(CAR_BRAND_ID);

        Log.d("DialoglistCarModels", "mIDBrand = " + mIDBrand);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list = new ArrayList<>();
        adapter = new DialogRecyclerViewAdapter(list);
        recyclerView.setAdapter(adapter);

        getSpiceManager().execute(carsJsonRequest, "carsmakes", DurationInMillis.ONE_HOUR, new CarsRequestListener());
        toolbar.setTitle(getActivity().getString(R.string.select_car_model));

        adapter.setOnSelectedItem(new DialogRecyclerViewAdapter.IOnSelectedItem() {
            @Override
            public void onSelecredItem(DialogItem item, int position) {
                Intent i = new Intent();
                i.putExtra("id", list.get(position).id);
                i.putExtra("name", list.get(position).title);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                dismissAllowingStateLoss();
            }
        });

    }

    public final class CarsRequestListener implements RequestListener<CarsMakesResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), R.string.error_loading_data, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(final CarsMakesResult result) {
            progressBar.setVisibility(View.GONE);
            list.clear();
            if (result != null) {
                if (result.getData() != null) {
                    List<CarsMakes> cars = result.getData();
                    for (int i = 0; i < cars.size(); i++) {
                        // list.add(new DialogItem(cars.get(i).getId(), "", cars.get(i).getName(), 0));

                        if (mIDBrand == cars.get(i).getId()) {
                            List<CarMake> models = cars.get(i).getCar_models();

                            if (models != null) {
                                for (int j = 0; j < models.size(); j++) {
                                    list.add(new DialogItem(models.get(j).getId(), "", models.get(j).getName(), 0));
                                }
                            }

                            break;
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

        }
    }
}
