package wash.rocket.xor.rocketwash.ui.history;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.Reservation;
import wash.rocket.xor.rocketwash.model.WashService;
import wash.rocket.xor.rocketwash.util.util;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<Reservation> reservations = new ArrayList<>();

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HistoryViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        ((HistoryViewHolder) holder).bind(reservations.get(position));
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations.clear();
        this.reservations.addAll(reservations);
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return reservations.size() == 0;
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        public TextView time;
        public TextView name;
        public TextView address;
        public TextView distance;
        public TextView date;
        public TextView txtAvailable;
        public ImageView imgBusy;


        public HistoryViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_wash_service_reserved, parent, false));
        }

        public void bind(Reservation reservation) {
            WashService s = reservation.getCarwash();

            this.name.setText(s.getName());
            this.address.setText(s.getAddress());

            if (s.getrDate() != null) {
                time.setText(util.dateToHM(s.getrDate()));
                date.setText(util.dateToddMM(s.getrDate()));

                time.setVisibility(View.VISIBLE);
                date.setVisibility(View.VISIBLE);

            } else {
                time.setText("");
                date.setText("");
            }
        }
    }
}
