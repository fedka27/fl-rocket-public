package wash.rocket.xor.rocketwash.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Field;

public class DialogItem implements Parcelable {

    public int id;
    public String uuid;
    public String title;
    public int type;

    public DialogItem() {
    }

    public DialogItem(int id, String uuid, String title, int type) {
        this.uuid = uuid;
        this.title = title;
        this.type = type;
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public DialogItem(Parcel in) {
        uuid = in.readString();
        title = in.readString();
        type = in.readInt();
        id = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(title);
        dest.writeInt(type);
        dest.writeInt(id);
    }

    public static final Creator<DialogItem> CREATOR = new Creator<DialogItem>() {
        public DialogItem createFromParcel(Parcel in) {
            return new DialogItem(in);
        }

        public DialogItem[] newArray(int size) {
            return new DialogItem[size];
        }
    };

    public void var_dump() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                System.out.println(fields[i].getName() + " - " + fields[i].get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
