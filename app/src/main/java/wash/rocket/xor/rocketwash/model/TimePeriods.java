package wash.rocket.xor.rocketwash.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Calendar;
import java.util.Date;

import wash.rocket.xor.rocketwash.util.util;

@JsonObject
public class TimePeriods implements Parcelable {

    @JsonField
    private String time_from;

    @JsonField
    private String time_from_no_time_zone;

    @JsonField
    private int price;

    private int selected = 0;

    private Date date;

    private Date date_td;

    public String getTime_from() {
        return time_from;
    }

    public void setTime_from(String time_from) {
        this.time_from = time_from;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Date getDate() {
        if (date == null)
            date = util.getDateS1(time_from);

        return date;
    }

    public String getTimeStr() {
        if (TextUtils.isEmpty(time_from_no_time_zone))
            return "";

        return time_from_no_time_zone.substring(time_from_no_time_zone.indexOf("T") + 1).replace(":00+00:00", "");
    }

    public void setToday(Date date_td) {
        this.date_td = date_td;
    }

    public boolean isToday() {
        Calendar c = Calendar.getInstance();
        c.setTime(date_td);
        int d = c.get(Calendar.DAY_OF_MONTH);
        int m = c.get(Calendar.MONTH);
        int y = c.get(Calendar.YEAR);
        c.setTime(getDate());
        int d1 = c.get(Calendar.DAY_OF_MONTH);
        int m1 = c.get(Calendar.MONTH);
        int y1 = c.get(Calendar.YEAR);
        return d == d1 && m == m1 && y == y1;
    }

    public boolean isTomorrow() {
        Calendar c = Calendar.getInstance();
        c.setTime(date_td);
        //c.roll(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.DAY_OF_MONTH, 1);
        int d = c.get(Calendar.DAY_OF_MONTH);
        int m = c.get(Calendar.MONTH);
        int y = c.get(Calendar.YEAR);
        c.setTime(getDate());
        int d1 = c.get(Calendar.DAY_OF_MONTH);
        int m1 = c.get(Calendar.MONTH);
        int y1 = c.get(Calendar.YEAR);
        return d == d1 && m == m1 && y == y1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time_from);
        dest.writeInt(price);
    }

    public TimePeriods() {

    }

    public TimePeriods(Parcel in) {
        time_from = in.readString();
        price = in.readInt();
    }

    public static final Creator<TimePeriods> CREATOR = new Creator<TimePeriods>() {
        public TimePeriods createFromParcel(Parcel in) {
            return new TimePeriods(in);
        }

        public TimePeriods[] newArray(int size) {
            return new TimePeriods[size];
        }
    };

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public String getTime_from_no_time_zone() {
        return time_from_no_time_zone;
    }

    public void setTime_from_no_time_zone(String time_from_no_time_zone) {
        this.time_from_no_time_zone = time_from_no_time_zone;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
