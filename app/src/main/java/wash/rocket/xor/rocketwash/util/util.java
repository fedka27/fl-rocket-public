package wash.rocket.xor.rocketwash.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import wash.rocket.xor.rocketwash.R;

public class util {


    public static Date getDate(String str) {
        //2014-11-24T11:24:40.000Z
        //2015-10-09T18:45:00.000Z
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date getDateS(String str) {
        //2014-11-24T11:24:40.000Z
        //2015-10-09T18:45:00.000Z
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }



    //XXX to utils
    public static String SecondsToMS(long seconds) {
        //final int hours = (int) seconds / 3600;
        final int min = (int) (seconds % 3600) / 60;
        final int sec = (int) seconds % 60;
        //return String.format("%02d:%02d:%02d", hours, min, sec);
        return String.format("%02d:%02d", min, sec);
    }

    public static String SecondsToHMS(long seconds) {
        final int hours = (int) seconds / 3600;
        final int min = (int) (seconds % 3600) / 60;
        final int sec = (int) seconds % 60;
        return String.format("%02d:%02d:%02d", hours, min, sec);
        ///return String.format("%02d:%02d", min, sec);
    }


    public static String SecondsToHM(long seconds) {
        final int hours = (int) seconds / 3600;
        final int min = (int) (seconds % 3600) / 60;
        //final int sec = (int) seconds % 60;
        return String.format("%02d:%02d", hours, min);
        //return String.format("%02d:%02d", min, sec);
    }

    public static String millsToHM(long elapsedTime) {
        final long seconds = elapsedTime / 1000;
        final int hours = (int) seconds / 3600;
        final int min = (int) (seconds % 3600) / 60;
        final int sec = (int) seconds % 60;
        return String.format("%02d:%02d", hours, min);
        //return String.format("%02d:%02d", min, sec);
    }


    public static String dateToHM(Date date) {
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm");
        return ft.format(date);
    }

    public static String dateToddMM(Date date) {
        SimpleDateFormat ft = new SimpleDateFormat("dd/MM");
        return ft.format(date);
    }

    public static String dateToDMYHM(Date date) {
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        return ft.format(date);
    }

    public static String dateToZZ(Date date) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return ft.format(date);
    }

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static String adressShortFormat(String country, String state, String city, String street, String house, String entrance) {

        String adress = "";

        //if (!TextUtils.isEmpty(country))
        //	adress = country;

        if (!TextUtils.isEmpty(state))
            adress = adress + (TextUtils.isEmpty(adress) ? "" : ", ") + state;

        if (!TextUtils.isEmpty(city))
            adress = adress + (TextUtils.isEmpty(adress) ? "" : ", ") + city;

        if (!TextUtils.isEmpty(street))
            adress = adress + (TextUtils.isEmpty(adress) ? "" : ", ") + street;

        if (!TextUtils.isEmpty(house))
            adress = adress + (TextUtils.isEmpty(adress) ? "" : ", ") + house;

        //if (!TextUtils.isEmpty(entrance))
        //	adress = adress + (TextUtils.isEmpty(adress) ? "" : ", ") + "Рї. " + entrance;

        return adress;
    }

}
