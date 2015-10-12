package wash.rocket.xor.rocketwash.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public class Profile implements Parcelable {
    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("cars_attributes")
    private List<CarsAttributes> cars_attributes;

    @JsonProperty("email")
    private String email;

    @JsonProperty("created_at")
    private String created_at;

    @JsonProperty("updated_at")
    private String updated_at;

    @JsonProperty("good_user")
    private String good_user;

    @JsonProperty("session_id")
    private String session_id;

    @JsonProperty("service_location_id")
    //@JsonInclude(JsonInclude.Include.NON_EMPTY)
    private int service_location_id;

    @JsonProperty("car_type_id")
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    private int car_type_id;

    @JsonProperty("sex")
    //  @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sex;

    @JsonProperty("date_of_birth")
    private String date_of_birth;

    //  @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("category")
    private String category;

    @JsonProperty("service_location_lane_id")
    //  @JsonInclude(JsonInclude.Include.NON_NULL)
    private int service_location_lane_id;

    @JsonProperty("deleted_at")
    private String deleted_at;

    @JsonProperty("discount")
    //  @JsonInclude(JsonInclude.Include.NON_NULL)
    private int discount;

    @JsonProperty("address")
    private String address;

    @JsonProperty("tin")
    private String tin;

    @JsonProperty("kpp")
    private String kpp;

    @JsonProperty("account_number")
    private String account_number;

    @JsonProperty("bic")
    private String bic;

    @JsonProperty("discount_card_number")
    private String discount_card_number;

    @JsonProperty("original_user_id")
    //  @JsonInclude(JsonInclude.Include.NON_NULL)
    private int original_user_id;

    @JsonProperty("superuser")
    //  @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean superuser;

    @JsonProperty("full_name")
    private String full_name;

    @JsonProperty("disable_sms")
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean disable_sms;

    @JsonProperty("organization_id")
    //   @JsonInclude(JsonInclude.Include.NON_NULL)
    private int organization_id;

    @JsonProperty("user_category_id")
    //   @JsonInclude(JsonInclude.Include.NON_NULL)
    private int user_category_id;

    @JsonProperty("user_category_updated_at")
    private String user_category_updated_at;

    @JsonProperty("job_id")
    private int job_id;

    @JsonProperty("employee_status")
    private String employee_status;

    @JsonProperty("latest_pin_sms_sent_at")
    private String latest_pin_sms_sent_at;

    @JsonProperty("phone_verified")
    private boolean phone_verified;

    private String string;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<CarsAttributes> getCars_attributes() {
        return cars_attributes;
    }

    public void setCars_attributes(List<CarsAttributes> cars_attributes) {
        this.cars_attributes = cars_attributes;
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

    public String getGood_user() {
        return good_user;
    }

    public void setGood_user(String good_user) {
        this.good_user = good_user;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public int getService_location_id() {
        return service_location_id;
    }

    public void setService_location_id(int service_location_id) {
        this.service_location_id = service_location_id;
    }

    public int getCar_type_id() {
        return car_type_id;
    }

    public void setCar_type_id(int car_type_id) {
        this.car_type_id = car_type_id;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getService_location_lane_id() {
        return service_location_lane_id;
    }

    public void setService_location_lane_id(int service_location_lane_id) {
        this.service_location_lane_id = service_location_lane_id;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getDiscount_card_number() {
        return discount_card_number;
    }

    public void setDiscount_card_number(String discount_card_number) {
        this.discount_card_number = discount_card_number;
    }

    public int getOriginal_user_id() {
        return original_user_id;
    }

    public void setOriginal_user_id(int original_user_id) {
        this.original_user_id = original_user_id;
    }

    public boolean isSuperuser() {
        return superuser;
    }

    public void setSuperuser(boolean superuser) {
        this.superuser = superuser;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public boolean isDisable_sms() {
        return disable_sms;
    }

    public void setDisable_sms(boolean disable_sms) {
        this.disable_sms = disable_sms;
    }

    public int getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(int organization_id) {
        this.organization_id = organization_id;
    }

    public int getUser_category_id() {
        return user_category_id;
    }

    public void setUser_category_id(int user_category_id) {
        this.user_category_id = user_category_id;
    }

    public String getUser_category_updated_at() {
        return user_category_updated_at;
    }

    public void setUser_category_updated_at(String user_category_updated_at) {
        this.user_category_updated_at = user_category_updated_at;
    }

    public int getJob_id() {
        return job_id;
    }

    public void setJob_id(int job_id) {
        this.job_id = job_id;
    }

    public String getEmployee_status() {
        return employee_status;
    }

    public void setEmployee_status(String employee_status) {
        this.employee_status = employee_status;
    }

    public String getLatest_pin_sms_sent_at() {
        return latest_pin_sms_sent_at;
    }

    public void setLatest_pin_sms_sent_at(String latest_pin_sms_sent_at) {
        this.latest_pin_sms_sent_at = latest_pin_sms_sent_at;
    }

    public boolean isPhone_verified() {
        return phone_verified;
    }

    public void setPhone_verified(boolean phone_verified) {
        this.phone_verified = phone_verified;
    }

    @Override
    public String toString() {
        return "";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeList(cars_attributes);
        dest.writeString(email);
        dest.writeString(created_at);
        dest.writeString(updated_at);
        dest.writeString(good_user);
        dest.writeString(session_id);
        dest.writeInt(service_location_id);
        dest.writeInt(car_type_id);
        dest.writeString(sex);
        dest.writeString(date_of_birth);
        dest.writeString(category);
        dest.writeInt(service_location_lane_id);
        dest.writeString(deleted_at);
        dest.writeInt(discount);
        dest.writeString(address);
        dest.writeString(tin);
        dest.writeString(kpp);
        dest.writeString(account_number);
        dest.writeString(bic);
        dest.writeString(discount_card_number);
        dest.writeInt(original_user_id);
        dest.writeInt(superuser ? 1 : 0);
        dest.writeString(full_name);
        dest.writeInt(disable_sms ? 1 : 0);
        dest.writeInt(organization_id);
        dest.writeInt(user_category_id);
        dest.writeString(user_category_updated_at);
        dest.writeInt(job_id);
        dest.writeString(employee_status);
        dest.writeString(latest_pin_sms_sent_at);
        dest.writeInt(phone_verified ? 1 : 0);
        dest.writeString(string);
    }

    public Profile() {

    }

    public Profile(Parcel in) {
        id = in.readInt();
        name = in.readString();
        phone = in.readString();
        cars_attributes = new ArrayList<>();
        in.readList(cars_attributes, CarsAttributes.class.getClassLoader());
        email = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
        good_user = in.readString();
        session_id = in.readString();
        service_location_id = in.readInt();
        car_type_id = in.readInt();
        sex = in.readString();
        date_of_birth = in.readString();
        category = in.readString();
        service_location_lane_id = in.readInt();
        deleted_at = in.readString();
        discount = in.readInt();
        address = in.readString();
        tin = in.readString();
        kpp = in.readString();
        account_number = in.readString();
        bic = in.readString();
        discount_card_number = in.readString();
        original_user_id = in.readInt();
        superuser = in.readInt() == 1;
        full_name = in.readString();
        disable_sms = in.readInt() == 1;
        organization_id = in.readInt();
        user_category_id = in.readInt();
        user_category_updated_at = in.readString();
        job_id = in.readInt();
        employee_status = in.readString();
        latest_pin_sms_sent_at = in.readString();
        phone_verified = in.readInt() == 1;
        string = in.readString();
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    public Bundle getBumdle() {
        Bundle dest = new Bundle();

        dest.putInt("id", id);
        dest.putString("name", name);
        dest.putString("phone", phone);
        dest.putParcelableArrayList("cars_attributes", (ArrayList<? extends Parcelable>) cars_attributes);
        dest.putString("email", email);
        dest.putString("created_at", created_at);
        dest.putString("updated_at", updated_at);
        dest.putString("good_user", good_user);
        dest.putString("session_id", session_id);
        dest.putInt("service_location_id", service_location_id);
        dest.putInt("car_type_id", car_type_id);
        dest.putString("sex", sex);
        dest.putString("date_of_birth", date_of_birth);
        dest.putString("category", category);
        dest.putInt("service_location_lane_id", service_location_lane_id);
        dest.putString("deleted_at", deleted_at);
        dest.putInt("discount", discount);
        dest.putString("address", address);
        dest.putString("tin", tin);
        dest.putString("kpp", kpp);
        dest.putString("account_number", account_number);
        dest.putString("bic", bic);
        dest.putString("discount_card_number", discount_card_number);
        dest.putInt("original_user_id", original_user_id);
        dest.putInt("superuser ? 1 : 0", superuser ? 1 : 0);
        dest.putString("full_name", full_name);
        dest.putInt("disable_sms ? 1 : 0", disable_sms ? 1 : 0);
        dest.putInt("organization_id", organization_id);
        dest.putInt("user_category_id", user_category_id);
        dest.putString("user_category_updated_at", user_category_updated_at);
        dest.putInt("job_id", job_id);
        dest.putString("employee_status", employee_status);
        dest.putString("latest_pin_sms_sent_at", latest_pin_sms_sent_at);
        dest.putInt("phone_verified ? 1 : 0", phone_verified ? 1 : 0);
        dest.putString("string", string);

        return dest;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
