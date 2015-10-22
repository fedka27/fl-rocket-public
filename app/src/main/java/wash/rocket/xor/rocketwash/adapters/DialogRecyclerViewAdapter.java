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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.DialogItem;
import wash.rocket.xor.rocketwash.widgets.ButtonWithState;

public class DialogRecyclerViewAdapter extends RecyclerView.Adapter<DialogRecyclerViewAdapter.ViewHolder> {

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_LOADER = 1;
    public static final int TYPE_CHECK = 2;

    private ArrayList<DialogItem> list;
    private int mSelectedId = -1;
    private int mOldSelectedId = -1;

    private IOnSelectedItem mOnSelectedItem;
    private IOnRequestNextPage mOnRequestNextPage;

    public DialogRecyclerViewAdapter(ArrayList<DialogItem> list) {
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

        /*
        switch (viewType) {
            case TYPE_ITEM:
                return new ViewHolder(inflater.inflate(R.layout.list_item_dialog, viewGroup, false), inflater, TYPE_ITEM);
            case TYPE_LOADER:
                return new ViewHolder(inflater.inflate(R.layout.list_item_loader, viewGroup, false), inflater, TYPE_LOADER);
            case TYPE_CHECK:
                return new ViewHolder(inflater.inflate(R.layout.list_item_loader, viewGroup, false), inflater, TYPE_CHECK);
            default:
                return new ViewHolder(inflater.inflate(R.layout.list_item_dialog, viewGroup, false), inflater, TYPE_ITEM);
        }*/

        return new ViewHolder(inflater.inflate(R.layout.list_item_dialog, viewGroup, false), inflater, TYPE_ITEM);
    }

    @Override
    public void onBindViewHolder(DialogRecyclerViewAdapter.ViewHolder holder, int position) {
        DialogItem r = list.get(position);
        holder.populate(r, position);

        if (mOnRequestNextPage != null) {
            if (position == getItemCount() - 1 && r.type == TYPE_LOADER)
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

        public TextView title;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView, LayoutInflater inflater, int type) {
            super(itemView);

            this.inflater = inflater;
            this.title = (TextView) itemView.findViewById(R.id.txtTitle);
            this.linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            this.type = type;

            overrideFonts(inflater.getContext(), itemView);

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
                                mOnSelectedItem.onSelecredItem(list.get(mSelectedId), mSelectedId);
                            }
                        }
                    });
                    break;

                case TYPE_LOADER:
                    break;
            }
        }

        public void populate(DialogItem p, int position) {
            switch (type) {
                case TYPE_ITEM:
                    title.setText(p.title);
                    linearLayout.setTag(position);
                    break;
            }
        }
    }

    public void remove_by_type(int type, boolean all) {
        int count = list.size();

        for (int i = count - 1; i >= 0; i--) {
            if (list.get(i).type == type) {
                list.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public interface IOnSelectedItem {
        void onSelecredItem(DialogItem item, int position);
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

    //XXX move to utils
    public static void overrideFonts(final Context context, final View v) {
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
