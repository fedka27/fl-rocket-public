package wash.rocket.xor.rocketwash.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import com.bluelinelabs.logansquare.LoganSquare;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import wash.rocket.xor.rocketwash.model.Profile;
import wash.rocket.xor.rocketwash.model.ProfileResult;


public class Preferences {
    private final static String REGISTER_STEP = "prefeence.register.step";

    private final static String LAST_USED_PHONE = "LastUsedPhone";
    private final static String LAST_COUNTRY_ID = "country.id";
    private final static String LAST_PHONE_CODE = "phone.code";
    private final static String LAT = "getLastKnowLocationLatitude";
    private final static String LON = "getLastKnowLocationLongitude";
    private final static String LAST_TIME_CLICK = "last.time.click";
    private final static String LAST_TIME_CLICK_SMS = "last.time.click.sms";
    private final static String SESSION_ID = "sessionid";

    Context mcontext;

    public Preferences(Context context) {
        mcontext = context;
    }

    public void clear() {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();
        editor.remove("LastUsedPhone");
        editor.remove("host");
        editor.commit();
    }

    public void saveLastUsedPhone(String value) {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();
        editor.putString(LAST_USED_PHONE, value);
        editor.commit();
    }

    public String getLastUsedPhone() {
        SharedPreferences prefs = this.getSharedPreferences(mcontext);
        return prefs.getString(LAST_USED_PHONE, "");
    }

    public String getLastUsedPhoneCode() {
        SharedPreferences prefs = this.getSharedPreferences(mcontext);
        return prefs.getString(LAST_PHONE_CODE, "");
    }

    public void setLastUsedPhoneCode(String value) {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();
        editor.putString(LAST_PHONE_CODE, value);
        editor.commit();
    }

    public int getLastCountryId() {
        SharedPreferences prefs = this.getSharedPreferences(mcontext);
        return prefs.getInt(LAST_COUNTRY_ID, 0);
    }

