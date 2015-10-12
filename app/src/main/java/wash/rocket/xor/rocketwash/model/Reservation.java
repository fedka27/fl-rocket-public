package wash.rocket.xor.rocketwash.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import wash.rocket.xor.rocketwash.util.util;

public class Reservation implements Parcelable {

    @JsonProperty("id")
    int id;
    @JsonProperty("user_id")
    int user_id;
    int service_location_lane_id;
    String time_start;
    String rating;
    String created_at;
    String updated_at;
    String status;
    String thank_message;
    String thank_the_client;
    String comments;
    String name;
    String time_end;
    String paid;
    int car_id;
    boolean mobile;
    int full_duration;
    int ordinal;
    String admin_status;
    String full_discount;
    String notes;
    String paid_at;
    String price;
    String discounted_price;
    String rounded_price;
    @JsonProperty("result")
    String result;

    int carwash_id;
    String time_from;
    WashService carwash;

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

    @Override
    public int describeContents() {
        return 0;
    }

    public Reservation() {

    }

    public Reservation(Parcel in) {

        id = in.readInt();
        user_id = in.readInt();
        service_location_lane_id = in.readInt();
        time_start = in.readString();
        rating = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
        status = in.readString();
        thank_message = in.readString();
        thank_the_client = in.readString();
        comments = in.readString();
        name = in.readString();
        time_end = in.readString();
        paid = in.readString();
        car_id = in.readInt();
        mobile = in.readInt() == 1;
        full_duration = in.readInt();
        ordinal = in.readInt();
        admin_status = in.readString();
        full_discount = in.readString();
        notes = in.readString();
        paid_at = in.readString();
        price = in.readString();
        discounted_price = in.readString();
        rounded_price = in.readString();
        result = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeInt(user_id);
        dest.writeInt(service_location_lane_id);
        dest.writeString(time_start);
        dest.writeString(rating);
        dest.writeString(created_at);
        dest.writeString(updated_at);
        dest.writeString(status);
        dest.writeString(thank_message);
        dest.writeString(thank_the_client);
        dest.writeString(comments);
        dest.writeString(name);
        dest.writeString(time_end);
        dest.writeString(paid);
        dest.writeInt(car_id);
        dest.writeInt(mobile ? 1 : 0);
        dest.writeInt(full_duration);
        dest.writeInt(ordinal);
        dest.writeString(admin_status);
        dest.writeString(full_discount);
        dest.writeString(notes);
        dest.writeString(paid_at);
        dest.writeString(price);
        dest.writeString(discounted_price);
        dest.writeString(rounded_price);
        dest.writeString(result);
    }

    public static final Creator<Reservation> CREATOR = new Creator<Reservation>() {
        public Reservation createFromParcel(Parcel in) {
            return new Reservation(in);
        }

        public Reservation[] newArray(int size) {
            return new Reservation[size];
        }
    };
}
