package wash.rocket.xor.rocketwash.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class CarsAttributes implements Parcelable {

    @JsonField
    private int id;

    @JsonField
    private String tag;

    @JsonField
    private int car_make_id;

    @JsonField
    private int car_model_id;

    @JsonField
    private String created_at;

    @JsonField
    private String updated_at;

    @JsonField
    private int contractor_id;

    @JsonField
    private int year;

    @JsonField
    private int service_location_id;

    @JsonField
    private String deleted_at;

    @JsonField
    private int organization_id;

    private String brandName;
    private String modelName;

    private int type;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getCar_make_id() {
        return car_make_id;
    }

    public void setCar_make_id(int car_make_id) {
        this.car_make_id = car_make_id;
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

    public int getContractor_id() {
        return contractor_id;
    }

    public void setContractor_id(int contractor_id) {
        this.contractor_id = contractor_id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getService_location_id() {
        return service_location_id;
    }

    public void setService_location_id(int service_location_id) {
        this.service_location_id = service_location_id;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public int getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(int organization_id) {
        this.organization_id = organization_id;
    }

    public int getCar_model_id() {
        return car_model_id;
    }

    public void setCar_model_id(int car_model_id) {
        this.car_model_id = car_model_id;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        String s;
        s = "{\n id=" + id;
        s = s + "\n tag=" + tag;
        s = s + "\n car_make_id=" + car_make_id;
        s = s + "\n car_model_id=" + car_model_id;
        s = s + "\n created_at=" + created_at;
        s = s + "\n updated_at=" + updated_at;
        s = s + "\n contractor_id=" + contractor_id;
        s = s + "\n year=" + year;
        s = s + "\n service_location_id=" + service_location_id;
        s = s + "\n deleted_at=" + deleted_at;
        s = s + "\n organization_id=" + organization_id;
        s = s + "\n}";
        return s;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(tag);
        dest.writeInt(car_make_id);
        dest.writeInt(car_model_id);
        dest.writeString(created_at);
        dest.writeString(updated_at);
        dest.writeInt(contractor_id);
        dest.writeInt(year);
        dest.writeInt(service_location_id);
        dest.writeString(deleted_at);
        dest.writeInt(organization_id);
        dest.writeString(brandName);
        dest.writeString(modelName);
    }

    public CarsAttributes() {
    }

    public CarsAttributes(Parcel in) {

        id = in.readInt();
        tag = in.readString();
        car_make_id = in.readInt();
        car_model_id = in.readInt();
        created_at = in.readString();
        updated_at = in.readString();
        contractor_id = in.readInt();
        year = in.readInt();
        service_location_id = in.readInt();
        deleted_at = in.readString();
        organization_id = in.readInt();
        brandName = in.readString();
        modelName = in.readString();

    }

    public static final Creator<CarsAttributes> CREATOR = new Creator<CarsAttributes>() {
        public CarsAttributes createFromParcel(Parcel in) {
            return new CarsAttributes(in);
        }

        public CarsAttributes[] newArray(int size) {
            return new CarsAttributes[size];
        }
    };


    public CarsAttributes copy() {
        CarsAttributes c = new CarsAttributes();
        c.id = id;
        c.tag = tag;
        c.car_make_id = car_make_id;
        c.car_model_id = car_model_id;
        c.created_at = created_at;
        c.updated_at = updated_at;
        c.contractor_id = contractor_id;
        c.year = year;
        c.service_location_id = service_location_id;
        c.deleted_at = deleted_at;
        c.organization_id = organization_id;
        c.brandName = brandName;
        c.modelName = modelName;
        return c;
    }

    public Bundle getBundle(int index) {
        Bundle dest = new Bundle();

        dest.putInt("id_" + index, id);
        dest.putString("tag_" + index, tag);
        dest.putInt("car_make_id_" + index, car_make_id);
        dest.putInt("car_model_id_" + index, car_model_id);
        dest.putString("created_at_" + index, created_at);
        dest.putString("updated_at_" + index, updated_at);
        dest.putInt("contractor_id_" + index, contractor_id);
        dest.putInt("year_" + index, year);
        dest.putInt("service_location_id_" + index, service_location_id);
        dest.putString("deleted_at_" + index, deleted_at);
        dest.putInt("organization_id_" + index, organization_id);
        dest.putString("brandName_" + index, brandName);
        dest.putString("modelName_" + index, modelName);

        return dest;
    }

    public static CarsAttributes fromBundle(Bundle data, int index) {
        CarsAttributes c = new CarsAttributes();

        c.id = data.getInt("id_" + index);
        c.tag = data.getString("tag_" + index);
        c.car_make_id = data.getInt("car_make_id_" + index);
        c.car_model_id = data.getInt("car_model_id_" + index);
        c.created_at = data.getString("created_at_" + index);
        c.updated_at = data.getString("updated_at_" + index);
        c.contractor_id = data.getInt("contractor_id_" + index);
        c.year = data.getInt("year_" + index);
        c.service_location_id = data.getInt("service_location_id_" + index);
        c.deleted_at = data.getString("deleted_at_" + index);
        c.organization_id = data.getInt("organization_id_" + index);
        c.brandName = data.getString("brandName_" + index);
        c.modelName = data.getString("modelName_" + index);

        return c;
    }

}
