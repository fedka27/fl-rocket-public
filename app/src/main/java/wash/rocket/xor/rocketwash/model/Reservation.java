package wash.rocket.xor.rocketwash.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

import wash.rocket.xor.rocketwash.util.util;

@JsonObject
public class Reservation implements Parcelable {

    @JsonField
    int id;
    @JsonField
    int user_id;
    @JsonField
    int service_location_lane_id;
    @JsonField
    String time_start;
    @JsonField
    String rating;
    @JsonField
    String created_at;
    @JsonField
    String updated_at;
    @JsonField
    String status;
    @JsonField
    String thank_message;
    @JsonField
    String thank_the_client;
    @JsonField
    String comments;
    @JsonField
    String name;
    @JsonField
    String time_end;
    @JsonField
    String paid;
    @JsonField
    int car_id;
    @JsonField
    boolean mobile;
    @JsonField
    int full_duration;
    @JsonField
    int ordinal;
    @JsonField
    String admin_status;
    @JsonField
    String full_discount;
    @JsonField
    String notes;
    @JsonField
    String paid_at;
    @JsonField
    String price;
    @JsonField
    String discounted_price;
    @JsonField
    String rounded_price;
    @JsonField
    String result;

    @JsonField
    int carwash_id;
    @JsonField
    String time_from;
    @JsonField
    WashService carwash;
    @JsonField
    String time_from_no_time_zone;
    @JsonField
    String time_to_no_time_zone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getService_location_lane_id() {
        return service_location_lane_id;
    }

    public void setService_location_lane_id(int service_location_lane_id) {
        this.service_location_lane_id = service_location_lane_id;
    }

    public String getTime_start() {
        return time_start;
    }

    public Date getTime_start_Date() {
        return util.getDateS(getTime_start());
    }

    public String getTime_start_format() {
        Date d = getTime_start_Date();
        if (d == null)
            d = util.getDate(getTime_start());
        if (d == null)
            d = util.getDateS1(getTime_start());
        if (d == null)
            d = new Date();

        return util.dateToDMYHM(d);
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThank_message() {
        return thank_message;
    }

    public void setThank_message(String thank_message) {
        this.thank_message = thank_message;
    }

    public String getThank_the_client() {
        return thank_the_client;
    }

    public void setThank_the_client(String thank_the_client) {
        this.thank_the_client = thank_the_client;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public int getCar_id() {
        return car_id;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public int getFull_duration() {
        return full_duration;
    }

    public void setFull_duration(int full_duration) {
        this.full_duration = full_duration;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public String getAdmin_status() {
        return admin_status;
    }

    public void setAdmin_status(String admin_status) {
        this.admin_status = admin_status;
    }

    public String getFull_discount() {
        return full_discount;
    }

    public void setFull_discount(String full_discount) {
        this.full_discount = full_discount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPaid_at() {
        return paid_at;
    }

    public void setPaid_at(String paid_at) {
        this.paid_at = paid_at;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscounted_price() {
        return discounted_price;
    }

    public void setDiscounted_price(String discounted_price) {
        this.discounted_price = discounted_price;
    }

    public String getRounded_price() {
        return rounded_price;
    }

    public void setRounded_price(String rounded_price) {
        this.rounded_price = rounded_price;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public WashService getCarwash() {
        return carwash;
    }

    public void setCarwash(WashService carwash) {
        this.carwash = carwash;
    }

    public int getCarwash_id() {
        return carwash_id;
    }

    public void setCarwash_id(int carwash_id) {
        this.carwash_id = carwash_id;
    }

    public String getTime_from() {
        return time_from;
    }

    public void setTime_from(String time_from) {
        this.time_from = time_from;
    }

    public Reservation() {

    }

    public String getTime_to_no_time_zone() {
        return time_to_no_time_zone;
    }

    public void setTime_to_no_time_zone(String time_to_no_time_zone) {
        this.time_to_no_time_zone = time_to_no_time_zone;
    }

    public String getTime_from_no_time_zone() {
        return time_from_no_time_zone;
    }

    public void setTime_from_no_time_zone(String time_from_no_time_zone) {
        this.time_from_no_time_zone = time_from_no_time_zone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.user_id);
        dest.writeInt(this.service_location_lane_id);
        dest.writeString(this.time_start);
        dest.writeString(this.rating);
        dest.writeString(this.created_at);
        dest.writeString(this.updated_at);
        dest.writeString(this.status);
        dest.writeString(this.thank_message);
        dest.writeString(this.thank_the_client);
        dest.writeString(this.comments);
        dest.writeString(this.name);
        dest.writeString(this.time_end);
        dest.writeString(this.paid);
        dest.writeInt(this.car_id);
        dest.writeByte(mobile ? (byte) 1 : (byte) 0);
        dest.writeInt(this.full_duration);
        dest.writeInt(this.ordinal);
        dest.writeString(this.admin_status);
        dest.writeString(this.full_discount);
        dest.writeString(this.notes);
        dest.writeString(this.paid_at);
        dest.writeString(this.price);
        dest.writeString(this.discounted_price);
        dest.writeString(this.rounded_price);
        dest.writeString(this.result);
        dest.writeInt(this.carwash_id);
        dest.writeString(this.time_from);
        dest.writeParcelable(this.carwash, 0);
        dest.writeString(this.time_from_no_time_zone);
        dest.writeString(this.time_to_no_time_zone);
    }

    protected Reservation(Parcel in) {
        this.id = in.readInt();
        this.user_id = in.readInt();
        this.service_location_lane_id = in.readInt();
        this.time_start = in.readString();
        this.rating = in.readString();
        this.created_at = in.readString();
        this.updated_at = in.readString();
        this.status = in.readString();
        this.thank_message = in.readString();
        this.thank_the_client = in.readString();
        this.comments = in.readString();
        this.name = in.readString();
        this.time_end = in.readString();
        this.paid = in.readString();
        this.car_id = in.readInt();
        this.mobile = in.readByte() != 0;
        this.full_duration = in.readInt();
        this.ordinal = in.readInt();
        this.admin_status = in.readString();
        this.full_discount = in.readString();
        this.notes = in.readString();
        this.paid_at = in.readString();
        this.price = in.readString();
        this.discounted_price = in.readString();
        this.rounded_price = in.readString();
        this.result = in.readString();
        this.carwash_id = in.readInt();
        this.time_from = in.readString();
        this.carwash = in.readParcelable(WashService.class.getClassLoader());
        this.time_from_no_time_zone = in.readString();
        this.time_to_no_time_zone = in.readString();
    }

    public static final Creator<Reservation> CREATOR = new Creator<Reservation>() {
        public Reservation createFromParcel(Parcel source) {
            return new Reservation(source);
        }

        public Reservation[] newArray(int size) {
            return new Reservation[size];
        }
    };
}
