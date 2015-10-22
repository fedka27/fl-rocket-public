package wash.rocket.xor.rocketwash.requests;

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

import roboguice.util.temp.Ln;
import wash.rocket.xor.rocketwash.model.ProfileResult;

public class ProfileRequest extends GoogleHttpClientSpiceRequest<ProfileResult> {

    private String baseUrl;
    private String session_id;

    public ProfileRequest(String session_id) {
        super(ProfileResult.class);
        this.baseUrl = "http://test.rocketwash.me/v2/profile";
        this.session_id = session_id;
    }

    @Override
    public ProfileResult loadDataFromNetwork() throws IOException {
        Ln.d("ProfileRequest = " + baseUrl);

        HttpHeaders header = new HttpHeaders();
        header.set("X-Rocketwash-Session-Id", session_id);
        HttpRequest request = getHttpRequestFactory().buildGetRequest(new GenericUrl(baseUrl)).setHeaders(header);


        String result = "";
        InputStream content = request.execute().getContent();
        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }

        //JsonNode json = new ObjectMapper().readTree(result);
        //ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ///mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        //ProfileResult res = mapper.readValue(result, getResultType());
        //res.getData().setString(result);
        //return mapper.readValue(result, getResultType());
        //return res;


        Log.d("ProfileRequest", " res = " + result);
        Log.w("ProfileRequest", " start parse json ");

        //ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //WashServiceResult res = mapper.readValue(result, getResultType());

        ProfileResult res = LoganSquare.parse(result, ProfileResult.class);
        res.getData().setString(result);

        Log.w("ProfileRequest", " end parse json ");

        return res;

    }

}