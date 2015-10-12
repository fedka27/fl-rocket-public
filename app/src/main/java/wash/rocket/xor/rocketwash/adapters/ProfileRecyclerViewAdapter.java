package wash.rocket.xor.rocketwash.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.CarsAttributes;

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
        }

        public void populate(CarsAttributes c, int position) {
            switch (type) {
                case TYPE_CAR:
                    String f = inflater.getContext().getResources().getString(R.string.cars);
                    txtTitle.setText(String.format(f, "" + (position + 1)));
                    edBrandCar.setText(c.getBrandName());
                    edModelCar.setText(c.getModelName());
                    //edNumberCar.setText();
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


}
