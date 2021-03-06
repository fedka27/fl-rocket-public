package wash.rocket.xor.rocketwash.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.CarsAttributes;
import wash.rocket.xor.rocketwash.widgets.ButtonWithState;

public class ProfileRecyclerViewAdapter extends RecyclerView.Adapter<ProfileRecyclerViewAdapter.ViewHolder> {

    public static final int TYPE_CAR = 0;
    public static final int TYPE_ADD_CAR = 1;
    public static final int TYPE_DELETED = 2;

    private List<CarsAttributes> list;
    private int mSelectedId = -1;
    private int mOldSelectedId = -1;

    private IOnSelectedItem mOnSelectedItem;
    private IOnRequestNextPage mOnRequestNextPage;

    public ProfileRecyclerViewAdapter(List<CarsAttributes> list) {
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
        switch (viewType) {
            case TYPE_CAR:
                return new ViewHolder(inflater.inflate(R.layout.list_item_profile_car, viewGroup, false), inflater, viewType);
            case TYPE_ADD_CAR:
                return new ViewHolder(inflater.inflate(R.layout.list_item_profile_add_car, viewGroup, false), inflater, viewType);
            case TYPE_DELETED:
                return new ViewHolder(inflater.inflate(R.layout.list_item_empty, viewGroup, false), inflater, viewType);
            default:
                return null;
        }
        //return new ViewHolder(inflater.inflate(R.layout.list_item_dialog, viewGroup, false), inflater, viewType);
    }

    @Override
    public void onBindViewHolder(ProfileRecyclerViewAdapter.ViewHolder holder, int position) {
        if (holder != null) {
            CarsAttributes r = list.get(position);
            holder.populate(r, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LayoutInflater inflater;
        private int type;
        public TextView txtTitle;
        public EditText edBrandCar;
        public EditText edModelCar;
        public EditText edNumberCar;
        public LinearLayout linearLayout;
        public ImageView imgDelete;
        public TextWatcherEx txtw;

        public ViewHolder(View itemView, LayoutInflater inflater, int type) {
            super(itemView);

            this.inflater = inflater;
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            edBrandCar = (EditText) itemView.findViewById(R.id.edBrandCar);
            edModelCar = (EditText) itemView.findViewById(R.id.edModelCar);
            edNumberCar = (EditText) itemView.findViewById(R.id.edNumberCar);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            imgDelete = (ImageView) itemView.findViewById(R.id.imgDelete);
            this.type = type;

            if (edNumberCar != null) {
                txtw = new TextWatcherEx(0);
                edNumberCar.addTextChangedListener(txtw);
            }

            overrideFonts(inflater.getContext(), itemView);
        }

        public void populate(CarsAttributes c, int position) {
            switch (type) {
                case TYPE_CAR:
                    String f = inflater.getContext().getResources().getString(R.string.cars);
                    txtTitle.setText(String.format(f, "" + (position + 1)));
                    edBrandCar.setText(c.getBrandName());
                    edModelCar.setText(c.getModelName());
                    txtw.updatePosition(position);
                    edNumberCar.setText(c.getTag());
                    linearLayout.setTag(position);

                    edBrandCar.setTag(position);
                    edModelCar.setTag(position);
                    imgDelete.setTag(position);

                    edBrandCar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSelectedId = (Integer) v.getTag();
                            if (mOnSelectedItem != null) {
                                mOnSelectedItem.onSelecredItem(list.get(mSelectedId), mSelectedId, 1);
                            }
                        }
                    });

                    edModelCar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSelectedId = (Integer) v.getTag();
                            if (mOnSelectedItem != null) {
                                mOnSelectedItem.onSelecredItem(list.get(mSelectedId), mSelectedId, 2);
                            }
                        }
                    });

                    imgDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSelectedId = (Integer) v.getTag();
                            if (mOnSelectedItem != null) {
                                mOnSelectedItem.onSelecredItem(list.get(mSelectedId), mSelectedId, 3);
                            }
                        }
                    });

                    break;
                case TYPE_ADD_CAR:

                    //edNumberCar.addTextChangedListener(new TextWatcherEx(position));

                    linearLayout.setTag(position);
                    this.linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSelectedId = (Integer) v.getTag();
                            if (mOnSelectedItem != null) {
                                mOnSelectedItem.onSelecredItem(list.get(mSelectedId), mSelectedId, 0);
                            }
                        }
                    });

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
        void onSelecredItem(CarsAttributes item, int position, int type);
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


    private class TextWatcherEx implements TextWatcher {

        int mPosition;

        public TextWatcherEx(final int position) {
            mPosition = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //list.get(mPosition).setTag(s.toString());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //list.get(mPosition).setTag(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
            list.get(mPosition).setTag(s.toString());
        }

        public void updatePosition(int position) {
            this.mPosition = position;
        }
    }

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
