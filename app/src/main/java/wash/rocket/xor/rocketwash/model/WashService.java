package wash.rocket.xor.rocketwash.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wash.rocket.xor.rocketwash.model.deserilazers.DoubleDeserializer;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WashService implements Parcelable {
    @JsonProperty("id")
    private int id;

    @JsonProperty("organization_id")
    private int organization_id;

    @JsonProperty("address")
    private String address;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("email")
    private String email;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("active")
    private boolean active;

    @JsonProperty("created_at")
    private String created_at;

    @JsonProperty("updated_at")
    private String updated_at;

    @JsonDeserialize(using = DoubleDeserializer.class)
    @JsonProperty("latitude")
    private double latitude;

    @JsonDeserialize(using = DoubleDeserializer.class)
    @JsonProperty("longitude")
    private double longitude;

    @JsonProperty("name")
    private String name;

    @JsonProperty("time_zone")
    private String time_zone;

    @JsonProperty("plan_id")
    private int plan_id;

    @JsonProperty("agreement_number")
    private String agreement_number;

    @JsonProperty("deleted_at")
    private String deleted_at;

    @JsonProperty("top_order")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean top_order;

    @JsonProperty("mobile_stub_text")
    private String mobile_stub_text;

    @JsonProperty("sms_price")
    private String sms_price;

    @JsonProperty("online_reservation_price")
    private String online_reservation_price;

    @JsonProperty("distance")
    private float distance;

    @JsonProperty("bearing")
    private int bearing;

    @JsonProperty("service_name")
    private String service_name;

    @JsonProperty("time_periods")
    private List<TimePeriods> time_periods;

    private int type;
    private Date rDate;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(int organization_id) {
        this.organization_id = organization_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
        //return active == null ? false : active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime_zone() {
        return time_zone;
    }

    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }

    public int getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(int plan_id) {
        this.plan_id = plan_id;
    }

    public String getAgreement_number() {
        return agreement_number;
    }

    public void setAgreement_number(String agreement_number) {
        this.agreement_number = agreement_number;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public boolean isTop_order() {
        //return top_order == null ? false : top_order;
        return top_order;
    }

    public void setTop_order(boolean top_order) {
        this.top_order = top_order;
    }

    public String getMobile_stub_text() {
        return mobile_stub_text;
    }

    public void setMobile_stub_text(String mobile_stub_text) {
        this.mobile_stub_text = mobile_stub_text;
    }

    public String getSms_price() {
        return sms_price;
    }

    public void setSms_price(String sms_price) {
        this.sms_price = sms_price;
    }

    public String getOnline_reservation_price() {
        return online_reservation_price;
    }

    public void setOnline_reservation_price(String online_reservation_price) {
        this.online_reservation_price = online_reservation_price;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getBearing() {
        return bearing;
    }

    public void setBearing(int bearing) {
        this.bearing = bearing;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public List<TimePeriods> getTime_periods() {
        return time_periods;
    }

    public void setTime_periods(List<TimePeriods> time_periods) {
        this.time_periods = time_periods;
    }

    public WashService getClone() {
        WashService a = new WashService();

        a.id = id;
        a.organization_id = organization_id;
        a.address = address;
        a.phone = phone;
        a.email = email;
        a.active = active;
        a.created_at = created_at;
        a.updated_at = updated_at;
        a.latitude = latitude;
        a.longitude = longitude;
        a.name = name;
        a.time_zone = time_zone;
        a.plan_id = plan_id;
        a.agreement_number = agreement_number;
        a.deleted_at = deleted_at;
        a.setTop_order(isTop_order());
        a.mobile_stub_text = mobile_stub_text;
        a.sms_price = sms_price;
        a.online_reservation_price = online_reservation_price;
        a.distance = distance;
        a.bearing = bearing;
        a.service_name = service_name;
        if (time_periods != null) {
            a.time_periods = new ArrayList<>();
            for (int i = 0; i < time_periods.size(); i++) {
                TimePeriods t = new TimePeriods();
                t.setPrice(time_periods.get(i).getPrice());
                t.setTime_from(time_periods.get(i).getTime_from());
                a.time_periods.add(t);
            }
        }
        return a;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id );
        dest.writeInt(organization_id);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeInt(active ? 1 : 0);
        dest.writeString(created_at);
        dest.writeString(updated_at);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(name);
        dest.writeString(time_zone);
        dest.writeInt(plan_id);
        dest.writeString(agreement_number);
        dest.writeString(deleted_at);
        dest.writeInt(top_order ? 1 : 0);
        dest.writeString(mobile_stub_text);
        dest.writeString(sms_price);
        dest.writeString(online_reservation_price);
        dest.writeFloat(distance);
        dest.writeInt(bearing);
        dest.writeString(service_name);
        dest.writeList(time_periods);
        dest.writeInt(type);
    }


    public WashService()
    {

    }

    public WashService(Parcel in) {
        id = in.readInt();
        organization_id = in.readInt();
        address = in.readString();
        phone = in.readString();
        email = in.readString();
        active = in.readInt() == 1;
        created_at = in.readString();
        updated_at = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        name = in.readString();
        time_zone = in.readString();
        plan_id = in.readInt();
        agreement_number = in.readString();
        deleted_at = in.readString();
        top_order = in.readInt() == 1;
        mobile_stub_text = in.readString();
        sms_price = in.readString();
        online_reservation_price = in.readString();
        distance = in.readFloat();
        bearing = in.readInt();
        service_name = in.readString();
        time_periods = new ArrayList<>();
        in.readList(time_periods, TimePeriods.class.getClassLoader());
        type = in.readInt();
    }

    public static final Creator<WashService> CREATOR = new Creator<WashService>() {
        public WashService createFromParcel(Parcel in) {
            return new WashService(in);
        }

        public WashService[] newArray(int size) {
            return new WashService[size];
        }
    };

    public void var_dump()
    {
        Field[] fields = this.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++)
        {
            try
            {
                System.out.println(fields[i].getName() + " - " + fields[i].get(this));
            }
            catch (java.lang.IllegalAccessException e)
            {
               e.printStackTrace();
            }
        }
    }


    public Date getrDate() {
        return rDate;
    }

    public void setrDate(Date rDate) {
        this.rDate = rDate;
    }
}
