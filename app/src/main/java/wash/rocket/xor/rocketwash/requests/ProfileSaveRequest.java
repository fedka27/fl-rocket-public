package wash.rocket.xor.rocketwash.requests;

import android.net.Uri;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.util.IOUtils;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import wash.rocket.xor.rocketwash.model.CarsAttributes;
import wash.rocket.xor.rocketwash.model.Profile;
import wash.rocket.xor.rocketwash.model.ProfileResult;
import wash.rocket.xor.rocketwash.util.Constants;

public class ProfileSaveRequest extends GoogleHttpClientSpiceRequest<ProfileResult> {

    private String profileUrl;
    private String carUrl;
    private String carUrlID;
    private String session_id;
    private Profile profile;

    public ProfileSaveRequest(String session_id, Profile profile) {
        super(ProfileResult.class);
        this.profileUrl = Constants.URL + "profile";
        this.carUrl = Constants.URL + "cars";
        this.carUrlID = Constants.URL + "cars/id";
        this.session_id = session_id;
        this.profile = profile;
    }

    @Override
    public ProfileResult loadDataFromNetwork() throws IOException {
        // Ln.d("Call web service " + baseUrl);


        String uri = Uri.parse(profileUrl)
                .buildUpon()
                .appendQueryParameter("user[name]", "" + profile.getName())
                .build().toString();

        HttpHeaders header = new HttpHeaders();
        header.set("X-Rocketwash-Session-Id", session_id);

        HttpRequest request = getHttpRequestFactory().buildPutRequest(new GenericUrl(uri), null).setHeaders(header);

        String result = "";
        InputStream content = request.execute().getContent();


        if (profile.getCars_attributes() != null)
            for (int i = 0; i < profile.getCars_attributes().size(); i++) {
                CarsAttributes c = profile.getCars_attributes().get(i);
                if (c.getId() == 0 && c.getType() == 0) { // Add

                    if (c.getCar_make_id() > 0 && c.getCar_model_id() > 0) {
                        uri = Uri.parse(carUrl)
                                .buildUpon()
                                .appendQueryParameter("car_make_id", "" + c.getCar_make_id())
                                .appendQueryParameter("car_model_id", "" + c.getCar_model_id())
                                .appendQueryParameter("year", "" + 0)
                                .appendQueryParameter("tag", c.getTag())
                                .build().toString();
                        request = getHttpRequestFactory().buildPostRequest(new GenericUrl(uri), null).setHeaders(header);
                        content = request.execute().getContent();

                    /*
                    if (content != null) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        IOUtils.copy(content, out);
                        result = out.toString("UTF-8");
                    }*/

                    }

                } else if (c.getId() > 0 && c.getType() == 0) { // update
                    uri = Uri.parse(carUrlID)
                            .buildUpon()
                            .appendQueryParameter("id", "" + c.getId())
                            .appendQueryParameter("car_make_id", "" + c.getCar_make_id())
                            .appendQueryParameter("car_model_id", "" + c.getCar_model_id())
                            .appendQueryParameter("year", "" + 0)
                            .appendQueryParameter("tag", c.getTag())
                            .build().toString();
                    request = getHttpRequestFactory().buildPutRequest(new GenericUrl(uri), null).setHeaders(header);
                    content = request.execute().getContent();

                /*
                if (content != null) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    IOUtils.copy(content, out);
                    result = out.toString("UTF-8");
                }*/

                } else if (c.getId() > 0 && c.getType() == 2) { // deleted
                    uri = Uri.parse(carUrlID)
                            .buildUpon()
                            .appendQueryParameter("id", "" + c.getId())
                            //.appendQueryParameter("car_make_id", "" + c.getCar_make_id())
                            //.appendQueryParameter("car_model_id", "" + c.getCar_model_id())
                            //.appendQueryParameter("year", "" + 0)
                            .build().toString();
                    request = getHttpRequestFactory().buildDeleteRequest(new GenericUrl(uri)).setHeaders(header);
                    content = request.execute().getContent();

                /*
                if (content != null) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    IOUtils.copy(content, out);
                    result = out.toString("UTF-8");
                }*/
                }
            }

        uri = Uri.parse(profileUrl)
                .buildUpon()
                .appendQueryParameter("user[name]", "" + profile.getName())
                .build().toString();

        request = getHttpRequestFactory().buildPutRequest(new GenericUrl(uri), null).setHeaders(header);
        content = request.execute().getContent();

        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }
        Log.d("ProfileResult", "res = " + result);

        //JsonNode json = new ObjectMapper().readTree(result);
        //ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        //ProfileResult res = mapper.readValue(result, getResultType());

        ProfileResult res = LoganSquare.parse(result, ProfileResult.class);
        Log.w("ProfileResult", "end parse json ");
        res.getData().setString(result);

        return res;
    }

}