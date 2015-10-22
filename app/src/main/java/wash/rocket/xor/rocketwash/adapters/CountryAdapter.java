package wash.rocket.xor.rocketwash.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.util.Country;


/**
 * Call getCountry( String code ) to get matching country sign.
 * Call getCode( String country ) to get matching phone code.
 * It has been extended from BaseAdapter in order to make it compatible with Spinner,
 * ListView and so on (just instance it and give it as adapter).
 * <p/>
 * This class is provided AS IS without any warranty.
 *
 * @author Niki Romagnoli
 */

public class CountryAdapter extends BaseAdapter {

    private Context m_Context;
    private LayoutInflater mInflater;
    private ArrayList<Country> countries;
    private int resLauout;

    public CountryAdapter(Context context, LayoutInflater inflater, int viewCountryListItem, ArrayList<Country> countries) {
        super();

        mInflater = LayoutInflater.from(context);
        this.countries = countries;
    }

    @Override
    public int getCount() {
        return countries.size();
    }

    @Override
    public Country getItem(int index) {
        return countries.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int index, View recycleView, ViewGroup viewGroup) {
        TextView view;
        LinearLayout v = null;
        /*
		if (recycleView == null)
		{

			//v = mInflater.inflate(R.layout.view_country_list_item, null, false);
			v = (LinearLayout) mInflater.inflate(R.layout.view_country_list_item, null);
			view = (TextView) v.findViewById(R.id.tv_country_name);
			//view = new TextView(mInflater.getContext());
			//view.setPadding(30, 10, 10, 10);
			//view.setTextColor(mInflater.getContext().getResources().getColor(R.color.white));
			//view.setTextSize(TypedValue.COMPLEX_UNIT_PX,
			//		mInflater.getContext().getResources().getDimension(R.dimen.spinner_country_text_size));
		}
		else
		{
			view = (TextView) recycleView.findViewById(R.id.tv_country_name);
		}*/

        v = (LinearLayout) mInflater.inflate(R.layout.view_country_list_item, null);
        view = (TextView) v.findViewById(R.id.tv_country_name);

        Country c = getItem(index);
        view.setText("+" + c.mDialPrefix);

        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view;
        LinearLayout v = null;
		/*
		if (recycleView == null)
		{

			//v = mInflater.inflate(R.layout.view_country_list_item, null, false);
			v = (LinearLayout) mInflater.inflate(R.layout.view_country_list_item, null);
			view = (TextView) v.findViewById(R.id.tv_country_name);
			//view = new TextView(mInflater.getContext());
			//view.setPadding(30, 10, 10, 10);
			//view.setTextColor(mInflater.getContext().getResources().getColor(R.color.white));
			//view.setTextSize(TypedValue.COMPLEX_UNIT_PX,
			//		mInflater.getContext().getResources().getDimension(R.dimen.spinner_country_text_size));
		}
		else
		{
			view = (TextView) recycleView.findViewById(R.id.tv_country_name);
		}*/

        v = (LinearLayout) mInflater.inflate(R.layout.view_country_list_item_p, null);
        view = (TextView) v.findViewById(R.id.tv_country_name);
        view.setTypeface(Typeface.createFromAsset(parent.getContext().getAssets(), "roboto_light.ttf"));
        Country c = getItem(position);
        view.setText(c.mCountryName);
        return v;
    }

}