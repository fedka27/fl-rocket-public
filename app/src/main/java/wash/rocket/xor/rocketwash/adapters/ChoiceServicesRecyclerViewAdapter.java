package wash.rocket.xor.rocketwash.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.ChoiceService;
import wash.rocket.xor.rocketwash.widgets.ButtonWithState;

public class ChoiceServicesRecyclerViewAdapter extends RecyclerView.Adapter<ChoiceServicesRecyclerViewAdapter.ViewHolder> {

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_LOADER = 1;


    private ArrayList<ChoiceService> list;
    private int mSelectedId = -1;
    private int mOldSelectedId = -1;

    private IOnSelectedItem mOnSelectedItem;
    private IOnRequestNextPage mOnRequestNextPage;
    private Typeface mFont;

    public ChoiceServicesRecyclerViewAdapter(ArrayList<ChoiceService> list) {
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

        return new ViewHolder(inflater.inflate(R.layout.list_item_choice_service, viewGroup, false), inflater, TYPE_ITEM);
    }

    @Override
    public void onBindViewHolder(ChoiceServicesRecyclerViewAdapter.ViewHolder holder, int position) {
        ChoiceService r = list.get(position);
        holder.populate(r, position);

        if (mOnRequestNextPage != null) {
            if (position == getItemCount() - 1 && r.getType() == TYPE_LOADER)
                mOnRequestNextPage.onRequestNextPage();
        }
    }

    @Override
    public int getItemViewType(int position) {
        // return list.get(position).type;
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LayoutInflater inflater;
        private int type;
        public TextView txtTitle;
        public TextView txtSum;
        public TextView txtDescription;
        public CheckBox check;
        public RelativeLayout linearLayout;

        public ViewHolder(View itemView, LayoutInflater inflater, int type) {
            super(itemView);

            this.inflater = inflater;
            this.txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            this.txtSum = (TextView) itemView.findViewById(R.id.txtSum);
            //txtSum.setTypeface(mFont);
            this.txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
            this.check = (CheckBox) itemView.findViewById(R.id.check);
            this.linearLayout = (RelativeLayout) itemView.findViewById(R.id.linearLayout);
            this.type = type;

            overrideFonts(inflater.getContext(), itemView);


            //this.txtDescription.setVisibility(View.GONE);

            switch (type) {
                case TYPE_ITEM:

                    this.linearLayout.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSelectedId = (Integer) v.getTag();
                            if (mOldSelectedId > -1) {
                                //  notifyItemChanged(mOldSelectedId);
                            }

                            mOldSelectedId = mSelectedId;
                            notifyItemChanged(mSelectedId);

                            if (mOnSelectedItem != null) {
                                mOnSelectedItem.onSelectedItem(list.get(mSelectedId), mSelectedId);
                            }

                            CheckBox c = (CheckBox) v.findViewById(R.id.check);
                            if (c != null) {
                                c.setChecked(!c.isChecked());
                                ChoiceService l = list.get(mSelectedId);
                                l.setCheck(c.isChecked() ? 1 : 0);
                            }

                        }
                    });

                    check.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //boolean b = list.get((Integer) v.getTag()).isCheck();
                            list.get((Integer) v.getTag()).setCheck(((CheckBox) v).isChecked() ? 1 : 0);
                        }
                    });

                    break;
            }
        }

        public void populate(ChoiceService p, int position) {
            switch (type) {
                case TYPE_ITEM:
                    txtTitle.setText(p.getName());
                    this.txtTitle.setText(p.getName());
                    this.txtSum.setText(String.format("%d %s", p.getPrice(), inflater.getContext().getString(R.string.rubleSymbolJava)));
                    this.txtDescription.setText(p.getDescription());
                    this.check.setChecked(p.isCheck());
                    linearLayout.setTag(position);
                    check.setTag(position);
                    break;
            }
        }
    }

    public void remove_by_type(int type, boolean all) {
        int count = list.size();

        for (int i = count - 1; i >= 0; i--) {
            if (list.get(i).getType() == type) {
                list.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public interface IOnSelectedItem {
        void onSelectedItem(ChoiceService item, int position);
    }

    public interface IOnRequestNextPage {
        void onRequestNextPage();
    }

    public void setOnSelectedItem(IOnSelectedItem onSelectedItem) {
        mOnSelectedItem = onSelectedItem;
    }

    public void setSelectionItemId(int position) {
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
