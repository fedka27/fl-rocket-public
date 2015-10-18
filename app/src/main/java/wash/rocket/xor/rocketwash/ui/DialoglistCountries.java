package wash.rocket.xor.rocketwash.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

import java.util.ArrayList;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.adapters.DialogRecyclerViewAdapter;
import wash.rocket.xor.rocketwash.model.DialogItem;
import wash.rocket.xor.rocketwash.util.Country;
import wash.rocket.xor.rocketwash.util.CountryMaster;

public class DialoglistCountries extends Dialoglist {

    public static DialoglistCountries newInstance() {
        DialoglistCountries d = new DialoglistCountries();
        d.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        /*
        Bundle data = new Bundle();
        data.putStringArrayList(LIST_PARAM, list);
        d.setArguments(data);*/
        return d;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        toolbar.setTitle(getActivity().getString(R.string.select_country));

        list = new ArrayList<>();
        adapter = new DialogRecyclerViewAdapter(list);

        CountryMaster cm = CountryMaster.getInstance(getActivity());
        ArrayList<Country> countries = cm.getCountries();
        String countryIsoCode = cm.getDefaultCountryIso();

        for (int i = 0; i < countries.size(); i++) {
            Country c = countries.get(i);
            DialogItem d = new DialogItem();
            d.uuid = c.mDialPrefix;
            d.title = c.mCountryName;
            list.add(d);
        }

        progressBar.setVisibility(View.GONE);

      //  Country country = cm.getCountryByIso(countryIsoCode);
      //  TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
      //  String countryiso = manager.getSimCountryIso().toUpperCase();

        // CountryAdapter adapter = new CountryAdapter(getActivity(), (LayoutInflater) getActivity().getSystemService(
        //         Context.LAYOUT_INFLATER_SERVICE), R.layout.view_country_list_item, countries);

        recyclerView.setAdapter(adapter);

        adapter.setOnSelectedItem(new DialogRecyclerViewAdapter.IOnSelectedItem() {
            @Override
            public void onSelecredItem(DialogItem item, int position) {
                Intent i = new Intent();
                i.putExtra("id", list.get(position).uuid);
                i.putExtra("name", list.get(position).title);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                dismissAllowingStateLoss();
            }
        });

    }

}
