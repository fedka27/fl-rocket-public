package wash.rocket.xor.rocketwash.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.provider.NavigationMenuContent;
import wash.rocket.xor.rocketwash.widgets.CursorRecyclerViewAdapter;

public class MenuCursorAdapter extends CursorRecyclerViewAdapter<MenuCursorAdapter.ViewHolder> {

    private IOnSelectedItem mOnSelectedItem;

    public MenuCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTitle;
        public TextView mCounter;
        public ImageView mIcon;
        public LinearLayout mMain;
        IOnSelectedItem onSelectedItem;

        public ViewHolder(View view, IOnSelectedItem onSelectedItem) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.txtTitle);
            mCounter = (TextView) view.findViewById(R.id.counter);
            mIcon = (ImageView) view.findViewById(R.id.icon);
            mMain = (LinearLayout) view.findViewById(R.id.main);
            this.onSelectedItem = onSelectedItem;
            mMain.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onSelectedItem != null)
                onSelectedItem.onSelectedItem(getAdapterPosition(), (Integer) v.getTag());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menu, parent, false);
        return new ViewHolder(itemView, mOnSelectedItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {

        int id = cursor.getInt(cursor.getColumnIndex(NavigationMenuContent.MENU_ID));
        viewHolder.mMain.setTag(id);

        viewHolder.mTitle.setText(cursor.getString(cursor.getColumnIndex(NavigationMenuContent.MENU_NAME)));
        viewHolder.mIcon.setImageResource(cursor.getInt(cursor.getColumnIndex(NavigationMenuContent.MENU_RES_ICON)));
        int value = cursor.getInt(cursor.getColumnIndex(NavigationMenuContent.MENU_VALUE));
        viewHolder.mCounter.setText("" + value);
        viewHolder.mCounter.setVisibility(value > 0 ? View.VISIBLE : View.GONE);
    }

    public interface IOnSelectedItem {
         void onSelectedItem(int position, int id);
    }


    public void setOnSelectedItem(IOnSelectedItem value)
    {
        mOnSelectedItem = value;
    }
}