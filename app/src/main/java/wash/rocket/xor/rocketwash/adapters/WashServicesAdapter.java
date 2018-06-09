package wash.rocket.xor.rocketwash.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.WashService;
import wash.rocket.xor.rocketwash.util.util;
import wash.rocket.xor.rocketwash.widgets.ButtonWithState;

public class WashServicesAdapter extends RecyclerView.Adapter<WashServicesAdapter.ViewHolder> {

    public static final int TYPE_VIEW_REC = 0;
    public static final int TYPE_VIEW_CALL = 1;
    public static final int TYPE_TOP_ORDER_CALL = 2;
    public static final int TYPE_TOP_ORDER_REC = 3;
    public static final int TYPE_LOADER = 4;
    public static final int TYPE_GROUP = 5;
    public static final int TYPE_RESERVED = 6;

    private List<WashService> list;

    private IOnSelectedItem mOnSelectedItem;
    private IOnRequestNextPage mOnRequestNextPage;

    public WashServicesAdapter(List<WashService> list) {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case TYPE_VIEW_REC:
                return new ViewHolder(inflater.inflate(R.layout.list_item_wash_service_rec, viewGroup, false), inflater, viewType);
            case TYPE_VIEW_CALL:
                return new ViewHolder(inflater.inflate(R.layout.list_item_wash_service_call, viewGroup, false), inflater, viewType);
            case TYPE_TOP_ORDER_REC:
                return new ViewHolder(inflater.inflate(R.layout.list_item_wash_service_rec_green, viewGroup, false), inflater, TYPE_VIEW_REC);
            case TYPE_TOP_ORDER_CALL:
                return new ViewHolder(inflater.inflate(R.layout.list_item_wash_service_call_green, viewGroup, false), inflater, TYPE_VIEW_CALL);
            case TYPE_LOADER:
                return new ViewHolder(inflater.inflate(R.layout.list_item_loader, viewGroup, false), inflater, viewType);
            case TYPE_GROUP:
                return new ViewHolder(inflater.inflate(R.layout.list_item_group, viewGroup, false), inflater, viewType);
            case TYPE_RESERVED:
                return new ViewHolder(inflater.inflate(R.layout.list_item_wash_service_reserved, viewGroup, false), inflater, viewType);


            default:
                return new ViewHolder(inflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false), inflater, viewType);
        }
    }

    @Override
    public void onBindViewHolder(WashServicesAdapter.ViewHolder holder, int position) {

        WashService c = list.get(position);
        holder.populate(c, position);

        if (mOnRequestNextPage != null) {
            if (position == getItemCount() - 1 && c.getType() == TYPE_LOADER)
                mOnRequestNextPage.onRequestNextPage();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = list.get(position).getType();

        if (type != TYPE_LOADER && type != TYPE_GROUP && type != TYPE_RESERVED) {
            if (!list.get(position).isActive())
                return list.get(position).isTop_order() ? TYPE_TOP_ORDER_CALL : TYPE_VIEW_CALL;
            else
                return list.get(position).isTop_order() ? TYPE_TOP_ORDER_REC : TYPE_VIEW_REC;
        } else
            return type;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LayoutInflater inflater;
        private int type;
        public TextView time;
        public TextView name;
        public TextView address;
        public TextView distance;
        public TextView date;
        public TextView txtAvailable;
        public ImageView imgBusy;
        public View itemView;
        @Nullable public RatingBar ratingBar;

        RelativeLayout layout_button_rec;
        RelativeLayout layout_button_call;
        RelativeLayout layout_button_hide;
        RelativeLayout layout_button_more;
        RelativeLayout layout_button_cancel;

        public ViewHolder(View itemView, LayoutInflater inflater, int type) {
            super(itemView);

            this.inflater = inflater;
            this.type = type;
            this.itemView = itemView;

            switch (type) {
                case TYPE_VIEW_CALL:
                case TYPE_VIEW_REC:
                case TYPE_RESERVED:

                    this.name = (TextView) itemView.findViewById(R.id.name);
                    this.time = (TextView) itemView.findViewById(R.id.time);
                    this.address = (TextView) itemView.findViewById(R.id.address);
                    this.distance = (TextView) itemView.findViewById(R.id.distance);
                    this.date = (TextView) itemView.findViewById(R.id.date);
                    this.imgBusy = (ImageView) itemView.findViewById(R.id.imgBusy);
                    this.ratingBar = itemView.findViewById(R.id.rating_bar);

                    txtAvailable = (TextView) itemView.findViewById(R.id.txtAvailable);

                    layout_button_rec = (RelativeLayout) itemView.findViewById(R.id.rec);
                    layout_button_call = (RelativeLayout) itemView.findViewById(R.id.call);
                    layout_button_hide = (RelativeLayout) itemView.findViewById(R.id.hide);
                    layout_button_more = (RelativeLayout) itemView.findViewById(R.id.more);
                    layout_button_cancel = (RelativeLayout) itemView.findViewById(R.id.cancel);


                    if (layout_button_rec != null) {
                        layout_button_rec.setOnClickListener(backButtonClickListenerRec);
                    }
                    if (layout_button_call != null) {
                        layout_button_call.setOnClickListener(backButtonClickListenerCall);
                    }
                    if (layout_button_hide != null) {
                        layout_button_hide.setOnClickListener(backButtonClickListenerHide);
                    }
                    if (layout_button_more != null) {
                        layout_button_more.setOnClickListener(backButtonClickListenerMore);
                    }
                    if (layout_button_cancel != null) {
                        layout_button_cancel.setOnClickListener(backButtonClickListenerCancel);
                    }

                    this.itemView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final int sel = (Integer) v.getTag();
                            if (mOnSelectedItem != null) {
                                mOnSelectedItem.onSelectedItem(list.get(sel), sel, 0);
                            }
                        }
                    });

                    break;
                case TYPE_GROUP:
                    this.name = (TextView) itemView.findViewById(R.id.name);
                    break;
                case TYPE_LOADER:
                    break;
            }

            overrideFonts(inflater.getContext(), itemView);
        }

        public void populate(WashService s, int position) {
            if (ratingBar != null) {
                ratingBar.setRating(s.getRating_service_location());
            }
            switch (type) {
                case TYPE_VIEW_CALL:
                case TYPE_VIEW_REC:
                    this.name.setText(s.getName());
                    this.address.setText(s.getAddress());
                    this.distance.setText(String.format("%.1f км", s.getDistance()));
                    this.itemView.setTag(position);

                    if (layout_button_rec != null)
                        layout_button_rec.setTag(position);
                    if (layout_button_call != null)
                        layout_button_call.setTag(position);
                    if (layout_button_hide != null)
                        layout_button_hide.setTag(position);
                    if (layout_button_more != null)
                        layout_button_more.setTag(position);
                    if (layout_button_cancel != null)
                        layout_button_cancel.setTag(position);

                    //time.setText(s.getTime_periods() == null ? "0" : "" + s.getTime_periods().size());
                    if (s.getTime_periods() != null && s.getTime_periods().size() > 0 && s.isActive())
                        txtAvailable.setVisibility(View.VISIBLE);
                    else
                        txtAvailable.setVisibility(View.GONE);

                    if (s.getTime_periods() == null || s.getTime_periods().size() == 0) {
                        imgBusy.setVisibility(View.VISIBLE);
                        time.setVisibility(View.GONE);
                    } else {
                        imgBusy.setVisibility(View.GONE);
                        time.setVisibility(View.VISIBLE);

                        Date d = util.getDatenoUTC(s.getTime_periods().get(0).getTime_from_no_time_zone());
                        if (d != null)
                            time.setText(util.dateToHM(d));
                        else
                            time.setText("");
                    }

                    break;
                case TYPE_RESERVED:

                    this.name.setText(s.getName());
                    this.address.setText(s.getAddress());

                    if (layout_button_rec != null)
                        layout_button_rec.setTag(position);
                    if (layout_button_call != null)
                        layout_button_call.setTag(position);
                    if (layout_button_hide != null)
                        layout_button_hide.setTag(position);
                    if (layout_button_more != null)
                        layout_button_more.setTag(position);
                    if (layout_button_cancel != null)
                        layout_button_cancel.setTag(position);

                    if (s.getrDate() != null) {
                        time.setText(util.dateToHM(s.getrDate()));
                        date.setText(util.dateToddMM(s.getrDate()));

                        time.setVisibility(View.VISIBLE);
                        date.setVisibility(View.VISIBLE);

                    } else {
                        time.setText("");
                        date.setText("");
                    }

                    break;
                case TYPE_GROUP:
                    this.name.setText(s.getName());
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
        void onSelectedItem(WashService item, int position, int button);
    }

    public interface IOnRequestNextPage {
        void onRequestNextPage();
    }

    public void setOnSelectedItem(IOnSelectedItem onSelectedItem) {
        mOnSelectedItem = onSelectedItem;
    }

    public void setOnRequestNextPage(IOnRequestNextPage value) {
        mOnRequestNextPage = value;
    }


    private OnClickListener backButtonClickListenerCall = new OnClickListener() {
        @Override
        public void onClick(View v) {

            final int sel = (Integer) v.getTag();
            if (mOnSelectedItem != null) {
                mOnSelectedItem.onSelectedItem(list.get(sel), sel, 1);
            }
        }
    };

    private OnClickListener backButtonClickListenerRec = new OnClickListener() {
        @Override
        public void onClick(View v) {

            final int sel = (Integer) v.getTag();
            if (mOnSelectedItem != null) {
                mOnSelectedItem.onSelectedItem(list.get(sel), sel, 2);
            }
        }
    };

    private OnClickListener backButtonClickListenerHide = new OnClickListener() {
        @Override
        public void onClick(View v) {

            final int sel = (Integer) v.getTag();
            if (mOnSelectedItem != null) {
                mOnSelectedItem.onSelectedItem(list.get(sel), sel, 3);
            }
        }
    };

    private OnClickListener backButtonClickListenerMore = new OnClickListener() {
        @Override
        public void onClick(View v) {

            final int sel = (Integer) v.getTag();
            if (mOnSelectedItem != null) {
                mOnSelectedItem.onSelectedItem(list.get(sel), sel, 4);
            }
        }
    };

    private OnClickListener backButtonClickListenerCancel = new OnClickListener() {
        @Override
        public void onClick(View v) {

            final int sel = (Integer) v.getTag();
            if (mOnSelectedItem != null) {
                mOnSelectedItem.onSelectedItem(list.get(sel), sel, 5);
            }
        }
    };

    public void remove(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
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
