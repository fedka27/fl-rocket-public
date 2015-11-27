package wash.rocket.xor.rocketwash.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.TimePeriods;
import wash.rocket.xor.rocketwash.util.util;
import wash.rocket.xor.rocketwash.widgets.ButtonWithState;

public class TimeRecyclerViewAdapter extends RecyclerView.Adapter<TimeRecyclerViewAdapter.ViewHolder> {

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_LOADER = 1;


    private ArrayList<TimePeriods> list;
    private int mSelectedId = -1;
    private int mOldSelectedId = -1;

    private IOnSelectedItem mOnSelectedItem;
    private IOnRequestNextPage mOnRequestNextPage;
    private Typeface mFont;

    public TimeRecyclerViewAdapter(ArrayList<TimePeriods> list) {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        else
            return list.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        if (mFont == null)
            mFont = Typeface.createFromAsset(inflater.getContext().getAssets(), "roboto.ttf");

        return new ViewHolder(inflater.inflate(R.layout.calendar_button, viewGroup, false), inflater, TYPE_ITEM);
    }

    @Override
    public void onBindViewHolder(TimeRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.populate(list.get(position), position);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LayoutInflater inflater;
        ButtonWithState button;

        public ViewHolder(View itemView, LayoutInflater inflater, int type) {
            super(itemView);

            this.inflater = inflater;
            button = (ButtonWithState) itemView.findViewById(R.id.button);
            button.setOnClickListener(click);
            overrideFonts(inflater.getContext(), itemView);
        }

        public void populate(TimePeriods p, int position) {
            button.setTag(position);

            Log.d("TimeRecyclerViewAdapter", "util.dateToHM(p.getDate()) = " + util.dateToHM(p.getDate()));
            Log.d("TimeRecyclerViewAdapter", "p.getTime_from_no_time_zone() = " + p.getTime_from_no_time_zone());

            //button.setText(util.dateToHM(p.getDate()));
            button.setText(p.getTimeStr());
            button.setSelected(p.getSelected() == 1);
        }
    }

    public interface IOnSelectedItem {
        void onSelectedItem(TimePeriods item, int position);
    }

    public interface IOnRequestNextPage {
        void onRequestNextPage();
    }

    public void setOnSelectedItem(IOnSelectedItem onSelectedItem) {
        mOnSelectedItem = onSelectedItem;
    }

    public void setSelectionItemId(int position) {


        if (position == -1)
        {
            for (int i =0; i < list.size();i++)
                list.get(i).setSelected(0);
        }

        mSelectedId = position;
        if (mOldSelectedId > -1) {
            notifyItemChanged(mOldSelectedId);
        }
        mOldSelectedId = mSelectedId;
        if (mOldSelectedId > -1)
            notifyItemChanged(mSelectedId);
    }

    public void setOnRequestNextPage(IOnRequestNextPage value) {
        mOnRequestNextPage = value;
    }

    public int getSelectedPosition() {
        return mSelectedId;
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            TimePeriods t = list.get((Integer) v.getTag());
            if (mOnSelectedItem != null)
                mOnSelectedItem.onSelectedItem(t, (Integer) v.getTag());

            t.setSelected( t.getSelected() == 1 ? 0 : 1 );
            setSelectionItemId((Integer) v.getTag());
        }
    };

    public void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else {
                if (v instanceof TextView)
                    ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "roboto_light.ttf"));
                if (v instanceof Button)
                    ((Button) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "roboto_light.ttf"));
                if (v instanceof ButtonWithState)
                    ((ButtonWithState) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "roboto_light.ttf"));
                if (v instanceof CheckBox)
                    ((CheckBox) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "roboto_light.ttf"));
                if (v instanceof RadioButton)
                    ((RadioButton) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "roboto_light.ttf"));
            }

        } catch (Exception e) {
            // do not show;
        }
    }

}
