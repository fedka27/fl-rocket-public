package wash.rocket.xor.rocketwash.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Calendar;
import java.util.Date;

import wash.rocket.xor.rocketwash.util.util;

public class TimePeriods implements Parcelable {

    @JsonProperty("time_from")
    private String time_from;

    @JsonProperty("price")
    private int price;

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
        return util.getDate(time_from);
    }

    public boolean isToday() {
        Calendar c = Calendar.getInstance();
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
        c.roll(Calendar.DAY_OF_MONTH, 1);
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
}
