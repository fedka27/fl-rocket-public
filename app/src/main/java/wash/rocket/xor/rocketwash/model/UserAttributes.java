package wash.rocket.xor.rocketwash.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class UserAttributes implements Parcelable {

    public static final Creator<UserAttributes> CREATOR = new Creator<UserAttributes>() {
        @Override
        public UserAttributes createFromParcel(Parcel in) {
            return new UserAttributes(in);
        }

        @Override
        public UserAttributes[] newArray(int size) {
            return new UserAttributes[size];
        }
    };
    @JsonField
    private int id;
    @JsonField
    private int discount = 0;
    @JsonField
    private int bonuses_percentage = 0;
    @JsonField
    private boolean disable_bonuses;
    @JsonField
    private Balance financial_center_user_money_balance;
    @JsonField
    private Balance financial_center_user_bonuses_balance;

    public UserAttributes() {

    }

    protected UserAttributes(Parcel in) {
        id = in.readInt();
        discount = in.readInt();
        bonuses_percentage = in.readInt();
        disable_bonuses = in.readByte() != 0;
        financial_center_user_money_balance = in.readParcelable(Balance.class.getClassLoader());
        financial_center_user_bonuses_balance = in.readParcelable(Balance.class.getClassLoader());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getBonuses_percentage() {
        return bonuses_percentage;
    }

    public void setBonuses_percentage(int bonuses_percentage) {
        this.bonuses_percentage = bonuses_percentage;
    }

    public boolean isDisable_bonuses() {
        return disable_bonuses;
    }

    public void setDisable_bonuses(boolean disable_bonuses) {
        this.disable_bonuses = disable_bonuses;
    }

    public Balance getFinancial_center_user_money_balance() {
        if (financial_center_user_bonuses_balance == null)
            financial_center_user_bonuses_balance = new Balance();
        return financial_center_user_money_balance;
    }

    public void setFinancial_center_user_money_balance(Balance financial_center_user_money_balance) {
        if (financial_center_user_money_balance == null)
            financial_center_user_money_balance = new Balance();
        this.financial_center_user_money_balance = financial_center_user_money_balance;
    }

    public Balance getFinancial_center_user_bonuses_balance() {
        return financial_center_user_bonuses_balance;
    }

    public void setFinancial_center_user_bonuses_balance(Balance financial_center_user_bonuses_balance) {
        this.financial_center_user_bonuses_balance = financial_center_user_bonuses_balance;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(discount);
        dest.writeInt(bonuses_percentage);
        dest.writeByte((byte) (disable_bonuses ? 1 : 0));
        dest.writeParcelable(financial_center_user_money_balance, flags);
        dest.writeParcelable(financial_center_user_bonuses_balance, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
