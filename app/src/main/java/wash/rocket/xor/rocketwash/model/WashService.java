package wash.rocket.xor.rocketwash.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

@JsonObject
public class WashService implements Parcelable {
    @JsonField
    private int id;

    @JsonField
    private int organization_id;

    @JsonField
    private String address;

    @JsonField
    private String phone;

    @JsonField
    private String email;

    @JsonField
    private boolean active;

    @JsonField
    private String created_at;

    @JsonField
    private String updated_at;

    @JsonField
    private double latitude;

    @JsonField
    private double longitude;

    @JsonField
    private String name;

    @JsonField
    private String time_zone;

    @JsonField
    private int plan_id;

    @JsonField
    private String agreement_number;

    @JsonField
    private String deleted_at;

    @JsonField
    private boolean top_order;

    @JsonField
    private String mobile_stub_text;

    @JsonField
    private String sms_price;

    @JsonField
    private String online_reservation_price;

    @JsonField
    private float distance;

    @JsonField
    private int bearing;

    @JsonField
    private String service_name;

    @JsonField
    private List<TimePeriods> time_periods;

    @JsonField
    private int favorite_id;

    @JsonField
    @Nullable
    private UserAttributes tenant_user_attributes;

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

    protected WashService(Parcel in) {
        this.id = in.readInt();
        this.organization_id = in.readInt();
        this.address = in.readString();
        this.phone = in.readString();
        this.email = in.readString();
        this.active = in.readByte() != 0;
        this.created_at = in.readString();
        this.updated_at = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.name = in.readString();
        this.time_zone = in.readString();
        this.plan_id = in.readInt();
        this.agreement_number = in.readString();
        this.deleted_at = in.readString();
        this.top_order = in.readByte() != 0;
        this.mobile_stub_text = in.readString();
        this.sms_price = in.readString();
        this.online_reservation_price = in.readString();
        this.distance = in.readFloat();
        this.bearing = in.readInt();
        this.service_name = in.readString();
        this.time_periods = in.createTypedArrayList(TimePeriods.CREATOR);
        this.favorite_id = in.readInt();
        this.type = in.readInt();
        long tmpRDate = in.readLong();
        this.rDate = tmpRDate == -1 ? null : new Date(tmpRDate);
        this.tenant_user_attributes = in.readParcelable(UserAttributes.class.getClassLoader());
    }

    public static Creator<WashService> getCREATOR() {
        return CREATOR;
    }

    public UserAttributes getTenant_user_attributes() {
        if (tenant_user_attributes == null) tenant_user_attributes = new UserAttributes();
        return tenant_user_attributes;
    }

    public void setTenant_user_attributes(@Nullable UserAttributes tenant_user_attributes) {
        this.tenant_user_attributes = tenant_user_attributes;
    }


    public WashService() {

    }

    public void var_dump() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                System.out.println(fields[i].getName() + " - " + fields[i].get(this));
            } catch (java.lang.IllegalAccessException e) {
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

    public int getFavorite_id() {
        return favorite_id;
    }

    public void setFavorite_id(int favorite_id) {
        this.favorite_id = favorite_id;
    }

    @Override
    public int describeContents() {
        return 0;
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
        a.tenant_user_attributes = tenant_user_attributes;
        if (time_periods != null) {
            a.time_periods = new ArrayList<>();
            for (int i = 0; i < time_periods.size(); i++) {
                TimePeriods t = new TimePeriods();
                t.setPrice(time_periods.get(i).getPrice());
                t.setTime_from(time_periods.get(i).getTime_from());
                t.setTime_from_no_time_zone(time_periods.get(i).getTime_from_no_time_zone());
                a.time_periods.add(t);
            }
        }
        a.favorite_id = favorite_id;
        return a;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.organization_id);
        dest.writeString(this.address);
        dest.writeString(this.phone);
        dest.writeString(this.email);
        dest.writeByte(active ? (byte) 1 : (byte) 0);
        dest.writeString(this.created_at);
        dest.writeString(this.updated_at);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.name);
        dest.writeString(this.time_zone);
        dest.writeInt(this.plan_id);
        dest.writeString(this.agreement_number);
        dest.writeString(this.deleted_at);
        dest.writeByte(top_order ? (byte) 1 : (byte) 0);
        dest.writeString(this.mobile_stub_text);
        dest.writeString(this.sms_price);
        dest.writeString(this.online_reservation_price);
        dest.writeFloat(this.distance);
        dest.writeInt(this.bearing);
        dest.writeString(this.service_name);
        dest.writeTypedList(time_periods);
        dest.writeInt(this.favorite_id);
        dest.writeInt(this.type);
        dest.writeLong(rDate != null ? rDate.getTime() : -1);
        dest.writeParcelable(tenant_user_attributes, 0);
    }

    public static final Creator<WashService> CREATOR = new Creator<WashService>() {
        public WashService createFromParcel(Parcel source) {
            return new WashService(source);
        }

        public WashService[] newArray(int size) {
            return new WashService[size];
        }
    };
}
