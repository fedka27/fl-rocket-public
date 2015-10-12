package wash.rocket.xor.rocketwash.requests;

import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.util.IOUtils;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

import wash.rocket.xor.rocketwash.model.ReverseGeocoding;
import wash.rocket.xor.rocketwash.util.util;

public class MapReverceGeocodingRequest extends GoogleHttpClientSpiceRequest<ReverseGeocoding> {

    private final String TAG = "MapReverceGeocodingRequest";

    private String baseUrl;
    private double lat;
    private double lon;

    public MapReverceGeocodingRequest(double lat, double lon) {
        super(ReverseGeocoding.class);
        this.baseUrl = "http://test.rocketwash.me/v2/service_locations/nearest";
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public ReverseGeocoding loadDataFromNetwork() throws IOException {

        String uri = Uri.parse(baseUrl)
                .buildUpon()
                .appendQueryParameter("latitude", "" + lat)
                .appendQueryParameter("longitude", "" + lon)
                .build().toString();

        Log.d("loadDataFromNetwork", "uri = " + uri);

        /*
        HttpRequest request = getHttpRequestFactory().buildGetRequest(new GenericUrl(uri));
        String result = "";
        InputStream content = request.execute().getContent();
        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }*/


        String state = "";
        String city = "";
        String street = "";
        String house_number = "";
        String country = "";
        String description = "";

        double r = getDist(150);
        String url_y = String.format(Locale.ENGLISH, "http://geocode-maps.yandex.ru/1.x/?geocode=%f,%f&format=json&sco=latlong&results=10&kind=house&spn=%.07f,%.07f&rspn=1", lat, lon, r, r);
        Log.d(TAG, "url_y = " + url_y);

        String s = httpRes(url_y);
        JSONObject json_ret_y = null;
        try {
            json_ret_y = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "ya res = " + json_ret_y);

        ArrayList<ReverseGeocoding> list = new ArrayList<ReverseGeocoding>();

        if (json_ret_y != null) {
            //getData(json_ret_y, "Premise");
            //JSONObject responce = json_ret_y.optJSONObject("response");
            //JSONObject metaDataProperty = geoObject.optJSONObject("metaDataProperty");

            JSONObject res = getData(json_ret_y, "GeoObjectCollection");
            if (res != null) {
                JSONArray arr = res.optJSONArray("featureMember");
                if (arr != null) {
                    //Log.d(TAG, " yandex result ");

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject item = arr.optJSONObject(i);
                        if (item != null) {
                            JSONObject country_ = getData(item, "Country");
                            JSONObject premise_ = getData(item, "Premise");
                            JSONObject locality = getData(item, "Locality");
                            JSONObject thoroughfare = getData(item, "Thoroughfare");
                            JSONObject administrativeArea = getData(item, "AdministrativeArea");

                            if (country_ != null) {
                                if (administrativeArea != null) {
                                    //String str = administrativeArea.optString("AdministrativeAreaName");
                                    state = administrativeArea.optString("AdministrativeAreaName");
                                }

                                if (locality != null)
                                    city = locality.optString("LocalityName");

                                if (thoroughfare != null)
                                    street = thoroughfare.optString("ThoroughfareName");

                                if (premise_ != null)
                                    house_number = premise_.optString("PremiseNumber");

                                if (country_ != null) {
                                    country = country_.optString("CountryName");
                                    description = country_.optString("AddressLine");
                                }

                                JSONObject point = getData(item, "Point");

                                ReverseGeocoding p = new ReverseGeocoding();
                                p.setCity(city);
                                p.setStreet(street);
                                p.setHouse(house_number);
                                p.setCountry(country);
                                p.setDescription(description);
                                p.setShortAddress(util.adressShortFormat(country, state, city, street, house_number, ""));

                                if (point != null) {
                                    String pos = point.optString("pos");
                                    String a[] = pos.split(" ");
                                    if (a != null && a.length > 0) {
                                        p.setLatitude(Double.valueOf(a[1]));
                                        p.setLongitude(Double.valueOf(a[0]));
                                        Location location = new Location("");
                                        location.setLatitude(lat);
                                        location.setLongitude(lon);

                                        float[] results = new float[1];
                                        results[0] = 0;
                                        Location.distanceBetween(p.getLatitude(), p.getLongitude(), location.getLatitude(), location.getLongitude(),
                                                results);
                                        p.setDistance(results[0]);
                                    }
                                }
                                list.add(p);
                            }
                        }
                    }
                }
            }

            // отсортируем по удаленности
            Collections.sort(list, new LocationSort());
            // выдергиваем ближайший дом

            ReverseGeocoding p = null;

            if (list.size() > 0) {
                p = list.get(0);
                return p;
            }
        }

        //http://open.mapquestapi.com/nominatim/v1/reverse.php?format=json&json_callback=renderExampleThreeResults&lat=55.643044&lon=37.600083

        Locale locale = Locale.getDefault();
        String url = "http://nominatim.openstreetmap.org/" + "reverse?" + "format=json" + "&accept-language=" + locale.getLanguage()
                + "&addressdetails=1" + "&zoom=18" + "&lat=" + lat + "&lon=" + lon;

        s = httpRes(url);

        JSONObject json_ret = null;
        try {
            json_ret = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (json_ret != null) {
            Log.d(TAG, "OSM result");
            try {
                state = getState(json_ret);
            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }

            try {
                city = getCity(json_ret);
            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }

            try {
                street = getStreet(json_ret);
            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }

            try {
                house_number = getHouse_number(json_ret);
            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }

            country = getCountry(json_ret);
            description = util.adressShortFormat(country, state, city, street, house_number, "");

            ReverseGeocoding p = new ReverseGeocoding();
            p.setCity(city);
            p.setStreet(street);
            p.setHouse(house_number);
            p.setCountry(country);
            p.setDescription(description);
            p.setShortAddress(description);
            p.setLatitude(getLat(json_ret));
            p.setLongitude(getLon(json_ret));

            return p;
        }

        return new ReverseGeocoding();
    }


    private String httpRes(String uri) throws IOException {
        HttpRequest request = getHttpRequestFactory().buildGetRequest(new GenericUrl(uri));
        String result = "";
        InputStream content = request.execute().getContent();
        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }


        return result;
    }


