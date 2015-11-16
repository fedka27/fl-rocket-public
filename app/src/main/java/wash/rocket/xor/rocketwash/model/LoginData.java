package wash.rocket.xor.rocketwash.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

@JsonObject
public class LoginData {

    @JsonField
    private Profile profile;
    @JsonField
    private String session_id;
    @JsonField
    private List<Integer> price_range;
    @JsonField
    private String result;

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public List<Integer> getPrice_range() {
        return price_range;
    }

    public void setPrice_range(List<Integer> price_range) {
        this.price_range = price_range;
    }

    @Override
    public String toString() {
        String s = "";
        s = "{\nsession_id = " + session_id;
        s = s + "\nresult = " + result;
        s = s + "\nprofile = " + (profile == null ? "null" : profile.toString());
        s = s + "\n}\n";
        return s;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