    public void setLastCountryId(int value) {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();
        editor.putInt(LAST_COUNTRY_ID, value);
        editor.commit();
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.PREFERENCE_SHARED, Context.MODE_PRIVATE);
    }

    public void saveHost(String value) {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();
        editor.putString("host", value);
        editor.commit();
    }

    public Location getLastKnowLocation() {
        Location l = null;

        SharedPreferences prefs = this.getSharedPreferences(mcontext);
        double lat = Double.valueOf(prefs.getString(LAT, "0"));
        double lon = Double.valueOf(prefs.getString(LON, "0"));

        System.out.println("getLastKnowLocation = " + lat + "; " + lon);

        if (lat != 0 && lon != 0) {
            l = new Location("");
            l.setLatitude(lat);
            l.setLongitude(lon);
        }
        return l;
    }

    public void setLastKnowLocation(Location l) {
        if (l == null)
            return;

        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();
        editor.putString(LAT, String.valueOf(l.getLatitude()));
        editor.putString(LON, String.valueOf(l.getLongitude()));
        editor.commit();
    }

    public void setCurrentPrice(int Price) {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();
        editor.putInt("CurrentPrice", Price);
        editor.commit();
    }

    public int getCurrentPrice() {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        return prefs.getInt("CurrentPrice", 0);
    }

    public boolean getShowHintRefresh() {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        return prefs.getBoolean("ShowHintRefresh1", false);
    }

    public void setShowHintRefresh(boolean value) {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();
        editor.putBoolean("ShowHintRefresh1", value);
        editor.commit();
    }

    public void setDefLocality(String str) {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();
        editor.putString("defaultlocality", str);
        editor.commit();
    }

    public String getDefLocality() {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        return prefs.getString("defaultlocality", "");
    }

    public int getRegisterStep() {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        return prefs.getInt(REGISTER_STEP, 0);
    }

    public void setRegisterStep(int value) {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();
        editor.putInt(REGISTER_STEP, value);
        editor.commit();
    }

    public long getLastTimeClick() {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        return prefs.getLong(LAST_TIME_CLICK, -1);
    }

    public void setLastTimeClick(long value) {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();
        editor.putLong(LAST_TIME_CLICK, value);
        editor.commit();
    }

    public long getLastTimeClickSMS() {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        return prefs.getLong(LAST_TIME_CLICK_SMS, -1);
    }

    public void setLastTimeClickSMS(long value) {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();
        editor.putLong(LAST_TIME_CLICK_SMS, value);
        editor.commit();
    }

    public void setSessionID(String value) {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();
        editor.putString(SESSION_ID, value);
        editor.commit();
    }

    public String getSessionID() {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        return prefs.getString(SESSION_ID, "");
    }

    public Profile getProfile() {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        String p = prefs.getString("profile", "");

        if (TextUtils.isEmpty(p))
            return null;

        //ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

        //EmptyUserResult


        ProfileResult res = null;
        try {
            //res = mapper.readValue(p, ProfileResult.class);
            res = LoganSquare.parse(p, ProfileResult.class);

            if (res != null && res.getData() != null) {
                res.getData().setString(p);
                return res.getData();
            } else return null;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setProfile(Profile profile) {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();

        if (profile != null)
            editor.putString("profile", profile.getString());
        else
            editor.putString("profile", "");

        editor.commit();
    }

    public void setUseCar(int value) {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();
        editor.putInt("car", value);
        editor.commit();
    }


    public void setCarModelId(int value) {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();
        editor.putInt("carModel", value);
        editor.commit();
    }

    public int getCarModelId() {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        return prefs.getInt("carModel", 0);
    }

    public String getCarName() {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        return prefs.getString("CarName", "");
    }

    public void setCarNum(String value) {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();
        editor.putString("CarNum", value);
        editor.commit();
    }

    public String getCarNum() {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        return prefs.getString("CarNum", "");
    }

    public void setCarName(String value) {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();
        editor.putString("CarName", value);
        editor.commit();
    }

    public int getUseCar() {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        return prefs.getInt("car", 0);
    }

    public static void savePreferencesBundle(SharedPreferences.Editor editor, String key, Bundle preferences) {
        Set<String> keySet = preferences.keySet();
        Iterator<String> it = keySet.iterator();
        String prefKeyPrefix = key + SAVED_PREFS_BUNDLE_KEY_SEPARATOR;

        while (it.hasNext()) {
            String bundleKey = it.next();
            Object o = preferences.get(bundleKey);
            if (o == null) {
                editor.remove(prefKeyPrefix + bundleKey);
            } else if (o instanceof Integer) {
                editor.putInt(prefKeyPrefix + bundleKey, (Integer) o);
            } else if (o instanceof Long) {
                editor.putLong(prefKeyPrefix + bundleKey, (Long) o);
            } else if (o instanceof Boolean) {
                editor.putBoolean(prefKeyPrefix + bundleKey, (Boolean) o);
            } else if (o instanceof CharSequence) {
                editor.putString(prefKeyPrefix + bundleKey, ((CharSequence) o).toString());
            } else if (o instanceof Bundle) {
                savePreferencesBundle(editor, prefKeyPrefix + bundleKey, ((Bundle) o));
            } else if (o instanceof Parcelable) {

            }
        }
    }

    /**
     * Load a Bundle object from SharedPreferences.
     * (that was previously stored using savePreferencesBundle())
     * <p/>
     * NOTE: The editor must be writable, and this function does not commit.
     *
     * @param sharedPreferences SharedPreferences
     * @param key SharedPreferences key under which to store the bundle data. Note this key must
     * not contain '§§' as it's used as a delimiter
     * @return bundle loaded from SharedPreferences
     */
    private static final String SAVED_PREFS_BUNDLE_KEY_SEPARATOR = "§§";

    public static Bundle loadPreferencesBundle(SharedPreferences sharedPreferences, String key) {


        Bundle bundle = new Bundle();
        Map<String, ?> all = sharedPreferences.getAll();
        Iterator<String> it = all.keySet().iterator();
        String prefKeyPrefix = key + SAVED_PREFS_BUNDLE_KEY_SEPARATOR;
        Set<String> subBundleKeys = new HashSet<String>();

        while (it.hasNext()) {

            String prefKey = it.next();

            if (prefKey.startsWith(prefKeyPrefix)) {
                String bundleKey = StringUtils.removeStart(prefKey, prefKeyPrefix);

                if (!bundleKey.contains(SAVED_PREFS_BUNDLE_KEY_SEPARATOR)) {

                    Object o = all.get(prefKey);
                    if (o == null) {
                        // Ignore null keys
                    } else if (o instanceof Integer) {
                        bundle.putInt(bundleKey, (Integer) o);
                    } else if (o instanceof Long) {
                        bundle.putLong(bundleKey, (Long) o);
                    } else if (o instanceof Boolean) {
                        bundle.putBoolean(bundleKey, (Boolean) o);
                    } else if (o instanceof CharSequence) {
                        bundle.putString(bundleKey, ((CharSequence) o).toString());
                    }
                } else {
                    // Key is for a sub bundle
                    String subBundleKey = StringUtils.substringBefore(bundleKey, SAVED_PREFS_BUNDLE_KEY_SEPARATOR);
                    subBundleKeys.add(subBundleKey);
                }
            } else {
                // Key is not related to this bundle.
            }
        }

        // Recursively process the sub-bundles
        for (String subBundleKey : subBundleKeys) {
            Bundle subBundle = loadPreferencesBundle(sharedPreferences, prefKeyPrefix + subBundleKey);
            bundle.putBundle(subBundleKey, subBundle);
        }


        return bundle;
    }

    public void setShowDialogGps(boolean checked) {

        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();
        editor.putBoolean("ShowDialogGps", checked);
        editor.commit();
    }

    public boolean getShowDialogGps() {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        return prefs.getBoolean("ShowDialogGps", true);
    }


    public void setRegistered(boolean checked) {

        SharedPreferences prefs = getSharedPreferences(mcontext);
        Editor editor = prefs.edit();
        editor.putBoolean("registered", checked);
        editor.commit();
    }

    public boolean getRegistered() {
        SharedPreferences prefs = getSharedPreferences(mcontext);
        return prefs.getBoolean("registered", true);
    }

}
