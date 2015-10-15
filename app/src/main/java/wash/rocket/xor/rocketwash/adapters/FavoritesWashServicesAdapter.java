package wash.rocket.xor.rocketwash.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.WashService;

public class FavoritesWashServicesAdapter extends WashServicesAdapter {


    public FavoritesWashServicesAdapter(List<WashService> list) {
        super(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case TYPE_VIEW_REC:
                return new ViewHolder(inflater.inflate(R.layout.list_item_favorite_wash_service_rec, viewGroup, false), inflater, viewType);
            case TYPE_VIEW_CALL:
                return new ViewHolder(inflater.inflate(R.layout.list_item_favorite_wash_service_call, viewGroup, false), inflater, viewType);
            case TYPE_TOP_ORDER_REC:
                return new ViewHolder(inflater.inflate(R.layout.list_item_favorite_wash_service_rec_green, viewGroup, false), inflater, TYPE_VIEW_REC);
            case TYPE_TOP_ORDER_CALL:
                return new ViewHolder(inflater.inflate(R.layout.list_item_favorite_wash_service_call_green, viewGroup, false), inflater, TYPE_VIEW_CALL);
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

}