    public String getHouse_number(JSONObject jResult) throws JSONException, UnsupportedEncodingException {
        JSONObject jAddress = jResult.getJSONObject("address");
        if (jAddress.has("house_number")) {
            return jAddress.getString("house_number");
        } else
            return "";
    }

    public String getState(JSONObject jResult) throws JSONException, UnsupportedEncodingException {
        JSONObject jAddress = jResult.getJSONObject("address");
        String state = jAddress.optString("state");
        if (!TextUtils.isEmpty(state)
                && (state.toLowerCase().trim().equalsIgnoreCase("москва") || state.toLowerCase().trim().equalsIgnoreCase("санкт-петербург"))) {
            return "";
        } else
            return state;

    }

    public String getCity(JSONObject jResult) throws JSONException, UnsupportedEncodingException {
        JSONObject jAddress = jResult.getJSONObject("address");

        String ret = "";

        if (jAddress.has("state")) {
            String state = jAddress.optString("state");
            if (!TextUtils.isEmpty(state)
                    && (state.toLowerCase().trim().equalsIgnoreCase("москва") || state.toLowerCase().trim()
                    .equalsIgnoreCase("санкт-петербург"))) {
                ret = state;
            } else if (jAddress.has("city")) {
                ret = jAddress.getString("city");
            } else if (jAddress.has("town")) {
                ret = jAddress.getString("town");
            } else if (jAddress.has("village")) {
                ret = jAddress.getString("village");
            }
        } else {
            String state = jAddress.optString("address");
            if (!TextUtils.isEmpty(state)
                    && (state.toLowerCase().trim().equalsIgnoreCase("москва") || state.toLowerCase().trim()
                    .equalsIgnoreCase("санкт-петербург"))) {
                ret = state;
            } else if (jAddress.has("city")) {
                ret = jAddress.getString("city");
            } else if (jAddress.has("town")) {
                ret = jAddress.getString("town");
            } else if (jAddress.has("village")) {
                ret = jAddress.getString("village");
            }
        }

        return ret.replace("городское поселение", "").trim();
    }

    public String getStreet(JSONObject jResult) throws JSONException, UnsupportedEncodingException {
        JSONObject jAddress = jResult.getJSONObject("address");
        if (jAddress.has("road")) {
            return jAddress.getString("road");
        } else if (jAddress.has("residential")) {
            // для некоторых районов москвы
            return jAddress.getString("residential");
        } else if (jAddress.has("pedestrian")) {
            // для некоторых районов москвы
            return jAddress.getString("pedestrian");
        } else if (jAddress.has("footway")) {
            // для некоторых районов москвы
            return jAddress.getString("footway");
        } else
            return "";
    }

    public String getCountry(JSONObject jResult) {

        JSONObject jAddress = null;
        try {
            jAddress = jResult.getJSONObject("address");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jAddress != null && jAddress.has("country")) {
            String c = "";

            try {
                c = jAddress.getString("country");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return c;
        } else
            return "";
    }

    public JSONObject getData(JSONObject jsonObject, String key) {
        if (jsonObject != null) {
            Iterator jsonObjectKeys = jsonObject.keys();
            while (jsonObjectKeys.hasNext()) {
                String currentDynamicKey = (String) jsonObjectKeys.next();
                JSONObject currentDynamicValue = jsonObject.optJSONObject(currentDynamicKey);
                JSONArray currentDynamicArray = null;

                if (currentDynamicValue == null)
                    currentDynamicArray = jsonObject.optJSONArray(currentDynamicKey);

                if (currentDynamicArray != null) {
                    //System.out.println("currentDynamicKey (arr)  = " + currentDynamicKey);

                    for (int i = 0; i < currentDynamicArray.length(); i++) {
                        JSONObject d = getData(currentDynamicArray.optJSONObject(i), key);
                        if (d != null)
                            return d;
                    }
                }
                //else
                //System.out.println("currentDynamicKey  = " + currentDynamicKey);

                if (key.equals(currentDynamicKey))
                    return currentDynamicValue;

                JSONObject d = getData(currentDynamicValue, key);
                if (d != null)
                    return d;
            }
        }

        return null;
    }

    private double getLat(JSONObject jResult) {
        return jResult.optDouble("lat");
    }

    private double getLon(JSONObject jResult) {
        return jResult.optDouble("lon");
    }

    private double getDist(float rad) {
        return rad * 180 / (Math.PI * 6371000.01f);
    }

    public static class LocationSort implements Comparator<ReverseGeocoding> {
        @Override
        public int compare(ReverseGeocoding arg0, ReverseGeocoding arg1) {
            return (int) (arg0.getDistance() - arg1.getDistance()) * 10;
        }
    }


}
