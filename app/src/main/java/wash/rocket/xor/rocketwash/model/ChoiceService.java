package wash.rocket.xor.rocketwash.model;

/*
 {
      "id": 7612,
      "name": "1.Пылесос ковриков",
      "price": 60,
      "duration": 15
    },
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonObject
public class ChoiceService implements Parcelable {


    @JsonProperty("id")
    @JsonField
    private int id;

    @JsonField
    @JsonProperty("name")
    private String name;

    @JsonField
    @JsonProperty("price")
    private int price;

    @JsonField
    @JsonProperty("duration")
    private int duration;

    private int type;
    private int check;

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public boolean isCheck() {
        return check == 1;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeInt(duration);
        dest.writeInt(type);
        dest.writeInt(check);
    }

    public ChoiceService() {

    }

    public ChoiceService(Parcel in) {
        id = in.readInt();
        name = in.readString();
        price = in.readInt();
        duration = in.readInt();
        type = in.readInt();
        check = in.readInt();
    }

    public static final Creator<ChoiceService> CREATOR = new Creator<ChoiceService>() {
        public ChoiceService createFromParcel(Parcel in) {
            return new ChoiceService(in);
        }

        public ChoiceService[] newArray(int size) {
            return new ChoiceService[size];
        }
    };

    public ChoiceService getClone() {
        ChoiceService s = new ChoiceService();
        s.id = this.id;
        s.name = this.name;
        s.price = this.price;
        s.duration = this.duration;
        s.type = this.type;
        s.check = this.check;
        return s;
    }
}
