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

import wash.rocket.xor.rocketwash.model.ProfileResult;
import wash.rocket.xor.rocketwash.util.Constants;

public class VerifyPhoneRequest extends GoogleHttpClientSpiceRequest<ProfileResult> {

    private String baseUrl;
    private String pin;
    private String session_id;

    public VerifyPhoneRequest(String pin, String session_id) {
        super(ProfileResult.class);
        this.baseUrl = Constants.URL + "user_actions/verify_phone";
        this.pin = pin;
        this.session_id = session_id;
    }

    @Override
    public ProfileResult loadDataFromNetwork() throws IOException {
        String uri = Uri.parse(baseUrl)
                .buildUpon()
                .appendQueryParameter("pin", pin)
                .build().toString();
        Log.d("loadDataFromNetwork", "uri = " + uri);

        HttpHeaders header = new HttpHeaders();
        header.set("X-Rocketwash-Session-Id", session_id);

        //HttpRequest request = getHttpRequestFactory().buildPostRequest(new GenericUrl(uri), null).setHeaders(header);
        //request.setParser(new JacksonFactory().createJsonObjectParser());
        //return request.execute().parseAs(getResultType());

        HttpRequest request = getHttpRequestFactory().buildPostRequest(new GenericUrl(uri), null).setHeaders(header);
        //request.setParser(new JacksonFactory().createJsonObjectParser());
        // return request.execute().parseAs(getResultType());

        InputStream content = request.execute().getContent();
        String result = "";

        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }

        Log.d("VerifyPhoneRequest", "result = " + result);

        //ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

        //ProfileResult res = mapper.readValue(result, getResultType());
        //res.getData().setString(result);

        ProfileResult res = LoganSquare.parse(result, ProfileResult.class);
        return res;
    }

}
